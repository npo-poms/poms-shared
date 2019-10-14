/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXB;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import nl.vpro.domain.image.ImageType;
import nl.vpro.test.theory.ObjectTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ImageTheoriesTest extends ObjectTest<Image> {

    @DataPoint
    public static Image nullArgument = null;

    @DataPoint
    public static Image emptyFields = new Image();

    @DataPoint
    public static Image withOwner = new Image(OwnerType.BROADCASTER);

    @DataPoint
    public static Image withOwnerAndUri = new Image(OwnerType.BROADCASTER, "urn:vpro:image:1234");

    @DataPoint
    public static Image withOwnerTypeAndUri = new Image(OwnerType.BROADCASTER, ImageType.ICON, "urn:vpro:image:3456");

    @Test
    public void testIsHighlightedOnXmlMarshaling() {
        Image image = new Image();
        Writer writer = new StringWriter();
        JAXB.marshal(image, writer);

        assertThat(image.isHighlighted()).isFalse();
        assertThat(writer.toString()).contains("highlighted=\"false\"");
    }

    @Test
    public void testGetHighlightedWithXmlMarshaling() {
        Image image = new Image();
        Writer writer = new StringWriter();
        image.setHighlighted(true);

        JAXB.marshal(image, writer);

        assertThat(image.isHighlighted()).isTrue();
        assertThat(writer.toString()).contains("highlighted=\"true\"");
    }

    @Test
    public void testCredits() {
        Image image = new Image();
        image.setCredits(" ");
        assertThat(image.getCredits()).isNull();
        image.setCredits("aaa");
        assertThat(image.getCredits()).isEqualTo("aaa");
    }

    @Test
    public void testDate() {
        Image image = new Image();
        image.setDate("");
        assertThat(image.getDate()).isNull();
        image.setDate("2015");
        assertThat(image.getDate()).isEqualTo("2015");
    }

    @Test
    public void testSetHighlighted() {
        Image image = new Image();
        image.setHighlighted(true);
        assertThat(image.isHighlighted()).isTrue();
    }

    @Test
    public void testSetHighlightedWithNull() {
        Image image = new Image();
        image.setHighlighted(null);
        assertThat(image.isHighlighted()).isFalse();
    }
}
