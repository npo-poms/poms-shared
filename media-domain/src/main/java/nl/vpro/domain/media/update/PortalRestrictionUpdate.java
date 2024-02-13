/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.time.Instant;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.PortalRestriction;
import nl.vpro.domain.user.Portal;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

import static nl.vpro.domain.Xmlns.UPDATE_NAMESPACE;

/**
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(
    name = "portalRestrictionUpdateType",
    namespace = UPDATE_NAMESPACE)
public class PortalRestrictionUpdate {
    protected Instant start;

    protected Instant stop;

    protected String portal;

    public PortalRestrictionUpdate() {
    }

    public static PortalRestrictionUpdate of(String portal) {
        PortalRestrictionUpdate update = new PortalRestrictionUpdate();
        update.portal = portal;
        return update;
    }

    public PortalRestrictionUpdate(PortalRestriction restriction) {
        this.start = restriction.getStart();
        this.stop = restriction.getStop();
        this.portal = restriction.getPortal().getId();
    }

    public PortalRestriction toPortalRestriction() {
        return new PortalRestriction(new Portal(portal, portal), start, stop);
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
    @JsonProperty("portal")
    public String getPortal() {
        return portal;
    }

    public void setPortal(String portal) {
        this.portal = portal;
    }
}
