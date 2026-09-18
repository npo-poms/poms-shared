/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Platform;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.Region;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
class GeoRestrictionConstraintTest {

    @Test
    void getStringValue() {
        GeoRestrictionConstraint in = new GeoRestrictionConstraint(null, Region.EUROPE);
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:geoRestrictionConstraint xmlns:constraint=\"urn:vpro:api:constraint\" xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">EUROPE</local:geoRestrictionConstraint>");
    }

    @Test
    void getESPath() {
        assertThat(new GeoRestrictionConstraint().getESPath()).isEqualTo("regions");
    }

    @Test
    void getESValue() {
        assertThat(new GeoRestrictionConstraint(Platform.INTERNETVOD, Region.NL).getValue()).isEqualTo("NL");
        assertThat(new GeoRestrictionConstraint(Platform.PLUSVOD, Region.NL).getValue()).isEqualTo("PLUSVOD:NL");
    }

    @SuppressWarnings("deprecation")
    @Test
    void applyWhenTrue() {
        Program program = MediaTestDataBuilder.program().withGeoRestrictions().build();
        assertThat(new GeoRestrictionConstraint(Platform.INTERNETVOD, Region.BENELUX).test(program)).isTrue();
    }

    @Test
    void applyWhenFalse() {
        Program program = MediaTestDataBuilder.program().build();
        assertThat(new GeoRestrictionConstraint(Platform.INTERNETVOD, Region.NL).test(program)).isFalse();
    }
}
