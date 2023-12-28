/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import org.junit.jupiter.api.BeforeEach;

import nl.vpro.domain.media.AVFileFormat;
import nl.vpro.domain.user.ServiceLocator;

public abstract class MediaUpdateTest {


    @BeforeEach
    public void setup() {
        ServiceLocator.setBroadcasterService("KRNC", "MAX", "EO", "VPRO");
    }




    protected AVAttributesUpdate avAttributes() {
        return new AVAttributesUpdate(
            AVFileFormat.H264,
            1000,
            new AudioAttributesUpdate(2, "AAC"),
            new VideoAttributesUpdate(320, 180));
    }

}
