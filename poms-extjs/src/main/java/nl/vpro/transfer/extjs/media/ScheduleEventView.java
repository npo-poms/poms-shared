/*
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.bind.DateToJsonTimestamp;
import nl.vpro.domain.user.Broadcaster;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "channel",
        "channelText",
        "net",
        "netText",
        "start",
        "duration",
        "mediaId",
        "title",
        "description",
        "isRerun",
        "broadcasters"
        })
public class ScheduleEventView {

    private String channel;

    private String channelText;

    private String net;

    private String netText;

    private Date start;

    @JsonSerialize(using = DateToJsonTimestamp.Serializer.class, include = JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = DateToJsonTimestamp.Deserializer.class)
    private Date duration;

    private Long mediaId;

    private String title;

    private String description;

    private boolean isRerun = false;

    @XmlElementWrapper(name = "broadcasters")
    @XmlElement(name = "broadcaster")
    private final List<String> broadcasters = new ArrayList<>();

    private ScheduleEventView() {
    }

    private ScheduleEventView(Channel channel, Net net, Date start) {
        this.channel = channel.name();
        this.channelText = channel.toString();
        this.net = net == null ? null  : net.getId();
        this.netText = net == null ? null : net.toString();
        this.start = start;
    }

    public static ScheduleEventView createScheduleEvent(ScheduleEvent fullEvent) {
        MediaObject media = fullEvent.getMediaObject();

        ScheduleEventView simpleEvent = new ScheduleEventView(
            fullEvent.getChannel(), fullEvent.getNet(),
            fullEvent.getStart()
        );

        if(media != null) {
            simpleEvent.mediaId = media.getId();
            simpleEvent.title = media.getMainTitle();
            simpleEvent.description = media.getMainDescription();
            if (simpleEvent.description == null) {
                //https://jira.vpro.nl/browse/MSE-1836
                simpleEvent.description = "";
            }
        }

        simpleEvent.duration = fullEvent.getDuration() == null ? null : new Date(fullEvent.getDuration().toMillis());
        simpleEvent.setRerunEvent(fullEvent);


        for(Broadcaster broadcaster : fullEvent.getMediaObject().getBroadcasters()) {
            simpleEvent.broadcasters.add(broadcaster.getDisplayName());
        }

        return simpleEvent;
    }

    public static ScheduleEventView createMediaEvent(ScheduleEvent fullEvent) {
        if (fullEvent == null) {
            return null;
        }
        ScheduleEventView simpleEvent = new ScheduleEventView(
            fullEvent.getChannel(), fullEvent.getNet(),
            fullEvent.getStart()
        );

        simpleEvent.duration = fullEvent.getDuration() == null ? null : new Date(fullEvent.getDuration().toMillis());
        simpleEvent.setRerunEvent(fullEvent);

        List<Broadcaster> bc = fullEvent.getMediaObject() == null ? null : fullEvent.getMediaObject().getBroadcasters();
        if(bc != null) {
            for(Broadcaster broadcaster : bc) {
                simpleEvent.broadcasters.add(broadcaster.getDisplayName());
            }
        }

        return simpleEvent;
    }

    private void setRerunEvent(ScheduleEvent fullEvent) {
        this.isRerun = ScheduleEvents.isRerun(fullEvent);
    }

    public String getChannel() {
        return channel;
    }

    public String getChannelText() {
        return channelText;
    }


    public String getNet() {
        return net;
    }

    public String getNetText() {
        return netText;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getDuration() {
        return duration;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRerun() {
        return isRerun;
    }
    public void setRerun(boolean b) {
        this.isRerun = b;
    }

    public List<String> getBroadcasters() {
        return broadcasters;
    }
}
