/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.io.Serial;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.*;

/**
 * A api-schedule event is like a poms schedule event but:
 * 1. it is in the api namespace
 * 2. it's internal mediaobject is not xmltransient
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
@XmlRootElement(name = "scheduleItem")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "scheduleEventApiType", propOrder = {

})
@XmlSeeAlso(Match.class)
public class ApiScheduleEvent extends ScheduleEvent {

    @Serial
    private static final long serialVersionUID = 8540834440100563042L;

    public ApiScheduleEvent() {
    }

    public ApiScheduleEvent(ScheduleEvent event) {
        super(event);
    }

    public ApiScheduleEvent(ScheduleEvent event, Program media) {
        super(event, media);
    }


    @XmlElements({
        @XmlElement(name = "program", namespace = Xmlns.MEDIA_NAMESPACE, type = Program.class),
        @XmlElement(name = "group", namespace = Xmlns.MEDIA_NAMESPACE, type = Group.class),
        @XmlElement(name = "segment", namespace = Xmlns.MEDIA_NAMESPACE, type = Segment.class)
    })
    @Override
    public Program getParent() {
        return super.getParent();
    }
    @Override
    public void setParent(Program o) {
        this.mediaObject = o;
    }

    @JsonProperty("media")
    public Program getMediaJSON() {
        return this.getParent();
    }

    public void setMediaJSON(Program media) {
        this.setParent(media);
    }
}
