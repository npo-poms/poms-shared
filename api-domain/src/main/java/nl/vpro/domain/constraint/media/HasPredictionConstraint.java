/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.ExistsConstraint;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Rico Jansen
 * @since 3.1
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "hasPredictionConstraintType")
public class HasPredictionConstraint implements ExistsConstraint<MediaObject> {

    @Override
    public String getESPath() {
        return "predictions.platform";
    }

    @Override
    public boolean test(MediaObject input) {
        return !input.getPredictions().isEmpty();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
