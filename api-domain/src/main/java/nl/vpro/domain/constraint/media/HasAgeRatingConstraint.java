/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.ExistsConstraint;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Michiel Meeuwissen
 * @since 4.9
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "hasAgeRatingConstraintType")
public class HasAgeRatingConstraint implements ExistsConstraint<MediaObject> {

    @Override
    public String getESPath() {
        return "ageRating";
    }

    @Override
    public boolean test(MediaObject input) {
        return input.getAgeRating() != null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
