/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.*;

import java.time.Duration;
import java.time.Instant;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Embargos;
import nl.vpro.domain.MutableEmbargo;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.DurationXmlAdapter;
import nl.vpro.xml.bind.InstantXmlAdapter;


/**
 *
 * @see nl.vpro.domain.media.update
 * @see Location
 */
@XmlRootElement(name = "location")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "locationUpdateType", propOrder = {
        "programUrl",
        "avAttributes",
        "offset",
        "duration"
        })
@EqualsAndHashCode
public class LocationUpdate implements Comparable<LocationUpdate>, MutableEmbargo<LocationUpdate> {


    public static LocationUpdate copy(LocationUpdate copy) {
        if (copy == null) {
            return null;
        }
        return new LocationUpdate(copy);
    }

    @Getter
    @Setter
    @XmlElement(required = true)
    @nl.vpro.validation.Location
    @NotNull
    private String programUrl;

    @Getter
    @Setter
    @XmlElement(required = true)
    @NotNull
    @Valid
    private AVAttributesUpdate avAttributes;

    @Getter
    @Setter
    @XmlElement
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    private Duration offset;

    @Getter
    @Setter
    @XmlElement
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    private Duration duration;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant publishStart;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant publishStop;

    @Getter
    @Setter
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

    @lombok.Builder(builderClassName = "Builder")
    public LocationUpdate(String programUrl, Duration duration, Integer width, Integer height, Integer bitrate, AVFileFormat format) {
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

    public Location toLocation(OwnerType ownerType) {
        Location result = new Location(
            programUrl,
            ownerType, avAttributes == null ? null : avAttributes.toAvAttributes());
        Embargos.copy(this, result);
        result.setDuration(duration);
        result.setOffset(offset);
        result.setUrn(urn);
        result.setCreationInstant(null);
        result.setPlatform(Platform.INTERNETVOD);
        return result;
    }

    @Override
    public int compareTo(LocationUpdate locationUpdate) {
        return toLocation(OwnerType.BROADCASTER).compareTo(locationUpdate.toLocation(OwnerType.BROADCASTER));
    }

    @Override
    public String toString() {
        return "LocationUpdate{" +
            "programUrl='" + programUrl + '\'' +
            ", duration=" + (duration != null ? duration : "-") +
            ", width=" + ((avAttributes != null && avAttributes.getVideoAttributes() != null) ? avAttributes.getVideoAttributes().getWidth() : "-") +
            ", height=" + ((avAttributes != null && avAttributes.getVideoAttributes() != null) ? avAttributes.getVideoAttributes().getHeight() : "-") +
            ", bitrate=" + ((avAttributes != null) ? avAttributes.getBitrate() : "-") +
            ", format='" + ((avAttributes != null) ? avAttributes.getAvFileFormat() : "-") + '\'' +
            '}';
    }


    @Override
    public Instant getPublishStartInstant() {
        return publishStart;
    }

    @NonNull
    @Override
    public LocationUpdate setPublishStartInstant(Instant publishStart) {
        this.publishStart = publishStart;
        return this;
    }

    @Override
    public Instant getPublishStopInstant() {
        return publishStop;
    }

    @NonNull
    @Override
    public LocationUpdate setPublishStopInstant(Instant publishStop) {
        this.publishStop = publishStop;
        return this;
    }
}
