/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.handler;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.media.Location;
import nl.vpro.media.odi.LocationHandler;
import nl.vpro.media.odi.util.LocationResult;

@Slf4j
public class OdiLocationHandler implements LocationHandler {
    private static HexBinaryAdapter hexBinaryAdapter = new HexBinaryAdapter();

    private static String ODI_SCHEME_PREFIX = "odi+";

    private String odiBaseUrl = "http://odi.omroep.nl/";

    private String odiSecret;

    private String odiApplication;

    @Override
    public boolean supports(Location location, String... pubOptions) {
        return location.getProgramUrl().startsWith(ODI_SCHEME_PREFIX);
    }

    @Override
    public LocationResult handle(Location location, HttpServletRequest request, String... pubOptions) {
        if(!supports(location)) {
            return null;
        }
        String programUrl = location.getProgramUrl().substring(ODI_SCHEME_PREFIX.length());
        try {
            URL pomsUrl = new URL(programUrl);
            String[] pieces = pomsUrl.getPath().split("/");
            if(pieces.length == 4) {
                String prefix = pieces[1];
                String publicationoption = pieces[2];
                String prid = pieces[3];

                String timehex = String.format("%08x", ((new Date()).getTime() / 1000));
                String token = md5(odiSecret + prefix + odiApplication + publicationoption + prid + timehex);
                String odiUrl =
                    StringUtils.join(new String[]{odiBaseUrl, prefix, odiApplication, publicationoption, token, timehex, prid}, "/");
                odiUrl += "?type=http";
                return new LocationResult(location.getAvFileFormat(), location.getBitrate(), odiUrl);

            }
        } catch(MalformedURLException mue) {
            log.error("Invalid url " + programUrl + " : " + mue.getMessage());
        }
        return null;
    }


    private static String md5(String data) {
        String digestHex = null;

        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] digest = digester.digest(data.getBytes("UTF8"));
            digestHex = hexBinaryAdapter.marshal(digest).toLowerCase();
        } catch(NoSuchAlgorithmException e) {
            log.error("Can't get MD5 " + e.getMessage());
        } catch(UnsupportedEncodingException e) {
            log.error("Character encoding UTF8 not supported " + e.getMessage());
        }
        return digestHex;
    }

    public void setOdiBaseUrl(String odiBaseUrl) {
        this.odiBaseUrl = odiBaseUrl;
    }

    public void setOdiApplication(String odiApplication) {
        this.odiApplication = odiApplication;
    }

    public void setOdiSecret(String odiSecret) {
        this.odiSecret = odiSecret;
    }
}
