/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.time.Duration;
import java.time.Instant;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import nl.vpro.theory.ObjectTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 1.7
 */
public class ScheduleEventTheoryTest extends ObjectTest<ScheduleEvent> { // TODO doesn't properly implement ComparableTest<ScheduleEvent> {

    @DataPoint
    public static ScheduleEvent nullEvent = null;

    @DataPoint
    public static ScheduleEvent nullValues = new ScheduleEvent();

    @DataPoint
    public static ScheduleEvent moreNullValues = new ScheduleEvent();

    @DataPoint
    public static ScheduleEvent nullChannel = new ScheduleEvent(null, Instant.ofEpochMilli(100), Duration.ofMillis(1000));

    @DataPoint
    public static ScheduleEvent withNet = new ScheduleEvent(Channel.NED3, new Net("ZAP", "Zappnet"),
        Instant.ofEpochMilli(100), Duration.ofMillis(1000));

    @DataPoint
    public static ScheduleEvent nullStart = new ScheduleEvent(Channel.NED2, null, Duration.ofMillis(1000));

    @DataPoint
    public static ScheduleEvent nullDuration = new ScheduleEvent(Channel.NED2, Instant.ofEpochMilli(100), null);

    @Test
    public void testGuideDayBeforeCutOff() {
        ScheduleEvent target = new ScheduleEvent(Channel.NED1, Instant.EPOCH, Duration.ofMillis(10));

        //noinspection deprecation
        assertThat(target.getGuideDay().toString()).isEqualTo("Wed Dec 31 00:00:00 CET 1969");
        assertThat(target.getGuideDate().toString()).isEqualTo("1969-12-31");

    }

    @Test
    public void testGuideDayAfterCutOff() {
        ScheduleEvent target = new ScheduleEvent(Channel.NED1, Instant.ofEpochMilli((5 * 3600 + 58 * 60) * 1000), Duration.ofMillis(10));

        //noinspection deprecation
        assertThat(target.getGuideDay().toString()).isEqualTo("Thu Jan 01 00:00:00 CET 1970");
        assertThat(target.getGuideDate().toString()).isEqualTo("1970-01-01");
    }
}
