/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.util;

import lombok.ToString;

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
@ToString
public class LocationResult implements Comparable<LocationResult> {

    @XmlElement
    private AVFileFormat avFileFormat;

    @XmlElement
    private Integer bitrate;

    @XmlElement
    private String programUrl;

    @XmlAttribute
    private String urn;

    @XmlAttribute
    private int score = 0;

    @XmlAttribute
    private String producer;


    public LocationResult() {
    }

    public static LocationResult of(Location location) {
        return new LocationResult(location.getAvFileFormat(), location.getBitrate(), location.getProgramUrl(), location.getUrn());
    }

    @lombok.Builder
    public LocationResult(
        AVFileFormat avFileFormat,
        Integer bitrate,
        String programUrl,
        String urn) {
        this.avFileFormat = avFileFormat;
        this.bitrate = bitrate;
        this.programUrl = programUrl;
        this.urn = urn;
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

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    @Override
    public int compareTo(LocationResult o) {
        int s = o.score - score;
        if (s == 0 && o.avFileFormat!= null && avFileFormat != null) {
            s = o.avFileFormat.compareTo(avFileFormat);
        }
        if (s == 0 && o.bitrate != null && bitrate != null) {
            s = o.bitrate - bitrate;
        }
        return s;


    }
}
