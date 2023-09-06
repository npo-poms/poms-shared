/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXB;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Range;

import nl.vpro.domain.bind.PublicationFilter;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.i18n.Locales;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.util.Ranges;

import static nl.vpro.domain.Changeables.*;
import static nl.vpro.domain.bind.AbstractJsonIterable.DEFAULT_CONSIDER_JSON_INCLUDE;
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
@SuppressWarnings({"deprecation", "OptionalGetWithoutIsPresent"})
@Slf4j
public class MediaObjectsTest {
    static final Instant NOW = Instant.parse("2021-10-26T13:00:00Z");

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

    @Nested
    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    static class Playability {

        @BeforeAll
        static void init (){
           MediaObjectsTest.init();
           MediaObjects.autoCorrectPredictions = false;

        }
        @AfterAll
        static void setClock() {
            CLOCK.remove();
            MediaObjects.autoCorrectPredictions = true;
        }

        private static MediaBuilder.ProgramBuilder fixed(String mid) {
            return MediaBuilder
                .broadcast()
                .mid(mid)
                .creationDate(instant().minus(Duration.ofDays(1)))
                .workflow(Workflow.PUBLISHED)
                ;

        }
        private static MediaBuilder.ProgramBuilder fixed() {
            return fixed("mid_123");
        }

        @Getter()
        public static class ExpectedPlatforms {
            final Platform[] now;
            final Platform[] was;
            final Platform[] willBe;

            final Platform[] publishedNow;
            final Platform[] publishedWas;
            final Platform[] publishedWillBe;

            final Map<Platform, Range<Instant>> ranges;

            public ExpectedPlatforms(
                Platform[] now,
                Platform[] was,
                Platform[] willBe,
                Platform[] publishedNow,
                Platform[] publishedWas,
                Platform[] publishedWillBe,
                Map<Platform, Range<Instant>> ranges
            ) {
                this.now = now;
                this.was = was;
                this.willBe = willBe;
                this.publishedNow = publishedNow;
                this.publishedWas = publishedWas;
                this.publishedWillBe = publishedWillBe;
                this.ranges = ranges;
            }

            public Map<Platform, Long[]> getLongRanges() {
                try {
                    return ranges.entrySet().stream()
                        .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> {
                                Range<Instant> value = entry.getValue();
                                return new Long[]{
                                    value.hasLowerBound() ? value.lowerEndpoint().toEpochMilli() : null,
                                    value.hasUpperBound() ? value.upperEndpoint().toEpochMilli() : null
                                };
                            },
                            (longs, longs2) -> longs2,
                            TreeMap::new
                            )
                        );
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return null;
                }
            }


            public ExpectedPlatforms(Platform[] now, Platform[] was, Platform[] willBe, Map<Platform, Range<Instant>> ranges) {
                this(now, was, willBe, now, was, willBe, ranges);
            }

