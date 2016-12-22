/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.TextConstraint;
import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "avFileFormatConstraintType")
public class AvFileFormatConstraint extends TextConstraint<MediaObject>  {

    public AvFileFormatConstraint() {
        // Due to a mapping error our current format mapping is lower case and will become upper on next mapping update
        caseHandling = CaseHandling.BOTH;
    }

    public AvFileFormatConstraint(String value) {
        super(value);
        // Due to a mapping error our current format mapping is lower case and will become upper on next mapping update
        caseHandling = CaseHandling.BOTH;
    }

    @Override
    public String getESPath() {
        return "locations.avAttributes.avFileFormat";
    }

    @Override
    public boolean test(MediaObject input) {
        for(Location location : input.getLocations()) {
            if(value.toUpperCase().equals(location.getAvFileFormat().name())) {
                return true;
            }
        }
        return false;
    }
}
