/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 1.7
 */
public class ScheduleEventsTest {

    @Test
    public void testEqualsHonoringOffset() throws Exception {
        ScheduleEvent event1 = new ScheduleEvent(Channel.NED1, Instant.ofEpochMilli(0), null);
        ScheduleEvent event2 = new ScheduleEvent(Channel.NED1, Instant.ofEpochMilli(10), null);

        event1.setOffset(Duration.ofMillis(10));

        assertThat(ScheduleEvents.equalHonoringOffset(event1, event2)).isTrue();
    }


    @Test
    public void testDifferWithinMarginWhenWithin() throws Exception {
        ScheduleEvent event1 = new ScheduleEvent(Channel.NED1, Instant.ofEpochMilli(0), null);
        ScheduleEvent event2 = new ScheduleEvent(Channel.NED1, Instant.ofEpochMilli(10), null);

        assertThat(ScheduleEvents.differWithinMargin(event1, event2, 10)).isTrue();
    }

    @Test
    public void testDifferWithinMarginWhenOutside() throws Exception {
        ScheduleEvent event1 = new ScheduleEvent(Channel.NED1, Instant.ofEpochMilli(0), null);
        ScheduleEvent event2 = new ScheduleEvent(Channel.NED1, Instant.ofEpochMilli(10), null);

        assertThat(ScheduleEvents.differWithinMargin(event1, event2, 9)).isFalse();
    }

    @Test
    public void testDifferWithinMarginOnChannel() throws Exception {
        ScheduleEvent event1 = new ScheduleEvent(Channel.NED1, Instant.ofEpochMilli(0), null);
        ScheduleEvent event2 = new ScheduleEvent(Channel.NED2, Instant.ofEpochMilli(0), null);

        assertThat(ScheduleEvents.differWithinMargin(event1, event2, 10)).isFalse();
    }

    @Test
    public void testEventsWithoutfilter() throws Exception {
        ScheduleEvent event1 = new ScheduleEvent(Channel.NED1, new Net("A", "aa"), 
            Instant.ofEpochMilli(100), Duration.ofMillis(1000));
        ScheduleEvent event2 = new ScheduleEvent(Channel.NED1, new Net("B", "bb"), Instant.ofEpochMilli(200), Duration.ofMillis(1000));
        ScheduleEvent event3 = new ScheduleEvent(Channel.NED1, new Net("B", "bb"), Instant.ofEpochMilli(300), Duration.ofMillis(1000));
        ScheduleEvent event4 = new ScheduleEvent(Channel.NED1, Instant.ofEpochMilli(400), Duration.ofMillis(1000));
        List<ScheduleEvent> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);
        events.add(event3);
        events.add(event4);
        Schedule schedule = new Schedule(Channel.NED1, Instant.ofEpochMilli(100), Instant.ofEpochMilli(300), events);

        assertThat(schedule.getScheduleEvents().size()).isEqualTo(4);
    }

    @Test
    public void testEventsWithfilter() throws Exception {
        ScheduleEvent event1 = new ScheduleEvent(Channel.NED1, new Net("A", "aa"), Instant.ofEpochMilli(100), Duration.ofMillis(1000));
        ScheduleEvent event2 = new ScheduleEvent(Channel.NED1, new Net("B", "bb"), Instant.ofEpochMilli(200), Duration.ofMillis(1000));
        ScheduleEvent event3 = new ScheduleEvent(Channel.NED1, new Net("B", "bb"), Instant.ofEpochMilli(300), Duration.ofMillis(1000));
        ScheduleEvent event4 = new ScheduleEvent(Channel.NED1, Instant.ofEpochMilli(400), Duration.ofMillis(1000));
        List<ScheduleEvent> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);
        events.add(event3);
        events.add(event4);
        Schedule schedule = new Schedule(Channel.NED1, Instant.ofEpochMilli(100), Instant.ofEpochMilli(300), events);
        schedule.setFiltered(true);

        assertThat(schedule.getScheduleEvents().size()).isEqualTo(3);
    }
}
