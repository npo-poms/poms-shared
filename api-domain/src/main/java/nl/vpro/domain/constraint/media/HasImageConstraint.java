/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.ExistsConstraint;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "hasImageConstraintType")
public class HasImageConstraint implements ExistsConstraint<MediaObject> {

    @Override
    public String getESPath() {
        return "images.urn";
    }

    @Override
    public boolean test(MediaObject input) {
        return !input.getImages().isEmpty();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
