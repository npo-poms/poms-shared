/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.ExistsConstraint;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Platform;

/**
 * @author Rico Jansen
 * @since 3.1
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "hasPredictionConstraintType")
public class HasPredictionConstraint implements ExistsConstraint<MediaObject> {

    @XmlAttribute
    @Getter
    @Setter
    private Platform platform;

    @Override
    public String getESPath() {
        return "predictions.platform";
    }

    @Override
    public boolean test(MediaObject input) {
        if (platform == null) {
            return !input.getPredictions().isEmpty();
        } else {
            return input.getPrediction(platform) != null;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }



}
