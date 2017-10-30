/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;

import nl.vpro.domain.media.support.*;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.i18n.Locales;

import static nl.vpro.domain.TextualObjects.findOwnersForTextFields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Roelof Jan Koekoek
 * @since 1.4
 */
@Slf4j
public class MediaObjectsTest {

    @Test
    public void testFindOwnersForTextFieldsOnOrdering() throws Exception {
        final Program program = MediaBuilder.program()
            .titles(new Title("1", OwnerType.CERES, TextualType.MAIN))
            .descriptions(new Description("1", OwnerType.BROADCASTER, TextualType.EPISODE))
            .build();

        assertThat(findOwnersForTextFields(program).length).isEqualTo(2);
        assertThat(findOwnersForTextFields(program)[0]).isEqualTo(OwnerType.BROADCASTER);
    }

    @Test
    public void testFindOwnersForTextFieldsOnDuplicates() throws Exception {
        final Program program = MediaBuilder.program().titles(
            new Title("1", OwnerType.CERES, TextualType.MAIN),
            new Title("1", OwnerType.CERES, TextualType.EPISODE)
        ).build();

        assertThat(findOwnersForTextFields(program).length).isEqualTo(1);
    }


    @Test
    public void sortDate() {
        Program program = new Program();
        assertThat(Math.abs(MediaObjects.getSortInstant(program).toEpochMilli() - System.currentTimeMillis())).isLessThan(10000);
        Instant publishDate = Instant.ofEpochMilli(1344043500362L);
        program.setPublishStartInstant(publishDate);
        assertThat(MediaObjects.getSortInstant(program)).isEqualTo(publishDate);
        ScheduleEvent se = new ScheduleEvent();
        se.setStartInstant(Instant.ofEpochMilli(1444043500362L));
        program.addScheduleEvent(se);
        assertThat(MediaObjects.getSortInstant(program)).isEqualTo(se.getStartInstant());
        Segment segment = new Segment();
        program.addSegment(segment);
        assertThat(MediaObjects.getSortInstant(segment)).isEqualTo(se.getStartInstant());
    }


    /**
     * MSE-3726 Sort date should be the most recent schedule event which is not a rerun
     */
    @Test
    public void testSortDateWithScheduleEvents() throws Exception {
        final Program program = MediaBuilder.program()
            .creationDate(Instant.ofEpochMilli(1))
            .publishStart(Instant.ofEpochMilli(2))
            .scheduleEvents(
                ScheduleEvent.builder().channel(Channel.NED2).localStart(2015, 1, 1, 12, 30).duration(Duration.ofMinutes(10)).rerun(false).build(),
                ScheduleEvent.builder().channel(Channel.NED2).localStart(2015, 1, 1, 17, 30).duration(Duration.ofMinutes(10)).rerun(true).build(),
                ScheduleEvent.builder().channel(Channel.NED2).localStart(2017, 7, 7, 12, 30).duration(Duration.ofMinutes(10)).rerun(false).build(),
                ScheduleEvent.builder().channel(Channel.NED2).localStart(2017, 7, 7, 17, 30).duration(Duration.ofMinutes(10)).rerun(true).build()
            )
            .build();

        assertThat(MediaObjects.getSortInstant(program).atZone(Schedule.ZONE_ID).toLocalDateTime())
            .isEqualTo(LocalDateTime.of(2017, 7, 7, 12, 30));
    }

    @Test
    public void testSortDateWithPublishStart() throws Exception {
        final Program program = MediaBuilder.program()
            .creationDate(Instant.ofEpochMilli(1))
            .publishStart(Instant.ofEpochMilli(2))
            .build();

        assertThat(MediaObjects.getSortInstant(program)).isEqualTo(Instant.ofEpochMilli(2));
    }

    @Test
    public void testSortDateWithCreationDate() throws Exception {
        final Program program = MediaBuilder.program()
            .creationDate(Instant.ofEpochMilli(1))
            .build();

        assertThat(MediaObjects.getSortInstant(program)).isEqualTo(Instant.ofEpochMilli(1));
    }

