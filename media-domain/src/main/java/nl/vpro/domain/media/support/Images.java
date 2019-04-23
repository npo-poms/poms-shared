/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

/**
 * See https://jira.vpro.nl/browse/MSE-1212
 *
 * @author Roelof Jan Koekoek
 * @since 1.6
 * @deprecated Used {@link ImageBackendService}
 */
@Slf4j
@Deprecated
public class Images {

        public static final String IMAGE_SERVER_BASE_URL_PROPERTY = "image.server.baseUrl";



    /**
     * Resolves an web location for images. Relies on a system property #IMAGE_SERVER_BASE_URL_PROPERTY to
     * obtain a base url for an image host.
     *
     * @return valid url string or null if it can't resolve a location
     * @throws NullPointerException on null arguments or null imageUri
     */
    public static String getImageLocation(Image image, String fileExtension, String... conversions) {
        ImageBackendService instance = ImageBackendServiceHolder.getInstance();
        return instance.getImageLocation(instance.getId(image), fileExtension, conversions);


    }
}
