/*
 * Copyright (C) 2010 All rights reserved
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

    private InstantRange startRange;

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
        this.startRange = startRange == null ? new InstantRange() : startRange;
        this.guideDayRange = guideDayRange == null ? new LocalDateRange(): guideDayRange;
        this.channels = channels;
    }


    public boolean hasStart() {
        return startRange != null && startRange.getStartValue() != null;
    }

    public boolean hasStop() {
        return startRange != null && startRange.getStopValue() != null;
    }

    public boolean hasChannels() {
        return channels != null && channels.size() > 0;
    }

    @Override
    public boolean test(ScheduleEvent scheduleEvent) {
        return (startRange == null || startRange.test(scheduleEvent.getStartInstant()))
            && (guideDayRange == null || guideDayRange.test(scheduleEvent.getGuideDate()))
            && (channels == null || channels.contains(scheduleEvent.getChannel()));
    }
}
