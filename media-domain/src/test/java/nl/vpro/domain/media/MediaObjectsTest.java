/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.vpro.domain.Embargos;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.i18n.Locales;
import nl.vpro.jackson2.Jackson2Mapper;

import static nl.vpro.domain.Embargos.CLOCK;
import static nl.vpro.domain.Embargos.clock;
import static nl.vpro.domain.media.Platform.INTERNETVOD;
import static nl.vpro.domain.media.Platform.PLUSVOD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Roelof Jan Koekoek
 * @since 1.4
 */
@SuppressWarnings({"deprecation", "ConstantConditions"})
@Slf4j
public class MediaObjectsTest {
    static final Instant NOW = Instant.parse("2021-10-26T13:00:00Z");
    @BeforeAll
    static void init() {
        log.info("Setting clock to {}", NOW);
        CLOCK.set(Clock.fixed(NOW , Schedule.ZONE_ID));
    }
    @AfterAll
    static void setClock() {
        CLOCK.remove();
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

        assertThat(MediaObjects.getSortInstant(program).atZone(Schedule.ZONE_ID).toLocalDateTime())
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
                    .publishStart(LocalDateTime.of(2017, 7, 7, 12, 30).atZone(Schedule.ZONE_ID).toInstant()).build())
            .build();

        assertThat(MediaObjects.getSortInstant(program).atZone(Schedule.ZONE_ID).toLocalDateTime())
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
            .build();
        assertFalse(program.hasSubtitles());
    }

    @Test
    public void hasSubtitles_Translation() {

        final Program program = MediaBuilder.program().build();
        program.getAvailableSubtitles().add(AvailableSubtitles.builder().language(Locales.DUTCH).type(SubtitlesType.TRANSLATION).build());
        assertFalse(program.hasSubtitles());
    }

    @Test
    public void hasSubtitles_DutchCaption() {
        final Program program = MediaBuilder.program()
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
        assertThat(path.get().get(0).getMediaRef()).isEqualTo("g2");
        assertThat(path.get().get(0).getMember().getMid()).isEqualTo("p1");
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

    public static class Playability {

        @BeforeAll
        static void init (){
           MediaObjectsTest.init();
        }
        @AfterAll
        static void setClock() {
            CLOCK.remove();
        }

        private static MediaBuilder.ProgramBuilder fixed() {
            return MediaBuilder
                .broadcast()
                .mid("mid_123")
                .creationDate(Embargos.clock().instant().minus(Duration.ofDays(1)))
                .workflow(Workflow.PUBLISHED)
                ;

        }

        public static final Platform[] A_NONE = new Platform[0];
        public static final Platform[] A_INTERNETVOD = new Platform[] {INTERNETVOD};
        public static final Platform[] A_PLUSVOD = new Platform[] {PLUSVOD};
        public static final Platform[] A_BOTH = new Platform[] {INTERNETVOD, PLUSVOD};
        public static Stream<Arguments> examples() {
            return Stream.of(
                Arguments.of(
                    "just a legacy location",
                    fixed()
                        .locations(Location.builder().platform(null).programUrl("https://bla.com/foobar.mp4").build())
                        .build(),
                    A_INTERNETVOD,
                    A_NONE,
                    A_NONE
                ),
                Arguments.of(
                    "just a legacy revoked location",
                   fixed()
                        .locations(Location.builder().platform(null).programUrl("https://bla.com/foobar.mp4").publishStop(NOW.minusSeconds(10)).build())
                        .build(),
                    A_NONE,
                    A_INTERNETVOD,
                    A_NONE
                ),
                Arguments.of(
                    "just a legacy windows media location",
                    fixed()
                        .locations(Location.builder().platform(null).programUrl("https://bla.com/foobar.wmv").build())
                        .build(),
                    A_NONE,
                    A_NONE,
                    A_NONE
                ),
                Arguments.of(
                    "a location with explicit INTERNETVOD",
                    fixed()
                        .locations(
                            Location.builder().platform(INTERNETVOD).programUrl("https://bla.com/foobar.mp4").build(),
                            Location.builder().platform(PLUSVOD).workflow(Workflow.DELETED).programUrl("https://bla.com/deleted.mp4").build()
                        )
                        .build(),
                    A_INTERNETVOD,
                    A_NONE,
                    A_NONE
                ),
                Arguments.of(
                    "a location with explicit PLUSVOD",
                    fixed()
                        .locations(
                            Location.builder().platform(PLUSVOD).programUrl("https://bla.com/foobar.mp4").build(),
                            Location.builder().platform(PLUSVOD).workflow(Workflow.DELETED).programUrl("https://bla.com/deleted.mp4").build(),
                            Location.builder().platform(PLUSVOD).publishStop(clock().instant().minusSeconds(10)).programUrl("https://bla.com/expired.mp4").build()
                        )
                        .build(),
                    A_PLUSVOD,
                    A_NONE,
                    A_NONE
                ),
                Arguments.of(
                    "an expired location with explicit INTERNETVOD",
                    fixed()
                        .locations(Location.builder().platform(INTERNETVOD).programUrl("https://bla.com/foobar.mp4").publishStop(NOW.minusSeconds(10)).build())
                        .build(),
                    A_NONE,
                    A_INTERNETVOD,
                    A_NONE
                ),
                Arguments.of(
                    "an expired location with explicit INTERNETVOD",
                    fixed()
                        .locations(Location.builder().platform(INTERNETVOD).programUrl("https://bla.com/foobar.mp4").publishStop(NOW.minusSeconds(10)).build())
                        .build(),
                    A_NONE,
                    A_INTERNETVOD,
                    A_NONE
                ),
                Arguments.of(
                    "an expired location with explicit PLUSVOD",
                    fixed()
                        .locations(Location.builder().platform(PLUSVOD).publishStop(clock().instant().minusSeconds(10)).programUrl("https://bla.com/foobar.mp4").build())
                        .build(),
                    A_NONE,
                    A_PLUSVOD,
                    A_NONE
                ),


                Arguments.of(
                    "realized prediction",
                    fixed()
                        .locations(Location.builder().platform(null).programUrl("https://bla.com/foobar.mp4").build())
                        .predictions(Prediction.realized().platform(INTERNETVOD).build())
                        .build(),
                    A_INTERNETVOD,
                    A_NONE,
                    A_NONE
                ),
                Arguments.of(
                    "realized prediction but no locations",
                    fixed()
                        .predictions(Prediction.realized().platform(PLUSVOD).build())
                        .build(),
                    A_PLUSVOD,
                    A_NONE,
                    A_NONE
                ),

                Arguments.of(
                    "realized prediction and expired location",
                    fixed()
                        .locations(Location.builder().platform(null).publishStop(clock().instant().minusSeconds(10)).programUrl("https://bla.com/foobar.mp4").build())
                        .predictions(Prediction.realized().platform(PLUSVOD).build())
                        .build(),
                    A_PLUSVOD,
                    A_INTERNETVOD,
                    A_NONE
                ),
                Arguments.of(
                    "revoked prediction and expired location",
                    fixed()
                        .locations(
                            Location.builder().platform(null).publishStop(clock().instant().minusSeconds(10)).programUrl("https://bla.com/foobar.mp4").build()
                        )
                        .predictions(Prediction.revoked().platform(PLUSVOD).build())
                        .build(),
                    A_NONE,
                    A_BOTH,
                    A_NONE
                ),
                Arguments.of(
                    "revoked prediction but no locations",
                    fixed()
                        .predictions(Prediction.revoked().platform(PLUSVOD).build())
                        .build(),
                    A_NONE,
                    A_PLUSVOD,
                    A_NONE
                ),
                Arguments.of(
                    "realized and revoked prediction but no locations",
                    fixed()
                        .predictions(
                            Prediction.realized().platform(PLUSVOD).build(),
                            Prediction.revoked().platform(INTERNETVOD).build()
                        )
                        .build(),
                    A_PLUSVOD,
                    A_INTERNETVOD,
                    A_NONE
                )
            );
        }
        public static Stream<Arguments> nowCases() {
            return examples().map(a -> Arguments.of(a.get()[0], a.get()[1], a.get()[2]));
        }

        public static Stream<Arguments> wasCases() {
            return examples().map(a -> Arguments.of(a.get()[0], a.get()[1], a.get()[3]));
        }
        public static Stream<Arguments> willCases() {
            return examples().map(a -> Arguments.of(a.get()[0], a.get()[1], a.get()[4]));
        }

        @Test
        void isPlayable() {
            // this is a bit strange, this call just looks at streaming status:
            assertThat(MediaObjects.isPlayable(nowCases().findFirst().map(a -> (MediaObject) a.get()[1]).orElseThrow(RuntimeException::new))).isFalse();
        }

        @ParameterizedTest
        @MethodSource("nowCases")
        void nowPlayable(String description, MediaObject object, Platform[] expectedPlatforms) throws JsonProcessingException {
            assertThat(MediaObjects.nowPlayable(object)).containsExactly(expectedPlatforms);
            // should still be valid if mediaobject gets published
            MediaObject published = Jackson2Mapper.getLenientInstance().treeToValue(Jackson2Mapper.getPublisherInstance().valueToTree(object), MediaObject.class);
            assertThat(MediaObjects.nowPlayable(published)).containsExactly(expectedPlatforms);
        }


        @ParameterizedTest
        @MethodSource("wasCases")
        void wasPlayable(String description, MediaObject object, Platform[] expectedPlatforms) throws JsonProcessingException {
            assertThat(MediaObjects.wasPlayable(object)).containsExactly(expectedPlatforms);
            // should still be valid if mediaobject gets published
            MediaObject published = Jackson2Mapper.getLenientInstance().treeToValue(Jackson2Mapper.getPublisherInstance().valueToTree(object), MediaObject.class);
            assertThat(MediaObjects.wasPlayable(published)).containsExactly(expectedPlatforms);
        }



        @ParameterizedTest
        @MethodSource("willCases")
        void willBePlayable(String description, MediaObject object, Platform[] expectedPlatforms) throws JsonProcessingException {
            assertThat(MediaObjects.willBePlayable(object)).containsExactly(expectedPlatforms);
            // should still be valid if mediaobject gets published
            MediaObject published = Jackson2Mapper.getLenientInstance().treeToValue(Jackson2Mapper.getPublisherInstance().valueToTree(object), MediaObject.class);
            assertThat(MediaObjects.willBePlayable(published)).containsExactly(expectedPlatforms);
        }

        @Test
        public void createJsonForJavascriptTests() {

            File dest = new File(StringUtils.substringBeforeLast(getClass().getResource(MediaObjectsTest.class.getSimpleName() + ".class").getPath(), "/media-domain/") + "/media-domain/src/test/javascript/cases/");
            dest.mkdirs();
            examples().forEach(a -> {
                try {
                    String description = (String) a.get()[0];
                    ObjectNode result = Jackson2Mapper.getInstance().createObjectNode();
                    result.put("description", description);
                    result.put("nowExpectedPlatforms", Jackson2Mapper.getInstance().valueToTree(a.get()[2]));
                    result.put("wasExpectedPlatforms", Jackson2Mapper.getInstance().valueToTree(a.get()[3]));
                    result.put("willExpectedPlatforms", Jackson2Mapper.getInstance().valueToTree(a.get()[4]));
                    result.put("mediaObject", Jackson2Mapper.getInstance().valueToTree(a.get()[1]));
                    Jackson2Mapper.getPrettyPublisherInstance().writeValueAsString(result);
                    File file = new File(dest,  description + ".json");
                    try (OutputStream outputStream = new FileOutputStream(file)) {
                        Jackson2Mapper.getPrettyPublisherInstance().writer().writeValue(outputStream, result);
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }
}


