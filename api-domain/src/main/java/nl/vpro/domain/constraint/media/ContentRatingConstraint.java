/*
 * Copyright (C) 2016 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import javax.annotation.Nullable;
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
        caseHandling = CaseHandling.ASIS;
    }

    public ContentRatingConstraint(ContentRating contentRating) {
        super(ContentRating.class, contentRating);
        caseHandling = CaseHandling.ASIS;
    }

    @Override
    public String getESPath() {
        return "contentRatings";
    }

    @Override
    public boolean test(@Nullable MediaObject input) {
        if (input != null) {
            for (ContentRating cr: input.getContentRatings()) {
                if (value.equals(cr.name())) {
                    return true;
                }
            }
        }
        return false;
    }
}
