/**
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.media.ScheduleEvent;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "schedule")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {
        "channel",
        "date"
        })
public class ScheduleList extends TransferList<ScheduleEventView> {

    private String channel;

    private String date;

    private ScheduleList() {
    }

    private ScheduleList(String channel, String date) {
        this.channel = channel;
        this.date = date;
        this.success = true;
    }

    public static ScheduleList create(Schedule fullSchedule) {
        ScheduleList simpleSchedule = new ScheduleList(
                fullSchedule.getChannel().toString(),
                fullSchedule.getStart().toString()
        );

        for (ScheduleEvent fullEvent : fullSchedule) {
            simpleSchedule.add(
                    ScheduleEventView.createScheduleEvent(fullEvent)
            );
        }

        return simpleSchedule;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
