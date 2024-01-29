/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.*;

import java.time.Instant;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.MoreObjects;

import nl.vpro.domain.api.Match;
import nl.vpro.domain.api.RangeMatcher;
import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.ScheduleEvent;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author rico
 */
@Getter
@XmlType(name = "scheduleEventSearchType", propOrder = {"begin", "end", "channel", "net", "rerun"})
@EqualsAndHashCode(callSuper = true, exclude = {"begin", "end"})
public class ScheduleEventSearch extends RangeMatcher<Instant, ScheduleEvent> {

    @XmlElement
    @Setter
    private Channel channel;

    @XmlElement
    @Getter
    @Setter
    private String net;

    @XmlElement
    @Getter
    @Setter
    private Boolean rerun;

    @XmlElement
    @Getter
    @Setter
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant begin;

    @XmlElement
    @Getter
    @Setter
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant end;


    public ScheduleEventSearch() {
    }

    @Override
    protected boolean defaultIncludeEnd() {
        return true;
    }

    public ScheduleEventSearch(Channel channel, Instant begin, Instant end) {
        super(begin, end, null);
        this.channel = channel;
    }

    public ScheduleEventSearch(Channel channel, Instant begin, Instant end, Boolean rerun) {
        super(begin, end, null);
        this.channel = channel;
        this.rerun = rerun;
    }

    public ScheduleEventSearch(Channel channel, String net, Instant begin, Instant end, Boolean rerun) {
        super(begin, end, null);
        this.channel = channel;
        this.net = net;
        this.rerun = rerun;
    }

    @lombok.Builder(builderClassName = "Builder")
    private ScheduleEventSearch(Channel channel, String net, Instant begin, Instant end, Boolean rerun, Boolean inclusiveEnd, Match match) {
        super(begin, end, inclusiveEnd);
        this.match = match;
        this.channel = channel;
        this.net = net;
        this.rerun = rerun;
    }

    public boolean hasSearches() {
        return countSearches() > 0;
    }

    /**
     * Returns the number of independent searches on a scheduleevent of a mediaobject this search represents (i.e. restristictions on the number of fields)
     */
    public int countSearches() {
        int result = 0;
        if (channel != null) {
            result++;
        }
        if (net != null) {
            result++;
        }

        if (rerun != null){
            result ++;
        }
        if (begin != null || end  != null) {
            result ++;
        }
        return result;
    }

    @Override
    public boolean test(@Nullable ScheduleEvent t) {
        return t != null && (channel == null || channel.equals(t.getChannel()))
            && (net == null || net.equals(t.getNet().getId()))
            && (rerun == null || rerun == t.isRerun())
            && super.valueTest(t.getStartInstant());
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("channel", channel)
            .add("net", net)
            .add("rerun", rerun)
            .add("begin", begin)
            .add("end", end)
            .omitNullValues()
            .toString();
    }

    public static class Builder {

        Builder original() {
            return rerun(false);
        }

        Builder repeat() {
            return rerun(true);
        }

    }
}
