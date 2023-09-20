/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.meeuw.xml.bind.annotation.XmlDocumentation;

import nl.vpro.domain.constraint.DateConstraint;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.ScheduleEvent;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "scheduleEventType")
@XmlDocumentation("documentation for schedule event date")
public class ScheduleEventDateConstraint extends DateConstraint<MediaObject> {

    public ScheduleEventDateConstraint() {

    }

    @Override
    public String getESPath() {
        return "scheduleEvents.start";
    }


    @Override
    public boolean test(MediaObject input) {
        if (input instanceof Program) {
            for (ScheduleEvent scheduleEvent : ((Program) input).getScheduleEvents()) {
                if (applyDate(scheduleEvent.getStartInstant())) {
                    return true;
                }
            }
        }

        return false;
    }


}
