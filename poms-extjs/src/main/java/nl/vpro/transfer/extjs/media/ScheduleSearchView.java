/**
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Schedule;

@XmlRootElement(name = "schedule")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "channel",
    "channelName",
    "guideDay"
})
public class ScheduleSearchView {

    private String channel;

    private String channelName;

    private Date guideDay;

    private ScheduleSearchView() {
    }

    private ScheduleSearchView(String channel, String channelName, Date guideDay) {
        this.channel = channel;
        this.channelName = channelName;
        this.guideDay = guideDay;
    }

    public static ScheduleSearchView create(Schedule schedule) {
        return new ScheduleSearchView(
            schedule.getChannel().name(),
            schedule.getChannel().toString(),
            schedule.getStart()
        );
    }

    public String getChannel() {
        return channel;
    }

    public String getChannelName() {
        return channelName;
    }

    public Date getGuideDay() {
        return guideDay;
    }
}
