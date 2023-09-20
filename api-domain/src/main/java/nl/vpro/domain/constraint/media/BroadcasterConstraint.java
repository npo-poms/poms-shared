/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.AbstractTextConstraint;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.user.Broadcaster;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "broadcasterConstraintType")
public class BroadcasterConstraint extends AbstractTextConstraint<MediaObject> {

    public BroadcasterConstraint() {
        caseHandling = CaseHandling.ASIS;
    }

    public BroadcasterConstraint(String value) {
        super(value);
        caseHandling = CaseHandling.ASIS;
    }


    @Override
    public String getESPath() {
        return "broadcasters.id";
    }

    @Override
    public boolean test(MediaObject input) {
        if (input != null) {
            for(Broadcaster broadcaster : input.getBroadcasters()) {
                if(value.equals(broadcaster.getId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
