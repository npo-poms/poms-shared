/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.EnumConstraint;
import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Program;
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
    }

    public ChannelConstraint(Channel value) {
        super(Channel.class, value);
    }

    @Override
    public String getESPath() {
        return "scheduleEvents.channel";
    }

    @Override
    protected Collection<Channel> getEnumValues(MediaObject input) {
        if (input instanceof Program) {
            return ((Program) input).getScheduleEvents().stream().map(ScheduleEvent::getChannel).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }

    }
}