            public ExpectedPlatforms withPublished(Platform[] publishedNow, Platform[] publishedWas, Platform[] publishedWillBe) {
                return new ExpectedPlatforms(
                    this.now,
                    this.was,
                    this.willBe,
                    publishedNow == null ? this.publishedNow : publishedNow,
                    publishedWas == null ? this.publishedWas : publishedWas,
                    publishedWillBe == null ? this.publishedWillBe : publishedWillBe,
                    this.ranges
                );
            }
        }
        public static ExpectedPlatforms expected(Platform[] now, Platform[] was, Platform[] willBe, Map<Platform, Range<Instant>> ranges) {
            return new ExpectedPlatforms(now, was, willBe, ranges);
        }

        public static final Platform[] A_NONE = new Platform[0];
        public static final Platform[] A_INTERNETVOD = new Platform[] {INTERNETVOD};
        public static final Platform[] A_PLUSVOD = new Platform[] {PLUSVOD};
        public static final Platform[] A_BOTH = new Platform[] {INTERNETVOD, PLUSVOD};

        public static Stream<Arguments> examples() {
            final Instant expired = instant().minusSeconds(10);
            return Stream.of(
                Arguments.of(
                    "just a legacy location",
                    fixed()
                        .locations(
                            Location.builder().platform(null).programUrl("https://bla.com/foobar.mp4").build()
                        )
                        .build(),
                    expected(A_INTERNETVOD, A_NONE, A_NONE, map(INTERNETVOD, null, null))
                ),
                Arguments.of(
                    "just a legacy revoked location",
                   fixed()
                        .locations(Location.builder().platform(null).programUrl("https://bla.com/revoke.foobar.mp4").publishStop(expired).build())
                        .build(),
                    expected(
                        A_NONE,
                        A_INTERNETVOD,
                        A_NONE,
                        map(INTERNETVOD, null, expired)
                    ).withPublished(A_NONE, A_NONE, A_NONE)
                ),
                Arguments.of(
                    "just a legacy windows media location",
                    fixed()
                        .locations(Location.builder().platform(null).programUrl("https://bla.com/foobar.wmv").build())
                        .build(),
                    expected(A_NONE, A_NONE, A_NONE, map())
                ),
                Arguments.of(
                    "a location with explicit INTERNETVOD",
                    fixed()
                        .locations(
                            Location.builder().platform(INTERNETVOD).programUrl("https://bla.com/foobar.mp4").build(),
                            Location.builder().platform(PLUSVOD).workflow(Workflow.DELETED).programUrl("https://bla.com/deleted.mp4").build()
                        )
                        .build(),
                    expected(A_INTERNETVOD, A_NONE, A_NONE, map(INTERNETVOD, null, null))
                ),
                Arguments.of(
                    "an INTERNETVOD prediction but the location became unplayable",
                    fixed("WO_AVRO_013701")
                        .locations(
                            Location.builder().platform(INTERNETVOD).programUrl("http://cgi.omroep.nl/cgi-bin/streams?/nps/cultura/CU_VrijdagvanVredenburg_181209b.wmv").build()
                        )
                        .predictions(Prediction.realized().platform(INTERNETVOD).build())
                        .build(),
                    expected(A_NONE, A_NONE, A_NONE, map())
                ),
                Arguments.of(
                    "a location with explicit PLUSVOD",
                    fixed()
                        .locations(
                            Location.builder().platform(PLUSVOD).programUrl("https://bla.com/foobar.mp4").build(),
                            Location.builder().platform(PLUSVOD).workflow(Workflow.DELETED).programUrl("https://bla.com/deleted.mp4").build(),
                            Location.builder().platform(PLUSVOD).publishStop(expired).programUrl("https://bla.com/expired.mp4").build()
                        )
                        .build(),
                    expected(A_PLUSVOD, A_NONE, A_NONE, map(PLUSVOD, null, null))
                ),
                Arguments.of(
                    "an expired location with explicit INTERNETVOD",
                    fixed()
                        .locations(Location.builder().platform(INTERNETVOD).programUrl("https://bla.com/foobar.mp4").publishStop(expired).build())
                        .build(),
                    expected(A_NONE, A_INTERNETVOD, A_NONE, map(INTERNETVOD,null, expired)).withPublished(A_NONE, A_NONE, A_NONE)
                ),
                Arguments.of(
                    "an expired location with explicit INTERNETVOD",
                    fixed()
                        .locations(Location.builder().platform(INTERNETVOD).programUrl("https://bla.com/foobar.mp4").publishStop(expired).build())
                        .build(),
                    expected(A_NONE, A_INTERNETVOD, A_NONE, map(INTERNETVOD, null, expired)).withPublished(A_NONE, A_NONE, A_NONE)
                ),
                Arguments.of(
                    "an expired location with explicit PLUSVOD",
                    fixed()
                        //.predictions(Prediction.builder().publishStop(expired).platform(PLUSVOD).build())
                        .locations(Location.builder().platform(PLUSVOD).publishStop(expired).programUrl("https://bla.com/foobar.mp4").build())
                        .build(),
                    expected(A_NONE, A_PLUSVOD, A_NONE, map(PLUSVOD, null,  expired))
                        .withPublished(A_NONE, A_NONE, A_NONE)
                ),
                Arguments.of(
                    "realized prediction",
                    fixed()
                        .locations(Location.builder().platform(null).programUrl("https://download.omroep.nl/vpro/algemeen/yous_yay_newemotions/yous_yay_johan fretz.mp3").build())
                        .predictions(Prediction.realized().platform(INTERNETVOD).build())
                        .build(),
                    expected(A_INTERNETVOD, A_NONE, A_NONE, map(INTERNETVOD, null, null))
                ),
                Arguments.of(
                    "realized prediction but no locations",
                    fixed()
                        .predictions(Prediction.realized().platform(PLUSVOD).build())
                        .build(),
                    expected(A_PLUSVOD, A_NONE, A_NONE, map(PLUSVOD, null, null))
                ),

                Arguments.of(
                    "realized prediction and expired location",
                    fixed()
                        .locations(Location.builder().platform(null).publishStop(expired).programUrl("https://bla.com/foobar.mp4").build())
                        .predictions(Prediction.realized().platform(PLUSVOD).build())
                        .build(),
                    expected(A_PLUSVOD, A_INTERNETVOD, A_NONE,
                        map(INTERNETVOD, null, expired,
                            PLUSVOD, null, null)
                    ).withPublished(A_PLUSVOD, A_NONE, A_NONE)
                ),
                Arguments.of(
                    "revoked prediction and expired location",
                    fixed()
                        .locations(
                            Location.builder()
                                .platform(null)
                                .publishStop(expired)
                                .programUrl("https://download.omroep.nl/vpro/algemeen/yous_yay_newemotions/yous_yay_johan fretz.mp3")
                                .build()
                        )
                        .predictions(Prediction.revoked().platform(PLUSVOD).build())
                        .build(),
                    expected(A_NONE, A_BOTH, A_NONE, map(INTERNETVOD, null, expired, PLUSVOD, null, null))
                        .withPublished(A_NONE, A_PLUSVOD, A_NONE)
                ),
                Arguments.of(
                    "revoked prediction but no locations",
                    fixed()
                        .predictions(Prediction.revoked().platform(PLUSVOD).build())
                        .build(),
                    expected(A_NONE, A_PLUSVOD, A_NONE, map(PLUSVOD, null, null))
                ),
                Arguments.of(
                    "realized and revoked prediction but no locations",
                    fixed()
                        .predictions(
                            Prediction.realized().platform(PLUSVOD).build(),
                            Prediction.revoked().platform(INTERNETVOD).build()
                        )
                        .build(),
                    expected(A_PLUSVOD, A_INTERNETVOD, A_NONE, map(PLUSVOD, null, null))
                ),
                Arguments.of(
                    "npo source",
                    fixed()
                        .predictions(
                            Prediction.realized().platform(INTERNETVOD).build()
                        )
                        .locations(
                            Location.builder()
                                .programUrl("npo+drm://internetvod.omroep.nl/VPWON_1322208")
                                .avFileFormat(AVFileFormat.HASP)
                                .owner(OwnerType.AUTHORITY)
                                .build()
                        )
                        .build(),
                    expected(A_INTERNETVOD, A_NONE, A_NONE, map(INTERNETVOD, null, null))
                )
            );
        }

        static Map<Platform, Range<Instant>> map(Object... keyValues) {
            assert keyValues.length % 3 == 0;
            Map<Platform, Range<Instant>> result = new HashMap<>();
            for (int i = 0; i < keyValues.length; i += 3) {
                result.put(
                    (Platform) keyValues[i],
                    Ranges.closedOpen(
                        (Instant) keyValues[i + 1],
                        (Instant) keyValues[i + 2]
                    )
                );
            }
            return result;
        }

        public static Stream<Arguments> nowCases() {
            return examples().map(a -> Arguments.of(a.get()[0], a.get()[1], ((ExpectedPlatforms)a.get()[2]).getNow()));
        }

        public static Stream<Arguments> wasCases() {
            return examples().map(a -> Arguments.of(a.get()[0], a.get()[1], ((ExpectedPlatforms)a.get()[2]).getWas()));
        }
        public static Stream<Arguments> willCases() {
            return examples().map(a -> Arguments.of(a.get()[0], a.get()[1], ((ExpectedPlatforms)a.get()[2]).getWillBe()));
        }

        public static Stream<Arguments> ranges() {
            return examples().map(a -> Arguments.of(a.get()[0], a.get()[1], ((ExpectedPlatforms)a.get()[2]).getRanges()));
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

        @ParameterizedTest
        @MethodSource("ranges")
        void ranges(String description, MediaObject object,  Map<Platform, Range<Instant>> ranges) {
            assertThat(MediaObjects.playableRanges(object)).isEqualTo(ranges);
        }


        @Test
        public void withHasp() {
            Program program = JAXB.unmarshal(MediaObjects.class.getResourceAsStream("/VPWON_1322208.xml"), Program.class);

            Map<Platform, Range<Instant>> platformRangeMap = MediaObjects.playableRanges(program);
            log.info("{}", platformRangeMap);

        }

        @Test
        public void createJsonForJavascriptTests() {
            DEFAULT_CONSIDER_JSON_INCLUDE.set(true);
            PublicationFilter.ENABLED.set(true);
            try {
                final File dest = new File(StringUtils.substringBeforeLast(getClass().getResource(MediaObjectsTest.class.getSimpleName() + ".class").getPath(), "/media-domain/") + "/media-domain/src/test/javascript/cases/playability/");
                dest.mkdirs();
                AtomicInteger count = new AtomicInteger(0);
                examples().forEach(a -> {
                    try {

                        String description = (String) a.get()[0];
                        File file = new File(dest, description + ".json");
                        log.info("{} Creating {}", count.incrementAndGet(), file);
                        ObjectNode result = Jackson2Mapper.getInstance().createObjectNode();
                        result.put("description", description);
                        ExpectedPlatforms expectedPlatforms = (ExpectedPlatforms) a.get()[2];
                        result.put("publishedNowExpectedPlatforms", Jackson2Mapper.getInstance().valueToTree(expectedPlatforms.getPublishedNow()));
                        result.put("publishedWasExpectedPlatforms", Jackson2Mapper.getInstance().valueToTree(expectedPlatforms.getPublishedWas()));
                        result.put("publishedWillExpectedPlatforms", Jackson2Mapper.getInstance().valueToTree(expectedPlatforms.getPublishedWillBe()));
                        result.put("nowExpectedPlatforms", Jackson2Mapper.getInstance().valueToTree(expectedPlatforms.getNow()));
                        result.put("wasExpectedPlatforms", Jackson2Mapper.getInstance().valueToTree(expectedPlatforms.getWas()));
                        result.put("willExpectedPlatforms", Jackson2Mapper.getInstance().valueToTree(expectedPlatforms.getWillBe()));
                        result.put("ranges", Jackson2Mapper.getPrettyPublisherInstance().valueToTree(expectedPlatforms.getLongRanges()));

                        result.put("publishedMediaObject", Jackson2Mapper.getPrettyPublisherInstance().valueToTree(a.get()[1]));
                        result.put("mediaObject", Jackson2Mapper.getPrettyInstance().valueToTree(a.get()[1]));

                        try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
                            Jackson2Mapper.getPrettyPublisherInstance().writer().writeValue(outputStream, result);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
                assertThat(count.get()).isEqualTo(16);
            } finally {
                DEFAULT_CONSIDER_JSON_INCLUDE.remove();
                PublicationFilter.ENABLED.remove();

            }
        }
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
}


