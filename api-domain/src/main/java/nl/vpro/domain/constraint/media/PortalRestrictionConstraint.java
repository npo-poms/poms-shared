/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import org.checkerframework.checker.nullness.qual.Nullable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.AbstractTextConstraint;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.PortalRestriction;
import nl.vpro.domain.user.Portal;

/**
 * @author Michiel Meeuwissen
 * @since 3.1
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "portalRestrictionConstraintType")
public class PortalRestrictionConstraint extends AbstractTextConstraint<MediaObject> {

    public PortalRestrictionConstraint() {
        caseHandling = CaseHandling.ASIS;
    }

    @lombok.Builder
    public PortalRestrictionConstraint(Portal value) {
        super(value.getId());
        caseHandling = CaseHandling.ASIS;
    }

    @Override
    public String getESPath() {
        return "exclusives";
    }

    @Override
    public boolean test(@Nullable MediaObject input) {
        if (input == null) {
            return false;
        }
        for (PortalRestriction e : input.getPortalRestrictions()) {
            if (value.equals(e.getPortalId())) {
                return true;
            }
        }
        return false;
    }
}
