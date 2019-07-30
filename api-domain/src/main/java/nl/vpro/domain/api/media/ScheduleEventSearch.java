/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.Nullable;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.Match;
import nl.vpro.domain.api.RangeMatcher;
import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.ScheduleEvent;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

;

/**
 * @author rico
 */
@XmlType(name = "scheduleEventSearchType", propOrder = {"begin", "end", "channel", "net", "rerun"})
public class ScheduleEventSearch extends RangeMatcher<Instant> implements Predicate<ScheduleEvent> {

    @XmlElement
    @Getter
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
        return channel != null || net != null || rerun != null;
    }

    @Override
    public boolean test(@Nullable ScheduleEvent t) {
        return t != null && (channel == null || channel.equals(t.getChannel()))
            && (net == null || net.equals(t.getNet().getId()))
            && (rerun == null || rerun == t.getRepeat().isRerun())
            && super.testComparable(t.getStartInstant());
    }


    @Override
    public String toString() {
        return "ScheduleEventMatcher{channel=" + channel + ", net=" + net + ", begin=" + begin + ", end=" + end + ", rerun=" + rerun + "}";
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
