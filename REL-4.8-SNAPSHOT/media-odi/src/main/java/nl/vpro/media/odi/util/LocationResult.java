/**
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.util;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.AVFileFormat;
import nl.vpro.domain.media.Location;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(propOrder = {"avFileFormat", "bitrate", "programUrl"})
@XmlRootElement
public class LocationResult {

    @XmlElement
    private AVFileFormat avFileFormat;

    @XmlElement
    private Integer bitrate;

    @XmlElement
    private String programUrl;

    protected LocationResult() {
    }

    public static LocationResult of(Location location) {
        return new LocationResult(location.getAvFileFormat(), location.getBitrate(), location.getProgramUrl());
    }

    public LocationResult(AVFileFormat avFileFormat, Integer bitrate, String programUrl) {
        this.avFileFormat = avFileFormat;
        this.bitrate = bitrate;
        this.programUrl = programUrl;
    }

    public AVFileFormat getAvFileFormat() {
        return avFileFormat;
    }

    public void setAvFileFormat(AVFileFormat avFileFormat) {
        this.avFileFormat = avFileFormat;
    }

    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    public String getProgramUrl() {
        return programUrl;
    }

    public void setProgramUrl(String programUrl) {
        this.programUrl = programUrl;
    }

    @Override
    public String toString() {
        return "LocationResult{" +
            "avFileFormat=" + avFileFormat +
            ", bitrate=" + bitrate +
            ", programUrl='" + programUrl + '\'' +
            '}';
    }
}
