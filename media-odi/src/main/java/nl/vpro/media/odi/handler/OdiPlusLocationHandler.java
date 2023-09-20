/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.handler;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import nl.vpro.domain.media.Location;
import nl.vpro.media.odi.LocationProducer;
import nl.vpro.media.odi.util.InetAddressUtil;
import nl.vpro.media.odi.util.LocationResult;

/**
 * TODO
 */
@ToString
@Slf4j
@Deprecated
public class OdiPlusLocationHandler implements LocationProducer {


    private static final String ODI_PLUS_SCHEME_PREFIX = "odip+";

    private String odiPlusBaseUrl = "http://odiplus.omroep.nl/odi";

    private String odiPlusApplication;

    @Override
    public int score(Location location, String... pubOptions) {
        if (location.getProgramUrl().startsWith(ODI_PLUS_SCHEME_PREFIX)) {
            return 2;
        } else {
            return 0;
        }
    }

    @Override
    public LocationResult produce(Location location, HttpServletRequest request, String... pubOptions) {


        String programUrl = location.getProgramUrl().substring(ODI_PLUS_SCHEME_PREFIX.length());
        String ip = InetAddressUtil.getClientHost(request);
        try {
            URL pomsUrl = new URL(programUrl);
            String[] pieces = pomsUrl.getPath().split("/");
            if(pieces.length == 4) {
                String prefix = pieces[1];
                String publicationoption = pieces[2];
                String prid = pieces[3];
                String odiUrl = getUrlFromODI(ip, prefix, publicationoption, prid);
                return new LocationResult(location.getAvFileFormat(), location.getBitrate(), odiUrl, location.getUrn());
            }
        } catch(MalformedURLException e) {
            log.warn("Invalid programUrl " + programUrl + " " + e.getMessage());
        } catch(IOException e) {
            log.warn("Error while obtaining an ODI url for " + programUrl + ". Root cause: " + e.getMessage());
        }

        return null;
    }


    public void setOdiPlusBaseUrl(String odiPlusBaseUrl) {
        this.odiPlusBaseUrl = odiPlusBaseUrl;
    }

    public void setOdiPlusApplication(String odiPlusApplication) {
        this.odiPlusApplication = odiPlusApplication;
    }

    private String getUrlFromODI(final String ip,
                                 final String prefix,
                                 final String publicationOption,
                                 final String prid) throws IOException {

        final List<NameValuePair> valuePairs = new ArrayList<>();
        addValuePair(valuePairs, "ip", ip);
        addValuePair(valuePairs, "prefix", prefix);
        addValuePair(valuePairs, "application", odiPlusApplication);
        addValuePair(valuePairs, "publicationoption", publicationOption);
        addValuePair(valuePairs, "prid", prid);

        HttpPost httpPost = new HttpPost(odiPlusBaseUrl);
        httpPost.setEntity(new UrlEncodedFormEntity(valuePairs));

        try (DefaultHttpClient httpclient = new DefaultHttpClient()) {
            HttpResponse response = httpclient.execute(httpPost);
            response.getStatusLine();
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        }
    }

    private void addValuePair(List<NameValuePair> pairs, String name, String value) {
        pairs.add(new BasicNameValuePair(name, value));
    }
}
