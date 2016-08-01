/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.handler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.domain.media.Location;
import nl.vpro.media.odi.LocationHandler;
import nl.vpro.media.odi.util.InetAddressUtil;
import nl.vpro.media.odi.util.LocationResult;

public class OdiPlusLocationHandler implements LocationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(OdiLocationHandler.class);

    private static String ODI_PLUS_SCHEME_PREFIX = "odip+";

    private String odiPlusBaseUrl = "http://odiplus.omroep.nl/odi";

    private String odiPlusApplication;

    @Override
    public boolean supports(Location location, String... pubOptions) {
        return location.getProgramUrl().startsWith(ODI_PLUS_SCHEME_PREFIX);
    }

    @Override
    public LocationResult handle(Location location, HttpServletRequest request, String... pubOptions) {

        if(!supports(location)) {
            return null;
        }

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
                return new LocationResult(location.getAvFileFormat(), location.getBitrate(), odiUrl);
            }
        } catch(MalformedURLException e) {
            LOG.warn("Invalid programUrl " + programUrl + " " + e.getMessage());
        } catch(IOException e) {
            LOG.warn("Error while obtaining an ODI url for " + programUrl + ". Root cause: " + e.getMessage());
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

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(httpPost);
        response.getStatusLine();
        HttpEntity entity = response.getEntity();

        return EntityUtils.toString(entity);
    }

    private void addValuePair(List<NameValuePair> pairs, String name, String value) {
        pairs.add(new BasicNameValuePair(name, value));
    }
}
