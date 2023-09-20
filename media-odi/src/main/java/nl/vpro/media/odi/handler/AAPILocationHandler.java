/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.handler;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import nl.vpro.domain.media.Location;
import nl.vpro.media.odi.LocationProducer;
import nl.vpro.media.odi.util.InetAddressUtil;
import nl.vpro.media.odi.util.LocationResult;

/**
 * TODO
 */
@Slf4j
@ToString
public class AAPILocationHandler implements LocationProducer {

    private static final String M3U8 = "m3u8";

    private static final String F4M = "f4m";

    private static final List<String> FILE_TYPES = Arrays.asList(M3U8, F4M);

    private static final String AWO_SCHEME_PREFIX = "odiw+";

    private String aapiServer = "http://aapi.omroep.nl/ondemand";

    /**
     * Does this handler support the location given the pubOptions supported
     *
     * @param location location with a programUrl
     * @param pubOptions ordered list with puboptions which should contain one of [m3u8, f4m]
     */
    @Override
    public int score(Location location, String... pubOptions) {
        if(location.getProgramUrl().startsWith(AWO_SCHEME_PREFIX)) {
            for(String pubOption : pubOptions) {
                if(FILE_TYPES.contains(pubOption.toLowerCase())) {
                    return 2;
                }
            }
        }
        return 0;
    }

    @Override
    public LocationResult produce(Location location, HttpServletRequest request, String... pubOptions) {

        String pubOption = null;
        for(String p : pubOptions) {
            if(FILE_TYPES.contains(p.toLowerCase())) {
                pubOption = p;
                break;
            }
        }

        if(pubOption == null) {
            return null;
        }

        String programUrl = location.getProgramUrl().substring(AWO_SCHEME_PREFIX.length());

        String odiUrl = null;
        try {
            URL pomsUrl = URI.create(programUrl).toURL();

            String file = FilenameUtils.getBaseName(pomsUrl.getPath());
            String stream = pomsUrl.getPath() + "/" + file + "." + pubOption;
            String url = getUrlFromAAPI(getClientHost(request), stream);

            String callback = request.getParameter("callback");

            odiUrl = addDefaultParameters(url, pubOption, callback);
        } catch(MalformedURLException mue) {
            log.error("Invalid url " + programUrl + " : " + mue.getMessage());
        } catch(ClientProtocolException e) {
            log.error("Can't execute request, invalid protocol: " + e.getMessage());
        } catch(URISyntaxException e) {
            log.error("Can't encode url for response: " + e.getMessage());
        } catch(UnsupportedEncodingException e) {
            log.error("Can't encode values for aapi request: " + e.getMessage());
        } catch(IOException e) {
            log.error("I/O Exception: " + e.getMessage());
        }

        if(odiUrl != null) {
            return new LocationResult(location.getAvFileFormat(), location.getBitrate(), odiUrl, location.getUrn());
        }

        return null;
    }


    protected String getClientHost(HttpServletRequest request) {
        String ip = InetAddressUtil.getClientHost(request);
        if (ip == null) {
            ip = "rs.vpro.nl";
        }
        return ip;
    }


    private String getUrlFromAAPI(String ip, String stream) throws IOException {
        List<NameValuePair> valuePairs = new ArrayList<>();
        valuePairs.add(new BasicNameValuePair("ip", ip));
        valuePairs.add(new BasicNameValuePair("stream", stream));
        return httpPost(aapiServer, valuePairs);

    }


    private String httpPost(String server, List<NameValuePair> valuePairs) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(server);
            httpPost.setEntity(new UrlEncodedFormEntity(valuePairs));
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                StatusLine status = response.getStatusLine();
                String result = EntityUtils.toString(response.getEntity());
                if (status.getStatusCode() == 200) {
                    return result;
                } else {
                    throw new RuntimeException(server+ ": " + status.getStatusCode() + " " + status.getReasonPhrase() + ": " + result);
                }
            }
        }
    }


    private String addDefaultParameters(String url, String type, String callback) throws URISyntaxException, MalformedURLException {
        URI uri = URI.create(url);
        List<NameValuePair> params = URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);
        params.add(new BasicNameValuePair("protection", "url"));
        if(type != null && F4M.equals(type)) {
            params.add(new BasicNameValuePair("type", "jsonp"));
            params.add(new BasicNameValuePair("callback", callback));
        } else {
            params.add(new BasicNameValuePair("type", "http"));
        }
        URIBuilder responseUri = new URIBuilder(uri);

        for (NameValuePair param : params) {
            responseUri.addParameter(param.getName(), param.getValue());
        }
        return responseUri.build().toURL().toExternalForm();
    }

    public void setAAPIServer(String aapiServer) {
        this.aapiServer = aapiServer;
    }
}
