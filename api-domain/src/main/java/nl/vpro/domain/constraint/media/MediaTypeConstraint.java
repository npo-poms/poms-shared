/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import java.util.Collection;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.EnumConstraint;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MediaType;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "typeConstraintType")
public class MediaTypeConstraint extends EnumConstraint<MediaType, MediaObject> {

    public MediaTypeConstraint() {
        super(MediaType.class);
    }

    public MediaTypeConstraint(MediaType value) {
        super(MediaType.class, value);
    }

    @Override
    public String getESPath() {
        return "type";
    }

    @Override
    protected Collection<MediaType> getEnumValues(MediaObject input) {
        return asCollection(input.getMediaType());
    }


}
