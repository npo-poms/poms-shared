/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.handler;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import nl.vpro.domain.media.Location;
import nl.vpro.media.odi.LocationProducer;
import nl.vpro.media.odi.util.LocationResult;

/**
 * See <a href="http://hosting.omroep.nl/sterretje-cluster:content-hosting#hotlink_bescherming">Hotlink bescherming</a>
 */
@ToString(exclude = "streamAAPISecret")
@Slf4j
public class DeeplinkProtectionLocationHandler implements LocationProducer {
    private static final HexBinaryAdapter hexBinaryAdapter = new HexBinaryAdapter();

    private static final String STREAM_API_SCHEME_PREFIX = "odis+";

    private String streamAAPISecret;

    @Override
    public int score(Location location, String... pubOptions) {
        String programUrl = location.getProgramUrl();
        if (programUrl.startsWith(STREAM_API_SCHEME_PREFIX) || programUrl.contains("/protected/")) {
            return 2;
        }
        return 0;
    }

    @Override
    public LocationResult produce(Location location, HttpServletRequest request, String... pubOptions) {

        String programUrl = location.getProgramUrl();

        String odiUrl = null;
        try {
            if(programUrl.startsWith(STREAM_API_SCHEME_PREFIX)) {
                programUrl = programUrl.substring(STREAM_API_SCHEME_PREFIX.length());
            }

            URL url = new URL(programUrl);
            String server = url.getProtocol() + "://" + url.getHost() + (url.getPort() < 0 ? "" : ":" + url.getPort());
            String path = url.getPath();
            String timehex = String.format("%08x", ((new Date()).getTime() / 1000));
            String token = md5(streamAAPISecret + path + timehex);
            odiUrl = String.format("%s/secure/%s/%s%s?md5=%s&t=%s",
                server, token, timehex, path, token, timehex);
        } catch(MalformedURLException e) {
            log.error("Invalid url " + programUrl + " " + e.getMessage());
        }

        if(odiUrl != null) {
            return new LocationResult(location.getAvFileFormat(), location.getBitrate(), odiUrl, location.getUrn());
        }

        return null;
    }

    private static String md5(String data) {
        String digestHex = null;

        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] digest = digester.digest(data.getBytes(StandardCharsets.UTF_8));
            digestHex = hexBinaryAdapter.marshal(digest).toLowerCase();
        } catch(NoSuchAlgorithmException e) {
            log.error("Can't get MD5 " + e.getMessage());
        }
        return digestHex;
    }

    public void setStreamAPISecret(String streamAAPISecret) {
        this.streamAAPISecret = streamAAPISecret;
    }
}
