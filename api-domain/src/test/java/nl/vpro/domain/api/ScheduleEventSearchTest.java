/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.time.Duration;
import java.time.Instant;

import org.junit.Test;

import nl.vpro.domain.api.media.ScheduleEventSearch;
import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.ScheduleEvent;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Rico Jansen
 * @since 3.0
 */
public class ScheduleEventSearchTest {


    @Test
    public void testGetBeginXml() throws Exception {
        Instant begin = Instant.EPOCH;
        Channel channel = Channel.NED3;
        ScheduleEventSearch in = new ScheduleEventSearch(channel, begin, null);
        ScheduleEventSearch out = JAXBTestUtil.roundTripAndSimilarAndEquals(in,
            "<local:scheduleEventSearch inclusiveEnd=\"true\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:begin>1970-01-01T01:00:00+01:00</api:begin>\n" +
                "    <api:channel>NED3</api:channel>\n" +
                "</local:scheduleEventSearch>");
        assertThat(out.getBegin()).isEqualTo(begin);
        assertThat(out.getChannel()).isEqualTo(channel);
    }

    @Test
    public void testGetEndXml() throws Exception {
        Instant end = Instant.EPOCH;
        Channel channel = Channel.NED3;
        ScheduleEventSearch in = new ScheduleEventSearch(channel, null, end);
        ScheduleEventSearch out = JAXBTestUtil.roundTripAndSimilarAndEquals(in,
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<local:scheduleEventSearch inclusiveEnd=\"true\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:end>1970-01-01T01:00:00+01:00</api:end>\n" +
                "    <api:channel>NED3</api:channel>\n" +
                "</local:scheduleEventSearch>");
        assertThat(out.getEnd()).isEqualTo(end);
        assertThat(out.getChannel()).isEqualTo(channel);
    }

    private ScheduleEventSearch getInstance() {
        return new ScheduleEventSearch(Channel.NED3, Instant.ofEpochMilli(100), Instant.ofEpochMilli(150));
    }


    @Test
    public void testApply() {
        ScheduleEventSearch instance = getInstance();
        ScheduleEvent event = new ScheduleEvent(Channel.NED3, Instant.ofEpochMilli(100), Duration.ofMillis(10));
        assertTrue(instance.test(event));
        event.setStartInstant(Instant.ofEpochMilli(150));
        assertTrue(instance.test(event));
        event.setStartInstant(Instant.ofEpochMilli(200));
        assertFalse(instance.test(event));
        event.setStartInstant(Instant.ofEpochMilli(-1));
        assertFalse(instance.test(event));
    }
}
