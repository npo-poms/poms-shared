/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.*;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.i18n.Locales;

import static nl.vpro.domain.Changeables.CLOCK;
import static nl.vpro.domain.Changeables.clock;
import static nl.vpro.domain.media.Platform.INTERNETVOD;
import static nl.vpro.domain.media.Platform.PLUSVOD;
import static nl.vpro.domain.media.Schedule.ZONE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Roelof Jan Koekoek
 * @since 1.4
 */
@SuppressWarnings({"deprecation", "OptionalGetWithoutIsPresent", "DataFlowIssue"})
@Slf4j
public class MediaObjectsTest {

    /**
     * The current time in test cases, a fixed time in october 2021.
     */
    public static final Instant NOW = Instant.parse("2021-10-26T13:00:00Z");


    /**
     * Currently only sets the clock in {@link nl.vpro.domain.Changeable}, so automatic fields related to the current time get predictable values.
     * @see #NOW
     */
    @BeforeAll
    static void init() {
        log.info("Setting clock to {}", NOW);
        CLOCK.set(Clock.fixed(NOW , ZONE_ID));
    }

    @AfterAll
    static void setClock() {
        CLOCK.remove();
    }

    @Test
    public void testIdFromUrn() {
        assertThat(MediaObjects.idFromUrn("urn:vpro:media:program:12463402")).isEqualTo(12463402L);
    }


