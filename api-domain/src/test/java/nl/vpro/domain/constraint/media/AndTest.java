/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import javax.xml.bind.JAXB;

import org.junit.jupiter.api.Test;

import nl.vpro.i18n.Locales;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class AndTest {

    @Test
    public void testAndBinding() {
        And in = new And(new And());
        And out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:and xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:and/>
                </local:and>""");

        assertThat(out.getConstraints().get(0)).isInstanceOf(And.class);
    }

    @Test
    public void testOrBinding() {
        And in = new And(new Or());
        And out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:and xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:or/>
                </local:and>""");

        assertThat(out.getConstraints().get(0)).isInstanceOf(Or.class);
    }

    @Test
    public void testNotBinding() {
        And in = new And(new Not());
        And out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:and xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:not/>
                </local:and>""");

        assertThat(out.getConstraints().get(0)).isInstanceOf(Not.class);
    }

    @Test
    public void testAvTypeBinding() {
        And in = new And(new AvTypeConstraint());
        And out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:and xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:avType/>
                </local:and>""");

        assertThat(out.getConstraints().get(0)).isInstanceOf(AvTypeConstraint.class);
    }

    @Test
    public void testAvFileFormatBinding() {
        And in = new And(new AvFileFormatConstraint());
        And out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:and xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:avFileFormat/>
                </local:and>""");

        assertThat(out.getConstraints().get(0)).isInstanceOf(AvFileFormatConstraint.class);
    }

    @Test
    public void testAvFileExtensionBinding() {
        And in = new And(new AVFileExtensionConstraint());
        And out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:and xmlns:local="uri:local" xmlns:media="urn:vpro:api:constraint:media:2013">
                    <media:avFileExtension/>
                </local:and>""");

        assertThat(out.getConstraints().get(0)).isInstanceOf(AVFileExtensionConstraint.class);
    }

    @Test
    public void testApplyWhenEmpty() {
        And constraint = new And();
        assertThat(constraint.test(null)).isTrue();
    }

    @Test
    public void testApplyWhenFalse() {
        And constraint = new And(
            MediaConstraints.alwaysFalse(),
            MediaConstraints.alwaysTrue(),
            MediaConstraints.alwaysFalse()
        );
        assertThat(constraint.test(null)).isFalse();

        assertThat(constraint.testWithReason(null).applies()).isFalse();
        assertThat(constraint.testWithReason(null).getReason()).isEqualTo("And");
        assertThat(constraint.testWithReason(null).getDescription(Locales.DUTCH)).isEqualTo("(Voldoet nooit en Voldoet nooit)");
        JAXB.marshal(constraint.testWithReason(null), System.out);

    }

    @Test
    public void testApplyWhenTrue() {
        And constraint = new And(
            MediaConstraints.alwaysTrue()
        );
        assertThat(constraint.test(null)).isTrue();
    }
}
