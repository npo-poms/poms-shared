/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.experimental.theories.DataPoint;

import nl.vpro.test.theory.ObjectTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
public class GeoRestrictionTheoryTest extends ObjectTest<GeoRestriction> {

    @DataPoint
    public static GeoRestriction nullArgument = null;

    @DataPoint
    public static GeoRestriction europeNoTime = new GeoRestriction(Region.EUROPE);

    @DataPoint
    public static GeoRestriction nlNoTime = new GeoRestriction(Region.NL);


    @DataPoint
    public static GeoRestriction nlTvvod = GeoRestriction.builder().region(Region.NL).platform(Platform.TVVOD).build();

    @DataPoint
    public static GeoRestriction europe = new GeoRestriction(Region.EUROPE, Instant.ofEpochMilli(1), Instant.ofEpochMilli(2));

    @DataPoint
    public static GeoRestriction nl = new GeoRestriction(Region.NL, Instant.ofEpochMilli(3), Instant.ofEpochMilli(3));

    @Test
    public void testEquals() {
        assertThat(new GeoRestriction(Region.EUROPE)).isEqualTo(new GeoRestriction(Region.EUROPE));
        assertThat(GeoRestriction.builder().region(Region.EUROPE).platform(Platform.PLUSVOD).build()).isEqualTo(GeoRestriction.builder().region(Region.EUROPE).platform(Platform.PLUSVOD).build());
    }

    @Test
    public void testEqualsOnTime() {
        assertThat(new GeoRestriction(Region.EUROPE)).isNotEqualTo(new GeoRestriction(Region.EUROPE, Instant.ofEpochMilli(1), Instant.ofEpochMilli(2)));
    }
}
