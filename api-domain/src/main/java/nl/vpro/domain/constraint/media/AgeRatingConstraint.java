/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.EnumConstraint;
import nl.vpro.domain.media.AgeRating;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Jeroen van Vianen
 * @since 4.8
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "ageRatingConstraintType")
public class AgeRatingConstraint extends EnumConstraint<AgeRating, MediaObject> {

    public AgeRatingConstraint() {
        super(AgeRating.class);
    }

    public AgeRatingConstraint(AgeRating ageRating) {
        super(AgeRating.class, ageRating);
    }

    @Override
    public String getESPath() {
        return "ageRating";
    }

    @Override
    protected Collection<AgeRating> getEnumValues(MediaObject input) {
        return asCollection(input.getAgeRating());

    }
}
