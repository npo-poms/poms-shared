/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.EnumConstraint;
import nl.vpro.domain.media.AVType;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "avTypeConstraintType")
public class AvTypeConstraint extends EnumConstraint<AVType, MediaObject> {

    public AvTypeConstraint() {
        super(AVType.class);
        caseHandling = CaseHandling.ASIS;
    }

    public AvTypeConstraint(AVType value) {
        super(AVType.class, value);
        caseHandling = CaseHandling.ASIS;
    }

    @Override
    public String getESPath() {
        return "avType";
    }

    @Override
    public boolean test(MediaObject input) {
        return value.toUpperCase().equals(input.getAVType().name());
    }
}
