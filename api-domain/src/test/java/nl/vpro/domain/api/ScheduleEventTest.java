/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.media.ScheduleResult;
import nl.vpro.domain.media.*;
import nl.vpro.test.jqwik.ComparableTest;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rico
 */
public class ScheduleEventTest implements ComparableTest<ScheduleEvent> {
    Program program = MediaTestDataBuilder.program().creationDate(Instant.ofEpochMilli(100)).mid("VPROWON_12345").withSubtitles().build();


    @Test
    public void testListMedia() {
        ScheduleEvent mediaEvent;
        ApiScheduleEvent apiEvent;

        mediaEvent = new ScheduleEvent(Channel.NED3, Instant.ofEpochMilli(0), Duration.ofMillis(100));

        apiEvent = new ApiScheduleEvent(mediaEvent, program);
        List<ApiScheduleEvent> events = new ArrayList<>();
        events.add(apiEvent);
        ScheduleResult result = new ScheduleResult(new Result<>(events, 0L, 10, Result.Total.approximate(1L)));


        String expected = "<api:scheduleResult total=\"1\"  totalQualifier=\"APPROXIMATE\" offset=\"0\" max=\"10\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <api:items>\n" +
            "        <api:item xsi:type=\"api:scheduleEventApiType\" channel=\"NED3\" midRef=\"VPROWON_12345\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "            <media:guideDay>1969-12-31+01:00</media:guideDay>\n" +
            "            <media:start>1970-01-01T01:00:00+01:00</media:start>\n" +
            "            <media:duration>P0DT0H0M0.100S</media:duration>\n" +
            "            <media:poProgID>VPROWON_12345</media:poProgID>\n" +
            "            <media:program embeddable=\"true\" hasSubtitles=\"true\" mid=\"VPROWON_12345\" sortDate=\"1970-01-01T01:00:00.100+01:00\" creationDate=\"1970-01-01T01:00:00.100+01:00\" workflow=\"FOR PUBLICATION\">\n" +
            "                <media:availableSubtitles language=\"nl\" type=\"CAPTION\"/>\n" +
            "                <media:credits/>\n" +
            "                <media:locations/>\n" +
            "                <media:images/>\n" +
            "                <media:scheduleEvents/>\n" +
            "                <media:segments/>\n" +
            "            </media:program>\n" +
            "        </api:item>\n" +
            "    </api:items>\n" +
            "</api:scheduleResult>";
        ScheduleResult roundtripped = JAXBTestUtil.roundTripAndSimilar(result, expected);
        assertThat(roundtripped.getItems().get(0).getChannel()).isEqualTo(Channel.NED3);
    }

    @Test
    public void testGetMedia() {
        ScheduleEvent mediaEvent;
        ApiScheduleEvent apiEvent;

        mediaEvent = new ScheduleEvent(Channel.NED3, Instant.EPOCH, Duration.ofMillis(100));
        Program program = MediaTestDataBuilder.program().creationDate(Instant.ofEpochMilli(100)).mid("VPROWON_12346").build();

        apiEvent = new ApiScheduleEvent(mediaEvent, program);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<api:scheduleItem channel=\"NED3\" midRef=\"VPROWON_12346\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <media:guideDay>1969-12-31+01:00</media:guideDay>\n" +
            "    <media:start>1970-01-01T01:00:00+01:00</media:start>\n" +
            "    <media:duration>P0DT0H0M0.100S</media:duration>\n" +
            "    <media:poProgID>VPROWON_12346</media:poProgID>\n" +
            "    <media:program embeddable=\"true\" mid=\"VPROWON_12346\" sortDate=\"1970-01-01T01:00:00.100+01:00\" creationDate=\"1970-01-01T01:00:00.100+01:00\" workflow=\"FOR PUBLICATION\">\n" +
            "        <media:credits/>\n" +
            "        <media:locations/>\n" +
            "        <media:images/>\n" +
            "        <media:scheduleEvents/>\n" +
            "        <media:segments/>\n" +
            "    </media:program>\n" +
            "</api:scheduleItem>";
        ApiScheduleEvent roundtripped = JAXBTestUtil.roundTripAndSimilar(apiEvent, expected);
        assertThat(roundtripped.getChannel()).isEqualTo(Channel.NED3);
    }


    Instant now = Instant.now();
    @Override
    public Arbitrary<? extends ScheduleEvent> datapoints() {
        return Arbitraries.of(
            ScheduleEvent.builder().build(),
            ScheduleEvent.builder().start(now).build(),
            ScheduleEvent.builder().channel(Channel.ARTE).start(now).build(),
            ScheduleEvent.builder().channel(Channel.ARTE).start(now.plus(Duration.ofMinutes(10))).build(),
            ScheduleEvent.builder().channel(Channel.ARTE).start(now.plus(Duration.ofMinutes(10))).build(),
            ScheduleEvent.builder().channel(Channel.NED1).start(now.plus(Duration.ofMinutes(10))).build(),
            null,
            null
        ).map(s -> s == null ? null : new ApiScheduleEvent(s, program));
    }
}
