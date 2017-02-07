/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.time.Instant;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
public class PredictionTest {

    @Test
    public void testGetPublishStartStop() throws Exception {
        Prediction target = new Prediction(null, Instant.ofEpochMilli(1), Instant.ofEpochMilli(2));
        assertThat(target.getPublishStartInstant()).isLessThan(target.getPublishStopInstant());
    }

    @Test
    public void testGetPlatform() throws Exception {
        Prediction target = new Prediction(Platform.INTERNETVOD);
        assertThat(target.getPlatform()).isEqualTo(Platform.INTERNETVOD);
    }
}
