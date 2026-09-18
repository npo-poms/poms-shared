/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.Channel;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
class OrTest {

    @BeforeEach
    public void setup() {
        Locale.setDefault(Locale.US);
    }

    @Test
    void andBinding() {
        Or in = new Or(new And());
        Or out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:or xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:and/>
                </local:or>""");

        assertThat(out.getConstraints().get(0)).isInstanceOf(And.class);
    }

    @Test
    void orBinding() {
        Or in = new Or(new Or());
        Or out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:or xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:or/>
                </local:or>""");

        assertThat(out.getConstraints().get(0)).isInstanceOf(Or.class);
    }

    @Test
    void notBinding() {
        Or in = new Or(new Not());
        Or out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:or xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:not/>
                </local:or>""");

        assertThat(out.getConstraints().get(0)).isInstanceOf(Not.class);
    }

    @Test
    void avTypeBinding() {
        Or in = new Or(new AvTypeConstraint());
        Or out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:or xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:avType/>
                </local:or>""");

        assertThat(out.getConstraints().get(0)).isInstanceOf(AvTypeConstraint.class);
    }

    @Test
    void avFileFormatBinding() {
        Or in = new Or(new AvFileFormatConstraint("MP3"));
        Or out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:or xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:avFileFormat>MP3</media:avFileFormat>
                </local:or>""");

        assertThat(out.getConstraints().get(0)).isInstanceOf(AvFileFormatConstraint.class);
    }

    @Test
    void avFileExtensionBinding() {
        Or in = new Or(new AVFileExtensionConstraint());
        Or out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:or xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:avFileExtension/>
                </local:or>""");

        assertThat(out.getConstraints().get(0)).isInstanceOf(AVFileExtensionConstraint.class);
    }

    @Test
    void channelBinding() {
        Or in = new Or(new ChannelConstraint(Channel.NED1));
        Or out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:or xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:channel>NED1</media:channel>
                </local:or>"""
        );

        assertThat(out.getConstraints().get(0)).isInstanceOf(ChannelConstraint.class);
    }


    @Test
    void applyWhenEmpty() {
        Or constraint = new Or();
        assertThat(constraint.test(null)).isFalse();
        assertThat(constraint.testWithReason(null).applies()).isFalse();
        assertThat(constraint.testWithReason(null).getReason()).isEqualTo("Or");
        assertThat(constraint.testWithReason(null).getDescription(Locale.US)).isEqualTo("Never matches because the or predicate has no clauses");

    }

    @Test
    void applyWhenFalse() {
        Or constraint = new Or(
            MediaConstraints.alwaysFalse(),
            MediaConstraints.alwaysFalse()
        );
        assertThat(constraint.test(null)).isFalse();

        assertThat(constraint.testWithReason(null).applies()).isFalse();
        assertThat(constraint.testWithReason(null).getReason()).isEqualTo("Or");
        assertThat(constraint.testWithReason(null).getDescription(Locale.US)).isEqualTo("(Never matches or Never matches should match)");

    }

    @Test
    void applyWhenTrue() {
        Or constraint = new Or(
            MediaConstraints.alwaysTrue(),
            MediaConstraints.alwaysFalse()
        );
        assertThat(constraint.test(null)).isTrue();
    }



}
