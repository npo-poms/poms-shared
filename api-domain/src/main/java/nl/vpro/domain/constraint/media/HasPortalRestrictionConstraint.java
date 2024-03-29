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
 * @author Michiel Meeuwissen
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "hasPortalRestrictionConstraintType")
public class HasPortalRestrictionConstraint implements ExistsConstraint<MediaObject> {

    @Override
    public String getESPath() {
        return "exclusives";
    }

    @Override
    public boolean test(MediaObject input) {
        return ! input.getPortalRestrictions().isEmpty();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
