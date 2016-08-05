/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

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
public class ScheduleForm implements Predicate<ScheduleEvent> {
    private Pager pager;

    private DateRange dateRange;

    private List<Channel> channels;

    private ScheduleForm() {
    }

    public ScheduleForm(Pager pager, DateRange dateRange) {
        if(pager == null) {
            throw new IllegalArgumentException("Must supply a pager, got: null");
        }
        this.pager = pager;
        this.dateRange = dateRange == null ? new DateRange() : dateRange;
    }

    public Pager getPager() {
        return pager;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public boolean hasStart() {
        return dateRange != null && dateRange.getStart() != null;
    }

    public boolean hasStop() {
        return dateRange != null && dateRange.getStop() != null;
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
