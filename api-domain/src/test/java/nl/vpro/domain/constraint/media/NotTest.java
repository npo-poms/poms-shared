/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.Program;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class NotTest {

    @BeforeEach
    public void setup() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void testAndBinding() {
        Not in = new Not(new And());
        Not out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:not xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:and/>\n" +
                "</local:not>");

        assertThat(out.getConstraint()).isInstanceOf(And.class);
    }

    @Test
    public void testOrBinding() {
        Not in = new Not(new Or());
        Not out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:not xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:or/>\n" +
                "</local:not>");

        assertThat(out.getConstraint()).isInstanceOf(Or.class);
    }

    @Test
    public void testNotBinding() {
        Not in = new Not(new Not());
        Not out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:not xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:not/>\n" +
                "</local:not>");

        assertThat(out.getConstraint()).isInstanceOf(Not.class);
    }

    @Test
    public void testAvTypeBinding() {
        Not in = new Not(new AvTypeConstraint());
        Not out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:not xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:avType/>\n" +
                "</local:not>");

        assertThat(out.getConstraint()).isInstanceOf(AvTypeConstraint.class);
    }

    @Test
    public void testAvFileFormatBinding() {
        Not in = new Not(new AvFileFormatConstraint());
        Not out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:not xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:avFileFormat/>\n" +
                "</local:not>");

        assertThat(out.getConstraint()).isInstanceOf(AvFileFormatConstraint.class);
    }

    @Test
    public void testAvFileExtensionBinding() {
        Not in = new Not(new AVFileExtensionConstraint());
        Not out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:not xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
                "    <media:avFileExtension/>\n" +
                "</local:not>");

        assertThat(out.getConstraint()).isInstanceOf(AVFileExtensionConstraint.class);
    }


    @Test
    public void testApplyWhenTrue() {
        Not not = new Not(MediaConstraints.alwaysFalse());
        assertThat(not.test(new Program())).isTrue();
        assertThat(not.testWithReason(new Program()).applies()).isTrue();
        assertThat(not.testWithReason(new Program()).getReason()).isEqualTo("Not");
        assertThat(not.testWithReason(new Program()).getDescription(Locale.US)).isEqualTo("The value 'Program{mid=null, title=null (new)}' does not match Never matches");
    }

    @Test
    public void testApplyWhenFalse() {
        Not not = new Not(MediaConstraints.alwaysTrue());
        assertThat(not.test(new Program())).isFalse();
        assertThat(not.testWithReason(new Program()).applies()).isFalse();
        assertThat(not.testWithReason(new Program()).getReason()).isEqualTo("Not");
        assertThat(not.testWithReason(new Program()).getDescription(Locale.US)).isEqualTo("(Always matches should not match)");
    }


    @Test
    public void testApplyWithNull() {
        // can happen if profile contain not yet supported tags
        assertThat(new Not(null).test(new Program())).isTrue();
    }


}
