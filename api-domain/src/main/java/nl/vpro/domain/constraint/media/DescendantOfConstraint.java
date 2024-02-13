/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.AbstractTextConstraint;
import nl.vpro.domain.media.DescendantRef;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "descendantOfConstraintType")
public class DescendantOfConstraint extends AbstractTextConstraint<MediaObject> {

    public DescendantOfConstraint() {
        caseHandling = CaseHandling.ASIS;
    }

    public DescendantOfConstraint(String value) {
        super(value);
        caseHandling = CaseHandling.ASIS;
    }

    @Override
    public String getESPath() {
        return "descendantOf.midRef";
    }

    @Override
    public boolean test(MediaObject input) {
        for(DescendantRef ref : input.getDescendantOf()) {
            if(value.equals(ref.getMidRef())) {
                return true;
            }
        }
        return false;
    }
}
