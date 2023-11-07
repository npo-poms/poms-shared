/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXB;

import org.junit.jupiter.api.Test;
import org.meeuw.theories.BasicObjectTheory;

import nl.vpro.domain.image.ImageType;

import static org.assertj.core.api.Assertions.assertThat;

public class ImageTheoriesTest implements BasicObjectTheory<Image> {


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

    @Override
    public Arbitrary<? extends Image> datapoints() {
                Image emptyFields = new Image();
        Image withOwner = new Image(OwnerType.BROADCASTER);
        Image withOwnerAndUri = new Image(OwnerType.BROADCASTER, "urn:vpro:image:1234");

        Image withOwnerTypeAndUri = new Image(OwnerType.BROADCASTER, ImageType.ICON, "urn:vpro:image:3456");
        return Arbitraries.of(
            emptyFields,
            withOwner,
            withOwnerAndUri,
            withOwnerTypeAndUri
        );
    }
}
