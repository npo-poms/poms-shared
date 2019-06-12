/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.ToString;

import java.util.List;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.ScheduleEvent;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "scheduleFormType", propOrder = {
    "pager",
    "dateRange",
    "channels"
})
@ToString
public class ScheduleForm implements Predicate<ScheduleEvent> {
    private SchedulePager pager;

    private InstantRange dateRange;

    private List<Channel> channels;


    private ScheduleForm() {
    }

    public ScheduleForm(SchedulePager pager, InstantRange dateRange) {
        if(pager == null) {
            throw new IllegalArgumentException("Must supply a pager, got: null");
        }
        this.pager = pager;
        this.dateRange = dateRange == null ? new InstantRange() : dateRange;
    }

    public SchedulePager getPager() {
        return pager;
    }

    public InstantRange getDateRange() {
        return dateRange;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public boolean hasStart() {
        return dateRange != null && dateRange.getStartValue() != null;
    }

    public boolean hasStop() {
        return dateRange != null && dateRange.getStopValue() != null;
    }

    public boolean hasChannels() {
        return channels != null && channels.size() > 0;
    }

    @Override
    public boolean test(ScheduleEvent scheduleEvent) {
        return (dateRange == null || dateRange.test(scheduleEvent.getStartInstant()))
            && (channels == null || channels.contains(scheduleEvent.getChannel()));
    }
}
