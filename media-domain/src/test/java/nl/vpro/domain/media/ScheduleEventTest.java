/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.util.Date;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import nl.vpro.theory.ObjectTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 1.7
 */
public class ScheduleEventTest extends ObjectTest<ScheduleEvent> { // TODO doesn't properly implement ComparableTest<ScheduleEvent> {

    @DataPoint
    public static ScheduleEvent nullEvent = null;

    @DataPoint
    public static ScheduleEvent nullValues = new ScheduleEvent();

    @DataPoint
    public static ScheduleEvent moreNullValues = new ScheduleEvent();

    @DataPoint
    public static ScheduleEvent nullChannel = new ScheduleEvent(null, new Date(100), new Date(1000));

    @DataPoint
    public static ScheduleEvent withNet = new ScheduleEvent(Channel.NED3, new Net("ZAP", "Zappnet"), new Date(100), new Date(1000));

    @DataPoint
    public static ScheduleEvent nullStart = new ScheduleEvent(Channel.NED2, null, new Date(1000));

    @DataPoint
    public static ScheduleEvent nullDuration = new ScheduleEvent(Channel.NED2, new Date(100), null);

    @Test
    public void testGuideDayBeforeCutOff() throws Exception {
        ScheduleEvent target = new ScheduleEvent(Channel.NED1, new Date(0), new Date(10));

        assertThat(target.getGuideDay().toString()).isEqualTo("Wed Dec 31 00:00:00 CET 1969");
    }

    @Test
    public void testGuideDayAfterCutOff() throws Exception {
        ScheduleEvent target = new ScheduleEvent(Channel.NED1, new Date((5 * 3600 + 58 * 60) * 1000), new Date(10));

        assertThat(target.getGuideDay().toString()).isEqualTo("Thu Jan 01 00:00:00 CET 1970");
    }
}
