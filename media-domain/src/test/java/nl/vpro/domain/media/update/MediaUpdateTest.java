/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import nl.vpro.domain.media.AVFileFormat;

public abstract class MediaUpdateTest {



    protected AVAttributesUpdate avAttributes() {
        return new AVAttributesUpdate(
            AVFileFormat.H264,
            1000,
            new AudioAttributesUpdate(2, "AAC"),
            new VideoAttributesUpdate(320, 180));
    }

}
