/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.EnumConstraint;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MediaType;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "typeConstraintType")
public class MediaTypeConstraint extends EnumConstraint<MediaType, MediaObject> {

    public MediaTypeConstraint() {
        super(MediaType.class);
        caseHandling = CaseHandling.ASIS;
    }

    public MediaTypeConstraint(MediaType value) {
        super(MediaType.class, value);
        caseHandling = CaseHandling.ASIS;
    }

    @Override
    public String getESPath() {
        return "type";
    }

    @Override
    public boolean test(@Nullable MediaObject input) {
        return value == null || (input != null && input.getMediaType() != null && value.equals(input.getMediaType().name()));
    }
}
