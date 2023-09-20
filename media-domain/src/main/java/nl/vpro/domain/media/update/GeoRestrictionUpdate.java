/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.GeoRestriction;
import nl.vpro.domain.media.Platform;
import nl.vpro.domain.media.Region;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(
    name = "geoRestrictionUpdateType"
)
@Getter
@Setter
public class GeoRestrictionUpdate implements Comparable<GeoRestrictionUpdate> {
    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    protected Instant start;


    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    protected Instant stop;

    @XmlValue
    protected Region region;

    @XmlAttribute
    protected Platform platform;

    public GeoRestrictionUpdate() {
    }

    public static GeoRestrictionUpdate of(Region region, Platform platform) {
        GeoRestrictionUpdate update = new GeoRestrictionUpdate();
        update.setRegion(region);
        update.setPlatform(platform);
        return update;
    }

    public GeoRestrictionUpdate(GeoRestriction restriction) {
        this.start = restriction.getStart();
        this.stop = restriction.getStop();
        this.region = restriction.getRegion();
        this.platform = restriction.getPlatform();
    }

    public GeoRestriction toGeoRestriction() {
        return GeoRestriction.builder()
            .region(region)
            .start(start)
            .stop(stop)
            .platform(platform)
            .build();
    }


    @Override
    public int compareTo(GeoRestrictionUpdate o) {
        return toGeoRestriction().compareTo(o.toGeoRestriction());

    }
}
