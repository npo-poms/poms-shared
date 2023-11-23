package nl.vpro.domain.media;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
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
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.util.Ranges;

import static nl.vpro.domain.Changeables.CLOCK;
import static nl.vpro.domain.Changeables.instant;
import static nl.vpro.domain.bind.AbstractJsonIterable.DEFAULT_CONSIDER_JSON_INCLUDE;
import static nl.vpro.domain.media.Platform.INTERNETVOD;
import static nl.vpro.domain.media.Platform.PLUSVOD;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"ResultOfMethodCallIgnored", "DataFlowIssue"})
@Log4j2
class MediaObjectsPlayabilityTest {

    @BeforeAll
    static void init() {
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
    public static final Platform[] A_INTERNETVOD = new Platform[]{INTERNETVOD};
    public static final Platform[] A_PLUSVOD = new Platform[]{PLUSVOD};
    public static final Platform[] A_BOTH = new Platform[]{INTERNETVOD, PLUSVOD};

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
                expected(A_INTERNETVOD, A_NONE, A_NONE, mapRanges(INTERNETVOD, null, null))
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
                    mapRanges(INTERNETVOD, null, expired)
                ).withPublished(A_NONE, A_NONE, A_NONE)
            ),
            Arguments.of(
                "just a legacy windows media location",
                fixed()
                    .locations(Location.builder().platform(null).programUrl("https://bla.com/foobar.wmv").build())
                    .build(),
                expected(A_NONE, A_NONE, A_NONE, mapRanges())
            ),
            Arguments.of(
                "a location with explicit INTERNETVOD",
                fixed()
                    .locations(
                        Location.builder().platform(INTERNETVOD).programUrl("https://bla.com/foobar.mp4").build(),
                        Location.builder().platform(PLUSVOD).workflow(Workflow.DELETED).programUrl("https://bla.com/deleted.mp4").build()
                    )
                    .build(),
                expected(A_INTERNETVOD, A_NONE, A_NONE, mapRanges(INTERNETVOD, null, null))
            ),
            Arguments.of(
                "an INTERNETVOD prediction but the location became unplayable",
                fixed("WO_AVRO_013701")
                    .locations(
                        Location.builder().platform(INTERNETVOD).programUrl("http://cgi.omroep.nl/cgi-bin/streams?/nps/cultura/CU_VrijdagvanVredenburg_181209b.wmv").build()
                    )
                    .predictions(Prediction.realized().platform(INTERNETVOD).build())
                    .build(),
                expected(A_NONE, A_NONE, A_NONE, mapRanges())
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
                expected(A_PLUSVOD, A_NONE, A_NONE, mapRanges(PLUSVOD, null, null))
            ),
            Arguments.of(
                "an expired location with explicit INTERNETVOD",
                fixed()
                    .locations(Location.builder().platform(INTERNETVOD).programUrl("https://bla.com/foobar.mp4").publishStop(expired).build())
                    .build(),
                expected(A_NONE, A_INTERNETVOD, A_NONE, mapRanges(INTERNETVOD, null, expired)).withPublished(A_NONE, A_NONE, A_NONE)
            ),
            Arguments.of(
                "an expired location with explicit INTERNETVOD",
                fixed()
                    .locations(Location.builder().platform(INTERNETVOD).programUrl("https://bla.com/foobar.mp4").publishStop(expired).build())
                    .build(),
                expected(A_NONE, A_INTERNETVOD, A_NONE, mapRanges(INTERNETVOD, null, expired)).withPublished(A_NONE, A_NONE, A_NONE)
            ),
            Arguments.of(
                "an expired location with explicit PLUSVOD",
                fixed()
                    //.predictions(Prediction.builder().publishStop(expired).platform(PLUSVOD).build())
                    .locations(Location.builder().platform(PLUSVOD).publishStop(expired).programUrl("https://bla.com/foobar.mp4").build())
                    .build(),
                expected(A_NONE, A_PLUSVOD, A_NONE, mapRanges(PLUSVOD, null, expired))
                    .withPublished(A_NONE, A_NONE, A_NONE)
            ),
            Arguments.of(
                "realized prediction",
                fixed()
                    .locations(Location.builder().platform(null).programUrl("https://download.omroep.nl/vpro/algemeen/yous_yay_newemotions/yous_yay_johan fretz.mp3").build())
                    .predictions(Prediction.realized().platform(INTERNETVOD).build())
                    .build(),
                expected(A_INTERNETVOD, A_NONE, A_NONE, mapRanges(INTERNETVOD, null, null))
            ),
            Arguments.of(
                "realized prediction and expired location",
                fixed()
                    .locations(Location.builder().platform(null).publishStop(expired).programUrl("https://bla.com/foobar.mp4").build())
                    .locations(Location.builder().platform(PLUSVOD).programUrl("https://bla.com/foobar-plusvod.mp4").build())
                    .predictions(Prediction.realized().platform(PLUSVOD).build())
                    .build(),
                expected(A_PLUSVOD, A_INTERNETVOD, A_NONE,
                    mapRanges(INTERNETVOD, null, expired,
                        PLUSVOD, null, null)
                ).withPublished(A_PLUSVOD, A_NONE, A_NONE)
            ),
            Arguments.of(
                "revoked prediction and expired location", // if the expired location is visible it can be used
                fixed()
                    .locations(
                        Location.builder()
                            .platform(null)
                            .publishStop(expired)
                            .programUrl("https://download.omroep.nl/vpro/algemeen/yous_yay_newemotions/yous_yay_johan fretz.mp3")
                            .build()
                    )
                    .predictions(Prediction.revoked().publishStop(expired).platform(PLUSVOD).build())
                    .build(),
                expected(A_NONE, A_BOTH, A_NONE,
                    mapRanges(
                        INTERNETVOD, null, expired,
                        PLUSVOD, null, expired
                    ))
                    .withPublished(A_NONE, A_PLUSVOD, A_NONE)
            ),
            Arguments.of(
                "revoked prediction but no locations",
                fixed()
                    .predictions(Prediction.revoked().publishStop(expired).platform(PLUSVOD).build())
                    .build(),
                expected(A_NONE, A_PLUSVOD, A_NONE,
                    mapRanges(PLUSVOD, null, expired))
            ),
            Arguments.of(
                "realized and revoked prediction",
                fixed()
                    .predictions(
                        Prediction.realized().platform(PLUSVOD).build(),
                        Prediction.revoked().platform(INTERNETVOD).build()
                    )
                    .locations(Location.builder().platform(PLUSVOD).programUrl("https://bla.com/foobar-plusvod.mp4").build())
                    .build(),
                expected(A_PLUSVOD, A_INTERNETVOD, A_NONE, mapRanges(PLUSVOD, null, null))
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
                expected(A_INTERNETVOD, A_NONE, A_NONE, mapRanges(INTERNETVOD, null, null))
            )
        );
    }

    static Map<Platform, Range<Instant>> mapRanges(Object... keyValues) {
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
        return examples().map(a -> Arguments.of(a.get()[0], a.get()[1], ((ExpectedPlatforms) a.get()[2]).getNow()));
    }

    public static Stream<Arguments> wasCases() {
        return examples().map(a -> Arguments.of(a.get()[0], a.get()[1], ((ExpectedPlatforms) a.get()[2]).getWas()));
    }

    public static Stream<Arguments> willCases() {
        return examples().map(a -> Arguments.of(a.get()[0], a.get()[1], ((ExpectedPlatforms) a.get()[2]).getWillBe()));
    }

    public static Stream<Arguments> ranges() {
        return examples().map(a -> Arguments.of(a.get()[0], a.get()[1], ((ExpectedPlatforms) a.get()[2]).getRanges()));
    }

    @Test
    void isPlayable() {
        assertThat(MediaObjects.isPlayable(nowCases()
            .findFirst()
            .map(a -> (MediaObject) a.get()[1]).orElseThrow(RuntimeException::new))
        ).isTrue();
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
    void ranges(String description, MediaObject object, Map<Platform, Range<Instant>> ranges) {
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
                    result.set("publishedNowExpectedPlatforms",
                            Jackson2Mapper.getInstance().valueToTree(expectedPlatforms.getPublishedNow()));
                    result.set("publishedWasExpectedPlatforms", Jackson2Mapper.getInstance().valueToTree(expectedPlatforms.getPublishedWas()));
                    result.set("publishedWillExpectedPlatforms", Jackson2Mapper.getInstance().valueToTree(expectedPlatforms.getPublishedWillBe()));
                    result.set("nowExpectedPlatforms", Jackson2Mapper.getInstance().valueToTree(expectedPlatforms.getNow()));
                    result.set("wasExpectedPlatforms", Jackson2Mapper.getInstance().valueToTree(expectedPlatforms.getWas()));
                    result.set("willExpectedPlatforms", Jackson2Mapper.getInstance().valueToTree(expectedPlatforms.getWillBe()));
                    result.set("ranges", Jackson2Mapper.getPrettyPublisherInstance().valueToTree(expectedPlatforms.getLongRanges()));

                    result.set("publishedMediaObject", Jackson2Mapper.getPrettyPublisherInstance().valueToTree(a.get()[1]));
                    result.set("mediaObject", Jackson2Mapper.getPrettyInstance().valueToTree(a.get()[1]));

                    try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
                        Jackson2Mapper.getPrettyPublisherInstance().writer().writeValue(outputStream, result);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
            assertThat(count.get()).isEqualTo(15);
        } finally {
            DEFAULT_CONSIDER_JSON_INCLUDE.remove();
            PublicationFilter.ENABLED.remove();

        }
    }
}
