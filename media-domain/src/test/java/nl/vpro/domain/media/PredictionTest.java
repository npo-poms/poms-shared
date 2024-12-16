/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.meeuw.theories.ComparableTheory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
public class PredictionTest  implements ComparableTheory<Prediction> {

    @Test
    public void testGetPublishStartStop() {
        Prediction target = new Prediction(null, Instant.ofEpochMilli(1), Instant.ofEpochMilli(2));
        assertThat(target.getPublishStartInstant()).isBefore(target.getPublishStopInstant());
    }

    @Test
    public void testGetPlatform() {
        Prediction target = new Prediction(Platform.INTERNETVOD);
        assertThat(target.getPlatform()).isEqualTo(Platform.INTERNETVOD);
    }


    @Test
    public void equals() {
        Prediction p1 = Prediction.announced().platform(Platform.INTERNETVOD).parent(null).build();
        Prediction p2 =  Prediction.announced().platform(Platform.INTERNETVOD).parent(new Program()).build();
        assertThat(p1).isNotEqualTo(p2);
        assertThat(p1.equalsIgnoreParent(p2)).isTrue();
        p1.setParent(new Program());
        assertThat(p1).isNotEqualTo(p2);
        p2.setParent(p1.getParent());
        assertThat(p1).isEqualTo(p2);

    }


    @Override
    public Arbitrary<Prediction> datapoints() {
        return Arbitraries.of(
            Prediction.announced().platform(Platform.INTERNETVOD).parent(null).build(),
            Prediction.announced().platform(Platform.INTERNETVOD).parent(new Program("mid_1")).build(),
            Prediction.announced().platform(Platform.INTERNETVOD).parent(new Program("mid_2")).build(),
            Prediction.announced().platform(Platform.PLUSVOD).parent(null).build(),
            Prediction.announced().platform(Platform.PLUSVOD).parent(new Program("mid_1")).build(),
            Prediction.announced().platform(Platform.PLUSVOD).parent(new Program("mid_2")).build(),
            Prediction.announced().platform(Platform.TVVOD).parent(null).build(),
            Prediction.announced().platform(Platform.TVVOD).parent(new Program("mid_1")).build(),
            Prediction.announced().platform(Platform.TVVOD).parent(new Program("mid_2")).build()

        );
    }
}
