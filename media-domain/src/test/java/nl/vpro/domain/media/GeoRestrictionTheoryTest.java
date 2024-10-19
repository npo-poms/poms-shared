/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.meeuw.theories.BasicObjectTheory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
public class GeoRestrictionTheoryTest implements BasicObjectTheory<GeoRestriction> {


    public static GeoRestriction europeNoTime = new GeoRestriction(Region.EUROPE);

    public static GeoRestriction nlNoTime = new GeoRestriction(Region.NL);


    public static GeoRestriction nlTvvod = GeoRestriction.builder().region(Region.NL).platform(Platform.TVVOD).build();

    public static GeoRestriction europe = new GeoRestriction(Region.EUROPE, Instant.ofEpochMilli(1), Instant.ofEpochMilli(2));

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

    @Override
    public Arbitrary<Object> datapoints() {
        return Arbitraries.of(
            europeNoTime,
            nlNoTime,
            nlTvvod,
            europe,
            nl
        );
    }
}
