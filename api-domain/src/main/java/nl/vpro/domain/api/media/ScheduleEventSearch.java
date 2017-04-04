/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.util.Date;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.DateRangeMatcher;
import nl.vpro.domain.media.ScheduleEvent;
import nl.vpro.util.DateUtils;

/**
 * @author rico
 */
@XmlType(name = "scheduleEventSearchType", propOrder = {"channel", "net", "rerun" })
public class ScheduleEventSearch extends DateRangeMatcher {

    @XmlElement
    private String channel;

    @XmlElement
    private String net;

    @XmlElement
    private Boolean rerun;

    public ScheduleEventSearch() {
    }

    public ScheduleEventSearch(String channel, Date begin, Date end) {
        super(begin, end, true);
        this.channel = channel;
    }

    public ScheduleEventSearch(String channel, Date begin, Date end, Boolean rerun) {
        super(begin, end, true);
        this.channel = channel;
        this.rerun = rerun;
    }

    public ScheduleEventSearch(String channel, String net, Date begin, Date end, Boolean rerun) {
        super(begin, end, true);
        this.channel = channel;
        this.net = net;
        this.rerun = rerun;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public Boolean getRerun() {
        return rerun;
    }

    public void setRerun(Boolean rerun) {
        this.rerun = rerun;
    }

    public boolean hasSearches() {
        return channel != null || net != null || rerun != null;
    }

    public boolean apply(@Nullable ScheduleEvent t) {
        return t != null && (channel == null || channel.equals(t.getChannel().name()))
            && (net == null || net.equals(t.getNet().getId()))
            && (rerun == null || rerun == t.getRepeat().isRerun())
            && super.test(DateUtils.toDate(t.getStartInstant()));
    }

    @Override
    public boolean test(@Nullable Date t) {
        throw new UnsupportedOperationException("Invalid methodcall, call apply(ScheduleEvent) instead");
    }

    @Override
    public String toString() {
        return "ScheduleEventMatcher{channel=" + channel + ", net=" + net + ", begin=" + begin + ", end=" + end + ", rerun=" + rerun + "}";
    }
}
