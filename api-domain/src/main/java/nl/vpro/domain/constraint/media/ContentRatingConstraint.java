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
import nl.vpro.domain.media.ContentRating;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Jeroen van Vianen
 * @since 4.8
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "contentRatingConstraintType")
public class ContentRatingConstraint extends EnumConstraint<ContentRating, MediaObject> {

    public ContentRatingConstraint() {
        super(ContentRating.class);
    }

    public ContentRatingConstraint(ContentRating contentRating) {
        super(ContentRating.class, contentRating);
    }

    @Override
    public String getESPath() {
        return "contentRatings";
    }

    @Override
    protected Collection<ContentRating> getEnumValues(MediaObject input) {
        return input.getContentRatings();

    }

}
