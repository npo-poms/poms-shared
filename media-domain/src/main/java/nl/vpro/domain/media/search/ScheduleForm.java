/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
public class ScheduleForm implements Predicate<ScheduleEvent> {

    private SchedulePager pager;

    /**
     * A filter on the 'start' of the scheduleEvent
     */
    private InstantRange dateRange;

    /**
     * A filter on the 'guide day' of the scheduleEvent
     */
    private LocalDateRange guideDayRange;

    private List<Channel> channels;


    public ScheduleForm() {
    }

    @lombok.Builder
    protected ScheduleForm(SchedulePager pager, InstantRange startRange, LocalDateRange guideDayRange, List<Channel> channels) {
        if(pager == null) {
            throw new IllegalArgumentException("Must supply a pager, got: null");
        }
        this.pager = pager;
        this.dateRange = startRange == null ? new InstantRange() : startRange;
        this.guideDayRange = guideDayRange == null ? new LocalDateRange(): guideDayRange;
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
            && (guideDayRange == null || guideDayRange.test(scheduleEvent.getGuideDate()))
            && (channels == null || channels.contains(scheduleEvent.getChannel()));
    }
}
