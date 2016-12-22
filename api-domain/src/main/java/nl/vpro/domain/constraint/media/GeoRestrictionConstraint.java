/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.TextConstraint;
import nl.vpro.domain.media.GeoRestriction;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Region;

/**
 * @author Michiel Meeuwissen
 * @since 3.1
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "geoRestrictionConstraintType")
public class GeoRestrictionConstraint extends TextConstraint<MediaObject> {

    public GeoRestrictionConstraint() {
        caseHandling = CaseHandling.ASIS;
    }

    public GeoRestrictionConstraint(Region region) {
        super(region.name());
        caseHandling = CaseHandling.ASIS;
    }

    @Override
    public String getESPath() {
        return "regions";
    }

    @Override
    public boolean test(@Nullable MediaObject input) {
        if (input == null) return false;
        for (GeoRestriction e : input.getGeoRestrictions()) {
            if (value.equals(e.getRegion().name())) {
                return true;
            }
        }
        return false;
    }
}
