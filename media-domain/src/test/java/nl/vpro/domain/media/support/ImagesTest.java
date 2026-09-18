/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Isolated;

import static nl.vpro.domain.media.support.ImageUrlServiceHolder.IMAGE_SERVER_BASE_URL_PROPERTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Roelof Jan Koekoek
 * @since 1.6
 */
@Deprecated
@Isolated
class ImagesTest {
    @BeforeEach
    public void init() {
        System.clearProperty(IMAGE_SERVER_BASE_URL_PROPERTY);
        ImageUrlServiceHolder.setInstance(() -> System.getProperty(IMAGE_SERVER_BASE_URL_PROPERTY));
    }

    @AfterAll
    public static void shutdown() {
        System.clearProperty(IMAGE_SERVER_BASE_URL_PROPERTY);

    }

    @Test
    void getImageLocationOnMissingSystemProperty() {
        String location = Images.getImageLocation(new Image(), null);
        assertThat(location).isNull();
    }

    @Test
    void getImageLocationOnNullArgument() {
        assertThatThrownBy(() -> {
            System.setProperty(IMAGE_SERVER_BASE_URL_PROPERTY, "http://domain.com/");
            Images.getImageLocation(null, null);
        }).isInstanceOf(NullPointerException.class);
    }

    @Test
    @Disabled
    void getImageLocationOnEmptyURI() {
        assertThatThrownBy(() -> {

            System.setProperty(IMAGE_SERVER_BASE_URL_PROPERTY, "http://domain.com/");
            Images.getImageLocation(new Image(), "jpg");
        }).isInstanceOf(NullPointerException.class);
    }

    @Test
    void getImageLocationOnNullExtension() {
        System.setProperty(IMAGE_SERVER_BASE_URL_PROPERTY, "http://domain.com/");
        String location = Images.getImageLocation(new Image(OwnerType.BROADCASTER, "urn:vpro:image:12345"), null);
        assertThat(location).isEqualTo("http://domain.com/12345");
    }

    @Test
    void getImageLocationOnInValidURI() {
        System.setProperty(IMAGE_SERVER_BASE_URL_PROPERTY, "http://domain.com/");
        String location = Images.getImageLocation(new Image(OwnerType.BROADCASTER, "urn:vpro:image:123aa"), "jpg");
        assertThat(location).isNull();
    }

    @Test
    void getImageLocationWhenValid() {
        System.setProperty(IMAGE_SERVER_BASE_URL_PROPERTY, "http://domain.com/");
        String location = Images.getImageLocation(new Image(OwnerType.BROADCASTER, "urn:vpro:image:12345"), "jpg");
        assertThat(location).isEqualTo("http://domain.com/12345.jpg");
    }

    @Test
    void getImageLocationWithConversion() {
        System.setProperty(IMAGE_SERVER_BASE_URL_PROPERTY, "http://domain.com/");
        String location = Images.getImageLocation(new Image(OwnerType.BROADCASTER, "urn:vpro:image:12345"), "jpg", "s350");
        assertThat(location).isEqualTo("http://domain.com/s350/12345.jpg");
    }
}
