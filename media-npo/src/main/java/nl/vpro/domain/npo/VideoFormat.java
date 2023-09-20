/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.npo;

import nl.vpro.domain.media.AVFileFormat;

public class VideoFormat {

    private final AVFileFormat avFileFormat;

    private final Integer bitrate;

    VideoFormat(AVFileFormat avFileFormat, Integer bitrate) {
        this.avFileFormat = avFileFormat;
        this.bitrate = bitrate;
    }

    public AVFileFormat getAvFileFormat() {
        return avFileFormat;
    }

    public Integer getBitrate() {
        return bitrate;
    }

}
