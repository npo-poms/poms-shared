/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.AbstractTextConstraint;
import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "avFileExtensionConstraintType")
public class AVFileExtensionConstraint extends AbstractTextConstraint<MediaObject> {

    public AVFileExtensionConstraint() {
        caseHandling = CaseHandling.LOWER;
    }

    public AVFileExtensionConstraint(String value) {
        super(value);
        caseHandling = CaseHandling.LOWER;
    }

    @Override
    public String getESPath() {
        return "locations.programUrl.extension";
    }

    @Override
    public boolean test(MediaObject input) {
        for(Location location : input.getLocations()) {
            if(location.getProgramUrl().toLowerCase().endsWith(value.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
