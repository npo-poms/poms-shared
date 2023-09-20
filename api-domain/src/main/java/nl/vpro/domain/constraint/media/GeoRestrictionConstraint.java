/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.constraint.TextConstraint;
import nl.vpro.domain.media.GeoRestriction;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Platform;
import nl.vpro.domain.media.Region;

/**
 * @author Michiel Meeuwissen
 * @since 3.1
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "geoRestrictionConstraintType")
public class GeoRestrictionConstraint implements TextConstraint<MediaObject> {



    @Getter
    @Setter
    private Region region;

    @XmlAttribute
    @Getter
    @Setter
    private Platform platform;


    public GeoRestrictionConstraint() {

    }


    public GeoRestrictionConstraint(Platform platform, Region region) {
        this.region = region;
        this.platform = platform;
    }

    @Override
    public String getESPath() {
        return "regions";
    }


    @Override
    public boolean test(MediaObject mediaObject) {
        return mediaObject
            .getGeoRestrictions()
            .stream()
            .anyMatch(g -> g.getRegion() == region && platform == null || Objects.equals(platform, g.getPlatform()));

    }

    @XmlValue
    // @XmlValue directly on region gives JAXB mappings troubles ApiMappingsTest fails.
    protected String getXmlValue() {
        return region == null ? null : region.name();
    }

    protected void setXmlValue(String s) {
        this.region = Region.valueOf(s);
    }

    @Override
    public String getValue() {
        return GeoRestriction.getJsonValue(platform, region);

    }
}
