/*
 * Copyright (C) 2008 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.*;
import java.util.Arrays;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.*;

import nl.vpro.domain.Changeables;

import static java.time.temporal.ChronoUnit.SECONDS;
import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

public class ScheduleTest {

    @Test
    public void testUnmarshalForMISDuplicatesOnClearScheduleEvents() {

        MediaTable schedule = exampleTable();
        final StringWriter writer = new StringWriter();
        JAXB.marshal(schedule, writer);
        final MediaTable result = JAXB.unmarshal(new StringReader(writer.toString()), MediaTable.class);

        assertThat(result.getProgramTable()).hasSize(2);
        assertThat(result.getProgramTable().get(0).getMid()).isEqualTo("id1");
        assertThat(result.getProgramTable().get(0).getScheduleEvents()).hasSize(1);
        assertThat(result.getProgramTable().get(0).getCrids()).hasSize(1);
        assertThat(result.getProgramTable().get(1).getMid()).isEqualTo("id2");
        assertThat(result.getProgramTable().get(1).getScheduleEvents()).hasSize(1);
        assertThat(result.getProgramTable().get(1).getCrids()).isEmpty();
    }

    @Test
     public void json() {
        Schedule schedule = example();

        assertThatJson(schedule).isSimilarTo(
            """
                {
                  "scheduleEvent" : [ {
                    "channel" : "CONS",
                    "start" : 0,
                    "guideDay" : -90000000,
                    "duration" : 10000,
                    "midRef" : "id1",
                    "poProgID" : "id1",
                    "urnRef" : "crid://domain.com/12345"
                  }, {
                    "channel" : "CONS",
                    "start" : 10000,
                    "guideDay" : -90000000,
                    "duration" : 10000,
                    "midRef" : "id2",
                    "poProgID" : "id2",
                    "urnRef" : "crid://domain.com/12345"
                  }, {
                    "channel" : "CONS",
                    "start" : 20000,
                    "guideDay" : -90000000,
                    "duration" : 10000
                  } ],
                  "channel" : "CONS",
                  "start" : 1649086200000,
                  "stop" : 1649086200000
                }""").andRounded().isEqualTo(schedule);

    }

    protected Schedule example() {
        MediaTable table = exampleTable();
        return table.getSchedule();
    }

    protected MediaTable exampleTable() {
        MediaTable table = new MediaTable();

        Program program = MediaBuilder.program().crids("crid://domain.com/12345").build();
        table.addProgram(program);

        // Need three to trigger ConcurrentModificationException
        ScheduleEvent event1 = new ScheduleEvent(Channel.CONS, Instant.EPOCH, Duration.of(10, SECONDS));
        event1.setUrnRef(program.getCrids().get(0));
        event1.setPoProgID("id1");
        ScheduleEvent event2 = new ScheduleEvent(Channel.CONS, Instant.EPOCH.plus(10, SECONDS), Duration.of(10, SECONDS));
        event2.setUrnRef(program.getCrids().get(0));
        event2.setPoProgID("id2");
        ScheduleEvent event3 = new ScheduleEvent(Channel.CONS, Instant.EPOCH.plus(20, SECONDS), Duration.of(10, SECONDS));
        event2.setUrnRef(program.getCrids().get(0));
        event2.setPoProgID("id2");

        Schedule schedule = new Schedule(Channel.CONS,
            Instant.parse("2022-04-04T15:30:00Z"),
            Arrays.asList(event1, event2, event3)
        );
        table.setSchedule(schedule);

        return table;
    }


    @Test
    public void testGuideDay() {

        fixedClock(LocalDateTime.of(2022, 12, 12, 5, 58));
        assertThat(Schedule.guideDay()).isEqualTo("2022-12-12");

        fixedClock(LocalDateTime.of(2022, 12, 12, 5, 57));
        assertThat(Schedule.guideDay()).isEqualTo("2022-12-11");

        fixedClock(LocalDateTime.of(2022, 12, 13, 4, 57));
        assertThat(Schedule.guideDay()).isEqualTo("2022-12-12");
    }

    void fixedClock(LocalDateTime localDateTime) {
        Changeables.CLOCK.set(Clock.fixed(localDateTime
            .atZone(Schedule.ZONE_ID).toInstant(), Schedule.ZONE_ID));
    }

    @AfterEach
    public  void clearUp() {
        Changeables.CLOCK.remove();
    }
}
