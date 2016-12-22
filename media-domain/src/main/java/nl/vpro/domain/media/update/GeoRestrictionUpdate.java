/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.time.Instant;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.GeoRestriction;
import nl.vpro.domain.media.Region;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(
    name = "geoRestrictionUpdateType"
)
public class GeoRestrictionUpdate {
    protected Instant start;

    protected Instant stop;

    protected Region region;

    public GeoRestrictionUpdate() {
    }

    public GeoRestrictionUpdate(GeoRestriction restriction) {
        this.start = restriction.getStart();
        this.stop = restriction.getStop();
        this.region = restriction.getRegion();
    }

    public GeoRestriction toGeoRestriction() {
        return new GeoRestriction(region, start, stop);
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    public Instant getStop() {
        return stop;
    }

    public void setStop(Instant stop) {
        this.stop = stop;
    }

    @XmlValue
    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
