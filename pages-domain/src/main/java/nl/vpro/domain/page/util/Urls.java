/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.util;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.page.Portal;
import nl.vpro.domain.page.Section;

/**
 * See http://stackoverflow.com/questions/2993649/how-to-normalize-a-url-in-java with some additions
 */
@Slf4j
public class Urls {

    private Urls() {
    }

    @Nullable
    public static String normalize(@Nullable final String taintedURI) {
        if (taintedURI == null) {
            return null;
        }
        URI uri;
        try {
            String noWhiteSpace =
                    taintedURI
                    .trim()
                    .replace(" ", "%20")
                    .replace("\b", "%08")
                    .replace("\t", "%09")
                    .replace("\r", "%0A")
                    .replace("\n", "%0D");

            uri = new URI(noWhiteSpace).normalize();
        } catch(URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String path = uri.getRawPath();
        if(path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        final SortedMap<String, String> params = createParameterMap(uri.getQuery());

        final String ref = uri.getFragment();
        if(ref != null && ref.startsWith("!/")) {
            params.put("_escaped_fragment_", ref.substring(1));
        }

        final int port = uri.getPort();
        final String queryString;

        if(params.size() > 0) {
            // Some params are only relevant for user tracking, so remove the most commons ones.
            params.keySet().removeIf(key -> key.startsWith("utm_") || key.contains("session"));
            queryString = "?" + canonicalize(params);
        } else {
            queryString = "";
        }

        return uri.getScheme()
                + "://" + uri.getHost()
                + (port == -1 || (port == 80 && "http".equals(uri.getScheme())) || (port == 443 && "https".equals(uri.getScheme())) ? "" : ":" + port)
                + path
                + queryString
                + (uri.getFragment() != null && !uri.getFragment().isEmpty() ? "#" + uri.getFragment() : "");
    }

    public static Portal portalFrom(String id, String normUrl) {
        int pathIndex = normUrl.indexOf('/', 8);
        String portalUrl = pathIndex > 8 ? normUrl.substring(0, pathIndex) : normUrl;
        Portal portal = new Portal(id, portalUrl, portalUrl);

        if(normUrl.length() > portalUrl.length() + 1) {
            int sectionEndIndex = normUrl.indexOf('/', portalUrl.length() + 1);
            if(sectionEndIndex > portalUrl.length() + 1) {
                String section = normUrl.substring(portalUrl.length(), sectionEndIndex);
                portal.setSection(new Section(section, section));
            }
        }

        return portal;
    }

    /**
     * Takes a query string, separates the constituent name-value pairs, and
     * stores them in a SortedMap ordered by lexicographical order.
     *
     * @return Null if there is no query string.
     */
    private static SortedMap<String, String> createParameterMap(final String queryString) {
        if(queryString == null || queryString.isEmpty()) {
            return new TreeMap<>();
        }

        final String[] pairs = queryString.split("&");
        final Map<String, String> params = new HashMap<>(pairs.length);

        for(final String pair : pairs) {
            if(pair.length() < 1) {
                continue;
            }

            String[] tokens = pair.split("=", 2);
            for(int j = 0; j < tokens.length; j++) {
                try {
                    tokens[j] = URLDecoder.decode(tokens[j], "UTF-8");
                } catch(UnsupportedEncodingException ex) {
                    // shouldnt happen
                    log.error(ex.getMessage(), ex);
                }
            }
            switch(tokens.length) {
                case 1: {
                    if(pair.charAt(0) == '=') {
                        params.put("", tokens[0]);
                    } else {
                        params.put(tokens[0], "");
                    }
                    break;
                }
                case 2: {
                    params.put(tokens[0], tokens[1]);
                    break;
                }
            }
        }

        return new TreeMap<>(params);
    }

    /**
     * Canonicalize the query string.
     *
     * @param sortedParamMap Parameter name-value pairs in lexicographical order.
     * @return Canonical form of query string.
     */
    private static String canonicalize(final SortedMap<String, String> sortedParamMap) {
        if(sortedParamMap == null || sortedParamMap.isEmpty()) {
            return "";
        }

        final StringBuilder sb = new StringBuilder(350);
        final Iterator<Map.Entry<String, String>> iter = sortedParamMap.entrySet().iterator();

        while(iter.hasNext()) {
            final Map.Entry<String, String> pair = iter.next();
            sb.append(percentEncodeRfc3986(pair.getKey()));
            if(!pair.getValue().isEmpty()) {
                sb.append('=');
                sb.append(percentEncodeRfc3986(pair.getValue()));
            }
            if(iter.hasNext()) {
                sb.append('&');
            }
        }

        return sb.toString();
    }

    /**
     * Percent-encode values according the RFC 3986. The built-in Java URLEncoder does not encode
     * according to the RFC, so we make the extra replacements.
     *
     * @param string Decoded string.
     * @return Encoded string per RFC 3986.
     */
    private static String percentEncodeRfc3986(final String string) {
        try {
            return URLEncoder.encode(string, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
        } catch(UnsupportedEncodingException e) {
            return string;
        }
    }
}
