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
import nl.vpro.domain.media.AVFileFormat;
import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "avFileFormatConstraintType")
public class AvFileFormatConstraint extends EnumConstraint<AVFileFormat, MediaObject> {

    public AvFileFormatConstraint() {
        super(AVFileFormat.class);
    }

    public AvFileFormatConstraint(String value) {
        super(AVFileFormat.class, AVFileFormat.valueOf(value.toUpperCase()));
    }


    @Override
    public String getESPath() {
        return "locations.avAttributes.avFileFormat";
    }

    @Override
    protected Collection<AVFileFormat> getEnumValues(MediaObject input) {
        return input.getLocations().stream().map(Location::getAvFileFormat).collect(Collectors.toList());
    }

}
