/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.update.ProgramUpdate;

import static org.assertj.core.api.Assertions.assertThat;

public class MediaBuilderTest {

    @Test
    public void testDescendantOf() {
        Program program = MediaBuilder.program()
            .descendantOf(
                new DescendantRef(null, "urn:vpro:media:program:1", MediaType.BROADCAST),
                new DescendantRef(null, "urn:vpro:media:group:2", MediaType.SEASON),
                new DescendantRef("MID_123456", null, MediaType.SERIES))
            .lastModifiedBy("pietje.puk@vpro.nl")
            .build();

        assertThat(program.getDescendantOf()).containsOnly(
            new DescendantRef(null, "urn:vpro:media:program:1", MediaType.BROADCAST),
            new DescendantRef(null, "urn:vpro:media:group:2", MediaType.SEASON),
            new DescendantRef("MID_123456", null, MediaType.SERIES));
        assertThat(program.getLastModifiedBy()).isEqualTo("pietje.puk@vpro.nl");
    }


    @SuppressWarnings("deprecation")
    @Test
    public void testScheduleEvent() {
        Program program = MediaBuilder.program().scheduleEvents(
            new ScheduleEvent(Channel.NED3, new Date(100), new Date(200)),
            new ScheduleEvent(Channel.NED3, new Net("ZAPP"), new Date(300 + 3 * 24 * 3600 * 1000), new Date(50)),
            new ScheduleEvent(Channel.HOLL, new Date(350 + 8 * 24 * 3600 * 1000), new Date(250)),
            new ScheduleEvent(Channel.CONS, new Date(600 + 10 * 24 * 3600 * 1000), new Date(200)),
            new ScheduleEvent(Channel.NED1, Date.from(LocalDateTime.parse("1973-03-05T06:30:00").atZone(Schedule.ZONE_ID).toInstant()), new Date(200)),
            new ScheduleEvent(Channel.NED1, Date.from(LocalDateTime.parse("1973-03-05T05:30:00").atZone(Schedule.ZONE_ID).toInstant()), new Date(200))
        )
            .build();

        assertThat(new ArrayList<>(program.getScheduleEvents()).get(0).getChannel()).isEqualTo(Channel.NED3);
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(0).getGuideDay()).isEqualTo(new Date(-25 * 60 * 60 * 1000));
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(0).getGuideDate()).isEqualTo(LocalDate.of(1969, 12, 31));
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(0).getStart())
            .isEqualTo(Date.from(LocalDateTime.parse("1970-01-01T01:00:00.100").atZone(Schedule.ZONE_ID).toInstant()));
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(1).getChannel()).isEqualTo(Channel.NED3);
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(1).getGuideDay()).isEqualTo(new Date(169200000));
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(1).getGuideDate()).isEqualTo(LocalDate.of(1970, 1, 3));
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(1).getStart())
            .isEqualTo(Date.from(LocalDateTime.parse("1970-01-04T01:00:00.300").atZone(Schedule.ZONE_ID).toInstant()));
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(2).getChannel()).isEqualTo(Channel.HOLL);
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(2).getGuideDay()).isEqualTo(new Date(601200000));
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(2).getGuideDate()).isEqualTo(LocalDate.of(1970, 1, 8));
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(2).getStart())
            .isEqualTo(Date.from(LocalDateTime.parse("1970-01-09T01:00:00.350").atZone(Schedule.ZONE_ID).toInstant()));
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(3).getChannel()).isEqualTo(Channel.CONS);
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(3).getGuideDay()).isEqualTo(new Date(774000000));
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(3).getGuideDate()).isEqualTo(LocalDate.of(1970, 1, 10));
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(3).getStart())
            .isEqualTo(Date.from(LocalDateTime.parse("1970-01-11T01:00:00.600").atZone(Schedule.ZONE_ID).toInstant()));

        assertThat(new ArrayList<>(program.getScheduleEvents()).get(4).getGuideDay())
            .isEqualTo(Date.from(LocalDateTime.parse("1973-03-04T00:00:00").atZone(Schedule.ZONE_ID).toInstant()));
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(4).getGuideDate()).isEqualTo(LocalDate.of(1973, 3, 4));

        assertThat(new ArrayList<>(program.getScheduleEvents()).get(5).getGuideDay())
            .isEqualTo(Date.from(LocalDateTime.parse("1973-03-05T00:00:00").atZone(Schedule.ZONE_ID).toInstant()));
        assertThat(new ArrayList<>(program.getScheduleEvents()).get(5).getGuideDate()).isEqualTo(LocalDate.of(1973, 3, 5));


    }

    @Test
    public void testClone() {
        MediaBuilder.ProgramBuilder programBuilder = MediaBuilder.program().mainTitle("base");
        Program program1 = programBuilder.copy().mainTitle("sub1").mid("mid1").build();
        Program program2 = programBuilder.mid("mid2").build();
        assertThat(program1.getMainTitle()).isEqualTo("sub1");
        assertThat(program1.getMid()).isEqualTo("mid1");
        assertThat(program2.getMainTitle()).isEqualTo("base");
        assertThat(program2.getMid()).isEqualTo("mid2");

    }

    @Test
    public void testLocations() {
        Program program = MediaBuilder.program()
            .locations(
                Location.builder()
                    .programUrl("http://www.vpro.nl/1")
                    .owner(OwnerType.BROADCASTER)
                    .build()
            )
            .build();
        program.getLocations().first().setPublishStopInstant(
            LocalDate.of(2017, 4, 11).atStartOfDay(Schedule.ZONE_ID).toInstant());


        assertThat(program.getLocations()).hasSize(1);
        assertThat(program.getLocations().first().getProgramUrl()).isEqualTo("http://www.vpro.nl/1");

        ProgramUpdate update = ProgramUpdate.create(program);

        assertThat(update.getLocations()).hasSize(1);
        assertThat(update.getLocations().first().getProgramUrl()).isEqualTo("http://www.vpro.nl/1");
        assertThat(update.getLocations().first().getPublishStopInstant()).isEqualTo(LocalDate.of(2017, 4, 11).atStartOfDay(Schedule.ZONE_ID).toInstant());

    }

    @Test
    public void  testEpisodeOf() {
        Program program = MediaBuilder.program()
            .episodeOf("bla_bla")
            .build();

        assertThat(program.getEpisodeOf()).hasSize(1);
        assertThat(program.getEpisodeOf().first().getMidRef()).isEqualTo("bla_bla");


        assertThat(program.getDescendantOf()).hasSize(1);
        assertThat(program.getDescendantOf().first().getMidRef()).isEqualTo("bla_bla");



    }
}
