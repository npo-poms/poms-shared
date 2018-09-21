/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

/**
 * See https://jira.vpro.nl/browse/MSE-1212
 *
 * @author Roelof Jan Koekoek
 * @since 1.6
 */
@Slf4j
public class Images {
    
    public static final String IMAGE_SERVER_BASE_URL_PROPERTY = "image.server.baseUrl";

    private static String imageHost;

    public static String getImageHost() {
        String fromSystem = System.getProperty(IMAGE_SERVER_BASE_URL_PROPERTY);
        if (fromSystem != null) {
            return  fromSystem;
        }
        return imageHost;
    }
    public static void setImageHost(String host) {
        imageHost = host;
    }

    /**
     * Resolves an web location for images. Relies on a system property #IMAGE_SERVER_BASE_URL_PROPERTY to
     * obtain a base url for an image host.
     *
     * @return valid url string or null if it can't resolve a location
     * @throws NullPointerException on null arguments or null imageUri
     */
    public static String getImageLocation(Image image, String fileExtension, String... conversions) {
        String imageHost = getImageHost();
        if (imageHost == null) {
            log.warn("Property: {} not set. Can't determine a base url to an image host, producing data with empty image URLs", IMAGE_SERVER_BASE_URL_PROPERTY);
            return null;
        }


        if(fileExtension == null) {
            throw new NullPointerException("Should provide a file extension, not null");
        }

        Matcher matcher = Image.SERVER_URI_PATTERN.matcher(image.getImageUri());
        if(!matcher.find()) {
            return null;
        }

        String id = matcher.group(1);

        if(StringUtils.isEmpty(id)) {
            return null;
        }
        StringBuilder builder = new StringBuilder(imageHost);
        for (String conversion : conversions) {
            builder.append(conversion);
            builder.append('/');
        }
        builder.append(id).append('.').append(fileExtension);
        return builder.toString();
    }
}
