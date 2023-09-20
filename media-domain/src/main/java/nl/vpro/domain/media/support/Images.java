/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * See https://jira.vpro.nl/browse/MSE-1212
 *
 * @author Roelof Jan Koekoek
 * @since 1.6
 * @deprecated Used {@link ImageUrlService}
 */
@Slf4j
@Deprecated
public class Images {

    public static final String IMAGE_SERVER_BASE_URL_PROPERTY = ImageUrlServiceHolder.IMAGE_SERVER_BASE_URL_PROPERTY;

    private Images() {
    }

    /**
     * Resolves a web location for images. Relies on a system property #IMAGE_SERVER_BASE_URL_PROPERTY to
     * obtain a base url for an image host.
     *
     * @return valid url string or null if it can't resolve a location
     * @throws NullPointerException on null arguments or null imageUri
     */
    public static String getImageLocation(@NonNull Image image, String fileExtension, String... conversions) {
        ImageUrlService instance = ImageUrlServiceHolder.getInstance();
        Long id = instance.getId(image);
        if (id == null) {
            return null;
        }
        return instance.getImageLocation(id, fileExtension, conversions);
    }
}
