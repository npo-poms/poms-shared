/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
public class PredictionTest  {

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
        Prediction p2 = Prediction.announced().platform(Platform.INTERNETVOD).parent(new Program()).build();
        assertThat(p1).isEqualTo(p2);
        p1.setParent(new Program());
        assertThat(p1).isNotEqualTo(p2);
    }


}