    @Test
    public void sortDate() {
        Program program = new Program();
        assertThat(Math.abs(MediaObjects.getSortInstant(program).toEpochMilli() - clock().millis())).isLessThan(10000);
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
    public void testSortDateWithScheduleEvents() {
        final Program program = MediaBuilder.program()
            .creationDate(Instant.ofEpochMilli(1))
            .publishStart(Instant.ofEpochMilli(2))
            .predictions(
                Prediction.announced()
                    .publishStart(Instant.ofEpochMilli(3)).build())
            .scheduleEvents(
                ScheduleEvent.builder().channel(Channel.NED2).localStart(2015, 1, 1, 12, 30).duration(Duration.ofMinutes(10)).rerun(false).build(),
                ScheduleEvent.builder().channel(Channel.NED2).localStart(2015, 1, 1, 17, 30).duration(Duration.ofMinutes(10)).rerun(true).build(),
                ScheduleEvent.builder().channel(Channel.NED2).localStart(2017, 7, 7, 12, 30).duration(Duration.ofMinutes(10)).rerun(false).build(),
                ScheduleEvent.builder().channel(Channel.NED2).localStart(2017, 7, 7, 17, 30).duration(Duration.ofMinutes(10)).rerun(true).build()
            )
            .build();

        assertThat(MediaObjects.getSortInstant(program).atZone(ZONE_ID).toLocalDateTime())
            .isEqualTo(LocalDateTime.of(2017, 7, 7, 12, 30));
    }

     /**
     * MSE-4094
     */
    @Test
    public void testSortDateWithPredictions() {
        final Program program = MediaBuilder.program()
            .creationDate(Instant.ofEpochMilli(1))
            .publishStart(Instant.ofEpochMilli(2))
            .predictions(
                Prediction.builder()
                    .publishStart(LocalDateTime.of(2017, 7, 7, 12, 30).atZone(ZONE_ID).toInstant()).build())
            .build();

        assertThat(MediaObjects.getSortInstant(program).atZone(ZONE_ID).toLocalDateTime())
            .isEqualTo(LocalDateTime.of(2017, 7, 7, 12, 30));
    }

    @Test
    public void testSortDateWithPublishStart() {
        final Program program = MediaBuilder.program()
            .creationDate(Instant.ofEpochMilli(1))
            .publishStart(Instant.ofEpochMilli(2))
            .build();

        assertThat(MediaObjects.getSortInstant(program)).isEqualTo(Instant.ofEpochMilli(2));
    }

    @Test
    public void testSortDateWithCreationDate() {
        final Program program = MediaBuilder.program()
            .creationDate(Instant.ofEpochMilli(1))
            .build();

        assertThat(MediaObjects.getSortInstant(program)).isEqualTo(Instant.ofEpochMilli(1));
    }

    @Test
    public void testSync() {
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
    public void testFindScheduleEventHonoringOffset() {
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
    public void filterOnWorkflow() {
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
    public void filterPublishable() {
        Location location1 = new Location("http://www.vpro.nl/1", OwnerType.BROADCASTER);
        Location location2 = new Location("http://www.vpro.nl/2", OwnerType.BROADCASTER);
        location2.setWorkflow(Workflow.DELETED);

        final Program program = MediaBuilder.program()
            .locations(location1, location2)
            .build();

        final Program copy = MediaObjects.filterPublishable(program, Instant.now());
        assertThat(copy.getLocations()).hasSize(1);
        assertThat(copy.getLocations().first().getProgramUrl()).isEqualTo("http://www.vpro.nl/1");
    }

    @Test
    public void hasSubtitles_NoSubs() {
        final Program program = MediaBuilder.program()
            .mid("program_123")
            .build();
        assertFalse(program.hasSubtitles());
    }

    @Test
    public void hasSubtitles_Translation() {

        final Program program = MediaBuilder.program()
            .mid("program_123")
            .build();
        program.getAvailableSubtitles().add(AvailableSubtitles.builder().language(Locales.DUTCH).type(SubtitlesType.TRANSLATION).build());
        assertFalse(program.hasSubtitles());
    }

    @Test
    public void hasSubtitles_DutchCaption() {
        final Program program = MediaBuilder.program()
            .mid("program_123")
            .build();
        program.getAvailableSubtitles().add(AvailableSubtitles.builder().language(Locales.DUTCH).type(SubtitlesType.CAPTION).build());
        assertTrue(program.hasSubtitles());
    }


    @Test
    public void getPathShallow() {
        Group g1 = MediaBuilder.group().mid("g1").build();
        Group g2 = MediaBuilder.group().mid("g2").memberOf(g1).build();
        Group g3 = MediaBuilder.group().mid("g3").build();
        Group g4 = MediaBuilder.group().mid("g4").memberOf(g1).build();
        Program p = MediaBuilder.program().mid("p1").memberOf(g2).memberOf(g3).build();
        List<MediaObject> descendants = Arrays.asList(g2, p);

        Optional<List<MemberRef>> path = MediaObjects.getPath(g2, p, descendants);

        assertThat(path.get()).hasSize(1);
        assertThat(path.get().getFirst().getMediaRef()).isEqualTo("g2");
        assertThat(path.get().getFirst().getMember().getMid()).isEqualTo("p1");
    }


    @Test
    public void getPathDeeper() {
        Group g1 = MediaBuilder.group().mid("g1").build();
        Group g2 = MediaBuilder.group().mid("g2").memberOf(g1).build();
        Group g3 = MediaBuilder.group().mid("g3").build();
        Group g4 = MediaBuilder.group().mid("g4").memberOf(g1).build();
        Program p = MediaBuilder.program().mid("p1").memberOf(g2).memberOf(g3).build();
        List<MediaObject> descendants = Arrays.asList(g2, p);

        Optional<List<MemberRef>> path = MediaObjects.getPath(g1, p, descendants);

        log.info("{}", path);
        assertThat(path.get().stream().map(MemberRef::getGroup).collect(Collectors.toList())).containsExactly(g2, g1);


    }

      @Test
    public void testUpdateLocationsForOwner() {
        Location e1 = new Location("aaa", OwnerType.NEBO);
        Location e2 = new Location("bbb", OwnerType.NEBO);
        Location e3 = new Location("ccc", OwnerType.BROADCASTER);
        Program existing = new Program();
        existing.addLocation(e1);
        existing.addLocation(e2);
        existing.addLocation(e3);

        java.time.Duration duration = java.time.Duration.ofMillis(10L);
        Location n1 = new Location("aaa", OwnerType.NEBO);
        n1.setDuration(duration);
        Location n2 = new Location("ddd", OwnerType.NEBO);
        Location n3 = new Location("eee", OwnerType.BROADCASTER);
        Program incoming = new Program();
        incoming.addLocation(n1);
        incoming.addLocation(n2);
        incoming.addLocation(n3);

        MediaObjects.updateAndRemoveLocationsForOwner(incoming, existing, OwnerType.NEBO);

        assertThat(existing.findLocation("bbb"))
            .withFailMessage("Removing deleted location failed").isNull();
        assertThat(existing.findLocation("aaa").getDuration())
            .withFailMessage("Update failed for duration").isEqualTo(duration);
        assertThat(existing.findLocation("ccc")).withFailMessage("Removed location for wrong owner").isNotNull();
        assertThat(existing.findLocation("eee")).withFailMessage("Added location for wrong owner")
            .isNull();
        assertThat(existing.getLocations().size()).withFailMessage("Number of locations does not match").isEqualTo(3);
    }

    @Test
    public void testUpdateLocationsForOwnerWithAvAttributes() {
        Location e1 = new Location("aaa", OwnerType.NEBO);
        Location e2 = new Location("bbb", OwnerType.NEBO)
            .setAvAttributes(new AVAttributes(1111, AVFileFormat.FLV));
        Location e3 = new Location("ccc", OwnerType.NEBO)
            .setAvAttributes(new AVAttributes(2222, AVFileFormat.FLV));


        Location n1 = new Location("aaa", OwnerType.NEBO)
            .setAvAttributes(new AVAttributes(3333, AVFileFormat.MP3));
        Location n2 = new Location("bbb", OwnerType.NEBO)
            .setAvAttributes(new AVAttributes(4444, AVFileFormat.MP3));
        Location n3 = new Location("ccc", OwnerType.NEBO);

        Program existing = new Program();
        Program incoming = new Program();

        existing.addLocation(e1);
        existing.addLocation(e2);
        existing.addLocation(e3);
        incoming.addLocation(n1);
        incoming.addLocation(n2);
        incoming.addLocation(n3);

        MediaObjects.updateLocationsForOwner(incoming, existing, OwnerType.NEBO, false);

        assertThat(existing.findLocation("aaa").getAvAttributes()).isNotNull();
        assertThat(existing.findLocation("bbb").getAvAttributes().getBitrate()).isEqualTo(4444);
        assertThat(existing.findLocation("ccc").getAvAttributes().getBitrate()).isNull();
    }

    @Test
    public void testGetPlatformNamesInLowerCase() {
        Prediction p1 = new Prediction(PLUSVOD);
        Prediction p2 = new Prediction(INTERNETVOD);
        Prediction p3 = new Prediction(Platform.NPOPLUSVOD);
        Collection<Prediction> predictions = new ArrayList<>();
        predictions.add(p1);
        predictions.add(p2);
        predictions.add(p3);

        List<String> result = MediaObjects.getPlannedPlatformNamesInLowerCase(predictions);
        assertThat(result).containsExactlyInAnyOrder("plusvod", "internetvod", "npoplusvod");
    }

    @Test
    public void testGetPlatformNamesInLowerCaseNotAvailable() {
        Prediction p1 = new Prediction(PLUSVOD);
        p1.setPlannedAvailability(false);
        Prediction p2 = new Prediction(INTERNETVOD);
        p2.setPlannedAvailability(false);
        Prediction p3 = new Prediction(Platform.NPOPLUSVOD);
        Collection<Prediction> predictions = new ArrayList<>();
        predictions.add(p1);
        predictions.add(p2);
        predictions.add(p3);

        List<String> result = MediaObjects.getPlannedPlatformNamesInLowerCase(predictions);
        assertThat(result).containsExactlyInAnyOrder("npoplusvod");
    }

    @Test
    public void testGetPlatformNamesInLowerCaseEmptyList() {
        Collection<Prediction> predictions = new ArrayList<>();
        List<String> result = MediaObjects.getPlannedPlatformNamesInLowerCase(predictions);
        assertThat(result).isEmpty();
    }


    @Test
    public void testUpdateLocationsForOwnerWithVidioAttributes() {
        Location e1 = new Location("aaa", OwnerType.NEBO)
            .setAvAttributes(new AVAttributes());
        Location e2 = new Location("bbb", OwnerType.NEBO)
            .setAvAttributes(new AVAttributes()
                .setVideoAttributes(new VideoAttributes(100, 100)));
        Location e3 = new Location("ccc", OwnerType.NEBO)
            .setAvAttributes(new AVAttributes()
                .setVideoAttributes(new VideoAttributes(100, 100)));


        Location n1 = new Location("aaa", OwnerType.NEBO)
            .setAvAttributes(new AVAttributes()
                .setVideoAttributes(new VideoAttributes(100, 100)));
        Location n2 = new Location("bbb", OwnerType.NEBO)
            .setAvAttributes(new AVAttributes()
                .setVideoAttributes(new VideoAttributes(200, 200)));
        Location n3 = new Location("ccc", OwnerType.NEBO)
            .setAvAttributes(new AVAttributes());

        Program existing = new Program();
        Program incoming = new Program();

        existing.addLocation(e1);
        existing.addLocation(e2);
        existing.addLocation(e3);
        incoming.addLocation(n1);
        incoming.addLocation(n2);
        incoming.addLocation(n3);

        MediaObjects.updateLocationsForOwner(incoming, existing, OwnerType.NEBO, false);

        assertThat(existing.findLocation("aaa").getAvAttributes().getVideoAttributes()).withFailMessage("Adding new VideoAttributes failed").isNotNull();
        assertThat( existing.findLocation("bbb").getAvAttributes().getVideoAttributes().getHorizontalSize()).withFailMessage("Updating VideoAttributes failed").isEqualTo(200);
        assertThat( existing.findLocation("ccc").getAvAttributes().getVideoAttributes()).withFailMessage("Removing deleted VideoAttributes failed").isNull();
    }

    public static class Impl implements UpdatableIdentifiable<Integer, Impl> {
        final int  id;
        String value;

        public Impl(int id, String value) {
            this.id = id;
            this.value = value;
        }

        @Override
        public Integer getId() {
            return id;
        }

        @Override
        public void update(Impl from) {
            this.value = from.value;
        }
    }

    @Test
    public void integrateShrink() {
        List<Impl> existing = new ArrayList<>(Arrays.asList(new Impl(0,  "a"), new Impl(1, "b"), new Impl(2, "c")));

        List<Impl> incoming = new ArrayList<>(Arrays.asList(new Impl(0,  "a"), new Impl(1, "x")));

        MediaObjects.integrate(existing, incoming);
        assertThat(existing.stream().map(i -> i.value)).containsExactly("a", "x");
    }

    @Test
    public void integrateShrinkAndGrow() {
        List<Impl> existing = new ArrayList<>(Arrays.asList(new Impl(0,  "a"), new Impl(1, "b"), new Impl(2, "c"), new Impl(4, "z")));

        List<Impl> incoming = new ArrayList<>(Arrays.asList(new Impl(0,  "a"), new Impl(1, "x"), new Impl(3, "y")));

        MediaObjects.integrate(existing, incoming);
        assertThat(existing.stream().map(i -> i.value)).containsExactly("a", "x", "y");
    }

    @Test
    public void integrateGrow() {
        List<Impl> existing = new ArrayList<>(Arrays.asList(new Impl(0,  "a"), new Impl(1, "b")));

        List<Impl> incoming = new ArrayList<>(Arrays.asList(new Impl(0,  "a"), new Impl(1, "x"), new Impl(3, "y")));

        MediaObjects.integrate(existing, incoming);
        assertThat(existing.stream().map(i -> i.value)).containsExactly("a", "x", "y");

    }


    @Test
    public void generations() {
        final Group g1  = MediaBuilder.group().mid("g1").type(GroupType.SERIES).build();
        final Group g2  = MediaBuilder.group().mid("g2").type(GroupType.SERIES).build();
        final Group g3  = MediaBuilder.group().mid("g3").type(GroupType.SEASON).memberOf(g2).build();
        final Program program = MediaBuilder.program().memberOf(g1).memberOf(g3).build();
        List<Generation> generations = MediaObjects.getAncestorGenerations(program).toList();

        Generation parents = generations.getFirst();
        log.info("{}", parents);
        Generation grandparents = parents.up();
        log.info("{}", grandparents);


        assertThat(generations).hasSize(2);

        log.info("{}", generations);


    }
}


