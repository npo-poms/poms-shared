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
import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.ScheduleEvent;

/**
 * @author Michiel Meeuwissen
 * @since 3.1
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "channelConstraintType")
public class ChannelConstraint extends EnumConstraint<Channel, MediaObject> {

    public ChannelConstraint() {
        super(Channel.class);
        caseHandling = CaseHandling.ASIS;
    }

    public ChannelConstraint(Channel value) {
        super(Channel.class, value);
        caseHandling = CaseHandling.ASIS;
    }

    @Override
    public String getESPath() {
        return "scheduleEvents.channel";
    }

    @Override
    public boolean test(@Nullable MediaObject input) {
        if (input == null) return false;
        for (ScheduleEvent e : input.getScheduleEvents()) {
            if (value.equals(e.getChannel().name())) {
                return true;
            }
        }
        return false;
    }
}
