/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.EnumConstraint;
import nl.vpro.domain.media.GeoRestriction;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Region;

/**
 * @author Michiel Meeuwissen
 * @since 3.1
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "geoRestrictionConstraintType")
public class GeoRestrictionConstraint extends EnumConstraint<Region, MediaObject> {

    public GeoRestrictionConstraint() {
        super(Region.class);
    }


    public GeoRestrictionConstraint(Region region) {
        super(Region.class, region);
    }

    @Override
    public String getESPath() {
        return "regions";
    }

    @Override
    protected Collection<Region> getEnumValues(MediaObject input) {
        return input.getGeoRestrictions().stream().map(GeoRestriction::getRegion).collect(Collectors.toList());

    }

}
