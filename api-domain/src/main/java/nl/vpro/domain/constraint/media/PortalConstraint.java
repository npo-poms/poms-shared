/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import org.checkerframework.checker.nullness.qual.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.AbstractTextConstraint;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.user.Portal;

/**
 * @author Michiel Meeuwissen
 * @since 3.1
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "portalConstraintType")
public class PortalConstraint extends AbstractTextConstraint<MediaObject> {

    public PortalConstraint() {
        caseHandling = CaseHandling.ASIS;
    }

    @lombok.Builder
    public PortalConstraint(Portal value) {
        super(value.getId());
        caseHandling = CaseHandling.ASIS;
    }

    @Override
    public String getESPath() {
        return "portals.id";
    }

    @Override
    public boolean test(@Nullable MediaObject input) {
        if (input == null) return false;
        for (Portal e : input.getPortals()) {
            if (value.equals(e.getId())) {
                return true;
            }
        }
        return false;
    }
}
