/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
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
class NotTest {

    @BeforeEach
    public void setup() {
        Locale.setDefault(Locale.US);
    }

    @Test
    void andBinding() {
        Not in = new Not(new And());
        Not out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:not xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:and/>
                </local:not>""");

        assertThat(out.getConstraint()).isInstanceOf(And.class);
    }

    @Test
    void orBinding() {
        Not in = new Not(new Or());
        Not out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:not xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:or/>
                </local:not>""");

        assertThat(out.getConstraint()).isInstanceOf(Or.class);
    }

    @Test
    void notBinding() {
        Not in = new Not(new Not());
        Not out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:not xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:not/>
                </local:not>""");

        assertThat(out.getConstraint()).isInstanceOf(Not.class);
    }

    @Test
    void avTypeBinding() {
        Not in = new Not(new AvTypeConstraint());
        Not out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:not xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:avType/>
                </local:not>""");

        assertThat(out.getConstraint()).isInstanceOf(AvTypeConstraint.class);
    }

    @Test
    void avFileFormatBinding() {
        Not in = new Not(new AvFileFormatConstraint());
        Not out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:not xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:avFileFormat/>
                </local:not>""");

        assertThat(out.getConstraint()).isInstanceOf(AvFileFormatConstraint.class);
    }

    @Test
    void avFileExtensionBinding() {
        Not in = new Not(new AVFileExtensionConstraint());
        Not out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:not xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:avFileExtension/>
                </local:not>""");

        assertThat(out.getConstraint()).isInstanceOf(AVFileExtensionConstraint.class);
    }


    @Test
    void applyWhenTrue() {
        Not not = new Not(MediaConstraints.alwaysFalse());
        assertThat(not.test(new Program())).isTrue();
        assertThat(not.testWithReason(new Program()).applies()).isTrue();
        assertThat(not.testWithReason(new Program()).getReason()).isEqualTo("Not");
        assertThat(not.testWithReason(new Program("mid")).getDescription(Locale.US)).isEqualTo("The value 'Program{mid=\"mid\", title=<no title>}' does not match Never matches");
    }

    @Test
    void applyWhenFalse() {
        Not not = new Not(MediaConstraints.alwaysTrue());
        assertThat(not.test(new Program())).isFalse();
        assertThat(not.testWithReason(new Program()).applies()).isFalse();
        assertThat(not.testWithReason(new Program()).getReason()).isEqualTo("Not");
        assertThat(not.testWithReason(new Program()).getDescription(Locale.US)).isEqualTo("(Always matches should not match)");
    }


    @Test
    void applyWithNull() {
        // can happen if profile contain not yet supported tags
        assertThat(new Not(null).test(new Program())).isTrue();
    }


}
