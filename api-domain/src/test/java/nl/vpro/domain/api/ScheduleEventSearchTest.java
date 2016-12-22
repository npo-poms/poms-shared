/**
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import nl.vpro.domain.api.media.ScheduleEventSearch;
import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.ScheduleEvent;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.Test;

import java.util.Date;

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
        Date begin = new Date(0);
        String channel = "NED3";
        ScheduleEventSearch in = new ScheduleEventSearch(channel, begin, null);
        ScheduleEventSearch out = JAXBTestUtil.roundTrip(in,
            " <api:begin>1970-01-01T01:00:00+01:00</api:begin>");
        assertThat(out.getBegin()).isEqualTo(begin);
        assertThat(out.getChannel()).isEqualTo(channel);
    }

    @Test
    public void testGetEndXml() throws Exception {
        Date end = new Date(0);
        String channel = "NED3";
        ScheduleEventSearch in = new ScheduleEventSearch(channel, null, end);
        ScheduleEventSearch out = JAXBTestUtil.roundTrip(in,
            "<api:end>1970-01-01T01:00:00+01:00</api:end>");
        assertThat(out.getEnd()).isEqualTo(end);
        assertThat(out.getChannel()).isEqualTo(channel);
    }

    private ScheduleEventSearch getInstance() {
        return new ScheduleEventSearch("NED3", new Date(100), new Date(150));
    }


    @Test
    public void testApply() {
        ScheduleEventSearch instance = getInstance();
        ScheduleEvent event = new ScheduleEvent(Channel.NED3, new Date(100), new Date(10));
        assertTrue(instance.apply(event));
        event.setStart(new Date(150));
        assertTrue(instance.apply(event));
        event.setStart(new Date(200));
        assertFalse(instance.apply(event));
        event.setStart(new Date(-1));
        assertFalse(instance.apply(event));
    }
}
