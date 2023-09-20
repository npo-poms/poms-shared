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
public class ImagesTest {
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
    public void testGetImageLocationOnMissingSystemProperty() {
        String location = Images.getImageLocation(new Image(), null);
        assertThat(location).isNull();
    }

    @Test
    public void testGetImageLocationOnNullArgument() {
        assertThatThrownBy(() -> {
            System.setProperty(IMAGE_SERVER_BASE_URL_PROPERTY, "http://domain.com/");
            Images.getImageLocation(null, null);
        }).isInstanceOf(NullPointerException.class);
    }

    @Test
    @Disabled
    public void testGetImageLocationOnEmptyURI() {
        assertThatThrownBy(() -> {

            System.setProperty(IMAGE_SERVER_BASE_URL_PROPERTY, "http://domain.com/");
            Images.getImageLocation(new Image(), "jpg");
        }).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testGetImageLocationOnNullExtension() {
        System.setProperty(IMAGE_SERVER_BASE_URL_PROPERTY, "http://domain.com/");
        String location = Images.getImageLocation(new Image(OwnerType.BROADCASTER, "urn:vpro:image:12345"), null);
        assertThat(location).isEqualTo("http://domain.com/12345");
    }

    @Test
    public void testGetImageLocationOnInValidURI() {
        System.setProperty(IMAGE_SERVER_BASE_URL_PROPERTY, "http://domain.com/");
        String location = Images.getImageLocation(new Image(OwnerType.BROADCASTER, "urn:vpro:image:123aa"), "jpg");
        assertThat(location).isNull();
    }

    @Test
    public void testGetImageLocationWhenValid() {
        System.setProperty(IMAGE_SERVER_BASE_URL_PROPERTY, "http://domain.com/");
        String location = Images.getImageLocation(new Image(OwnerType.BROADCASTER, "urn:vpro:image:12345"), "jpg");
        assertThat(location).isEqualTo("http://domain.com/12345.jpg");
    }

    @Test
    public void testGetImageLocationWithConversion() {
        System.setProperty(IMAGE_SERVER_BASE_URL_PROPERTY, "http://domain.com/");
        String location = Images.getImageLocation(new Image(OwnerType.BROADCASTER, "urn:vpro:image:12345"), "jpg", "s350");
        assertThat(location).isEqualTo("http://domain.com/s350/12345.jpg");
    }
}
