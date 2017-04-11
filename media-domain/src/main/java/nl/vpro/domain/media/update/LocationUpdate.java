/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Builder;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.EmbargoDeprecated;
import nl.vpro.domain.Embargos;
import nl.vpro.domain.media.AVAttributes;
import nl.vpro.domain.media.AVFileFormat;
import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.util.DateUtils;
import nl.vpro.xml.bind.DurationXmlAdapter;

@XmlRootElement(name = "location")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "locationUpdateType", propOrder = {
        "programUrl",
        "avAttributes",
        "offset",
        "duration"
        })
public class LocationUpdate implements Comparable<LocationUpdate>, EmbargoDeprecated {


    public static LocationUpdate copy(LocationUpdate copy) {
        if (copy == null) {
            return null;
        }
        return new LocationUpdate(copy);
    }

    @XmlElement(required = true)
    private String programUrl;

    @XmlElement(required = true)
    private AVAttributesUpdate avAttributes;

    @XmlElement
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    private Duration offset;

    @XmlElement
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    private Duration duration;

    @XmlAttribute
    private Date publishStart;

    @XmlAttribute
    private Date publishStop;

    @XmlAttribute
    private String urn;

    public LocationUpdate() {
    }

    public LocationUpdate(LocationUpdate copy) {
        programUrl = copy.programUrl;
        avAttributes = AVAttributesUpdate.copy(copy.avAttributes);
        offset = copy.offset;
        duration = copy.duration;
        publishStart = copy.publishStart;
        publishStop = copy.publishStop;
    }

    public LocationUpdate(String programUrl, Duration duration, Integer bitrate, AVFileFormat format) {
        this.duration = duration;
        this.programUrl = programUrl;
        this.avAttributes = new AVAttributesUpdate(format, bitrate);
    }

    @Builder
    public LocationUpdate(String programUrl, Duration duration, Integer width, Integer height, Integer bitrate, AVFileFormat format) {
        this(programUrl, duration, bitrate, format);
        this.avAttributes.setVideoAttributes(new VideoAttributesUpdate(width, height));
    }


    @Deprecated
    public LocationUpdate(String programUrl, Date duration, Integer bitrate, AVFileFormat format) {
        this.duration = duration == null ? null : Duration.ofMillis(duration.getTime());
        this.programUrl = programUrl;
        this.avAttributes = new AVAttributesUpdate(format, bitrate);
    }
    @Deprecated
    public LocationUpdate(String programUrl, Date duration, Integer width, Integer height, Integer bitrate, AVFileFormat format) {
        this(programUrl, duration, bitrate, format);
        this.avAttributes.setVideoAttributes(new VideoAttributesUpdate(width, height));
    }
    public LocationUpdate(Location location) {
        programUrl = location.getProgramUrl();
        AVAttributes ats = location.getAvAttributes();
        avAttributes = ats == null ? null : new AVAttributesUpdate(ats);
        offset = location.getOffset();
        duration = location.getDuration();
        Embargos.copy(location, this);
        urn = location.getUrn();
    }

    public Location toLocation() {
        Location result = new Location(
            programUrl,
            OwnerType.BROADCASTER, avAttributes == null ? null : avAttributes.toAvAttributes());
        Embargos.copy(this, result);
        result.setDuration(duration);
        result.setOffset(offset);
        result.setUrn(urn);
        result.setCreationInstant(null);
        return result;
    }

    public AVAttributesUpdate getAvAttributes() {
        return avAttributes;
    }

    public void setAvAttributes(AVAttributesUpdate avAttributes) {
        this.avAttributes = avAttributes;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Deprecated
    public void setDuration(Date duration) {
        this.duration = duration == null ? null : Duration.ofMillis(duration.getTime());
    }

    public Duration getOffset() {
        return offset;
    }

    @Deprecated
    public void setOffset(Date offset) {
        this.offset = offset == null ? null : Duration.ofMillis(offset.getTime());
    }

    public void setOffset(Duration offset) {
        this.offset = offset;
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

    @Override
    public int compareTo(LocationUpdate locationUpdate) {
        return toLocation().compareTo(locationUpdate.toLocation());
    }

    @Override
    public String toString() {
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        return "LocationUpdate{" +
            "programUrl='" + programUrl + '\'' +
            ", duration=" + (duration != null ? timeFormat.format(duration) : "-") +
            ", width=" + ((avAttributes != null && avAttributes.getVideoAttributes() != null) ? avAttributes.getVideoAttributes().getWidth() : "-") +
            ", height=" + ((avAttributes != null && avAttributes.getVideoAttributes() != null) ? avAttributes.getVideoAttributes().getHeight() : "-") +
            ", bitrate=" + ((avAttributes != null) ? avAttributes.getBitrate() : "-") +
            ", format='" + ((avAttributes != null) ? avAttributes.getAvFileFormat() : "-") + '\'' +
            '}';
    }


    @Override
    public Instant getPublishStartInstant() {
        return DateUtils.toInstant(publishStart);

    }

    @Override
    public LocationUpdate setPublishStartInstant(Instant publishStart) {
        this.publishStart = DateUtils.toDate(publishStart);
        return this;
    }

    @Override
    public Instant getPublishStopInstant() {
        return DateUtils.toInstant(publishStop);

    }

    @Override
    public LocationUpdate setPublishStopInstant(Instant publishStop) {
        this.publishStop = DateUtils.toDate(publishStop);
        return this;

    }
}