    @Test
    public void testSync() throws Exception {
        Website a = new Website("a");
        a.setId(1L);
        Website b = new Website("b");
        b.setId(2L);
        Website c = new Website("c");
        c.setId(3L);
        Website d = new Website("d"); // new

        List<Website> existing = new ArrayList<>(Arrays.asList(a, b, c));
        List<Website> updates = new ArrayList<>(Arrays.asList(b, d, a));
        MediaObjects.integrate(existing, updates);

        assertThat(existing).containsSequence(b, d, a);
    }

    @Test
    public void testFindScheduleEventHonoringOffset() throws Exception {
        final Program program = MediaBuilder.program()
            .scheduleEvents(new ScheduleEvent(Channel.NED1, Instant.ofEpochMilli(100), Duration.ofMillis(100)))
            .build();

        final ScheduleEvent mismatch = new ScheduleEvent(Channel.NED1, Instant.ofEpochMilli(90), Duration.ofMillis(100));
        mismatch.setOffset(Duration.ofMillis(9));
        assertThat(MediaObjects.findScheduleEventHonoringOffset(program, mismatch)).isNull();

        final ScheduleEvent match = new ScheduleEvent(Channel.NED1, Instant.ofEpochMilli(90), Duration.ofMillis(100));
        match.setOffset(Duration.ofMillis(10));
        assertThat(MediaObjects.findScheduleEventHonoringOffset(program, match)).isNotNull();
    }

    @Test
    public void filterOnWorkflow() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Location location1 = new Location("http://www.vpro.nl/1", OwnerType.BROADCASTER);
        Location location2 = new Location("http://www.vpro.nl/2", OwnerType.BROADCASTER);
        location2.setWorkflow(Workflow.DELETED);

        final Program program = MediaBuilder.program()
            .locations(location1, location2)
            .build();

        final Program copy = MediaObjects.filterOnWorkflow(program, Workflow.PUBLICATIONS::contains);
        assertThat(copy.getLocations()).hasSize(1);
        assertThat(copy.getLocations().first().getProgramUrl()).isEqualTo("http://www.vpro.nl/1");

    }

    @Test
    public void filterPublishable() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Location location1 = new Location("http://www.vpro.nl/1", OwnerType.BROADCASTER);
        Location location2 = new Location("http://www.vpro.nl/2", OwnerType.BROADCASTER);
        location2.setWorkflow(Workflow.DELETED);

        final Program program = MediaBuilder.program()
            .locations(location1, location2)
            .build();

        final Program copy = MediaObjects.filterPublishable(program);
        assertThat(copy.getLocations()).hasSize(1);
        assertThat(copy.getLocations().first().getProgramUrl()).isEqualTo("http://www.vpro.nl/1");
    }

    @Test
    public void hasSubtitles_NoSubs() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final Program program = MediaBuilder.program()
            .build();
        assertFalse(program.isHasSubtitles());
    }

    @Test
    public void hasSubtitles_Translation() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

    	final Program program = MediaBuilder.program()
    			.build();
    	program.getAvailableSubtitles().add(new AvailableSubtitles(Locales.DUTCH,
            SubtitlesType.TRANSLATION));
    	assertFalse(program.isHasSubtitles());
    }

    @Test
    public void hasSubtitles_DutchCaption() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final Program program = MediaBuilder.program()
            .build();
        program.getAvailableSubtitles().add(new AvailableSubtitles(Locales.DUTCH,
            SubtitlesType.CAPTION));
        assertTrue(program.isHasSubtitles());
    }

    @Test
    public void getPath() {
        Group g1 = MediaBuilder.group().mid("g1").build();
        Group g2 = MediaBuilder.group().mid("g2").memberOf(g1).build();
        Group g3 = MediaBuilder.group().mid("g3").build();
        Group g4 = MediaBuilder.group().mid("g4").memberOf(g1).build();
        Program p = MediaBuilder.program().mid("p1").memberOf(g2).memberOf(g3).build();
        List<MediaObject> descendants = Arrays.asList(g2, p);

        Optional<List<MemberRef>> path = MediaObjects.getPath(g1, p, descendants);

        log.info("{}", path);
        assertThat(path.get().stream().map(MemberRef::getOwner).collect(Collectors.toList())).containsExactly(g2, g1);



    }
}
