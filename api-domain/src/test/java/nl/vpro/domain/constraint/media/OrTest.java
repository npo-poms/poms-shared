/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import nl.vpro.domain.media.Channel;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class OrTest {

    @Before
    public void setup() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void testAndBinding() {
        Or in = new Or(new And());
        Or out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:or xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:and/>\n" +
                "</local:or>");

        assertThat(out.getConstraints().get(0)).isInstanceOf(And.class);
    }

    @Test
    public void testOrBinding() {
        Or in = new Or(new Or());
        Or out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:or xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:or/>\n" +
                "</local:or>");

        assertThat(out.getConstraints().get(0)).isInstanceOf(Or.class);
    }

    @Test
    public void testNotBinding() {
        Or in = new Or(new Not());
        Or out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:or xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:not/>\n" +
                "</local:or>");

        assertThat(out.getConstraints().get(0)).isInstanceOf(Not.class);
    }

    @Test
    public void testAvTypeBinding() {
        Or in = new Or(new AvTypeConstraint());
        Or out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:or xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:avType/>\n" +
                "</local:or>");

        assertThat(out.getConstraints().get(0)).isInstanceOf(AvTypeConstraint.class);
    }

    @Test
    public void testAvFileFormatBinding() {
        Or in = new Or(new AvFileFormatConstraint("MP3"));
        Or out = JAXBTestUtil.roundTripAndSimilar(in,
                    "<local:or xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                        "    <media:avFileFormat>MP3</media:avFileFormat>\n" +
                        "</local:or>");

        assertThat(out.getConstraints().get(0)).isInstanceOf(AvFileFormatConstraint.class);
    }

    @Test
    public void testAvFileExtensionBinding() {
        Or in = new Or(new AVFileExtensionConstraint());
        Or out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:or xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:avFileExtension/>\n" +
                "</local:or>");

        assertThat(out.getConstraints().get(0)).isInstanceOf(AVFileExtensionConstraint.class);
    }

    @Test
    public void testChannelBinding() {
        Or in = new Or(new ChannelConstraint(Channel.NED1));
        Or out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:or xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:channel>NED1</media:channel>\n" +
                "</local:or>"
        );

        assertThat(out.getConstraints().get(0)).isInstanceOf(ChannelConstraint.class);
    }


    @Test
    public void testApplyWhenEmpty() {
        Or constraint = new Or();
        assertThat(constraint.test(null)).isFalse();
        assertThat(constraint.testWithReason(null).applies()).isFalse();
        assertThat(constraint.testWithReason(null).getReason()).isEqualTo("Or");
        assertThat(constraint.testWithReason(null).getDescription(Locale.US)).isEqualTo("Never matches because the or predicate has no clauses");

    }

    @Test
    public void testApplyWhenFalse() {
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
    public void testApplyWhenTrue() {
        Or constraint = new Or(
            MediaConstraints.alwaysTrue(),
            MediaConstraints.alwaysFalse()
        );
        assertThat(constraint.test(null)).isTrue();
    }



}
