/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;

import nl.vpro.domain.bind.PublicationFilter;
import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.gtaa.GTAAStatus;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.domain.user.Editor;
import nl.vpro.i18n.Locales;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import static java.nio.charset.StandardCharsets.UTF_8;
import static nl.vpro.domain.Changeables.CLOCK;
import static nl.vpro.domain.Changeables.instant;
import static nl.vpro.domain.media.MediaTestDataBuilder.group;
import static nl.vpro.domain.media.MediaTestDataBuilder.program;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Roelof Jan Koekoek
 * @since 1.7
 */
@SuppressWarnings("DataFlowIssue")
@Slf4j
@Isolated
@Execution(ExecutionMode.SAME_THREAD)
public class MediaObjectJsonSchemaTest {

    @BeforeEach
    public void before() {
        CLOCK.set(Clock.fixed(Instant.ofEpochMilli(10), Schedule.ZONE_ID));
        Locale.setDefault(Locales.DUTCH);
        ClassificationServiceLocator.setInstance(new MediaClassificationService());
        MediaObjects.autoCorrectPredictions = false;
        PublicationFilter.ENABLED.remove();
    }

    @AfterEach
    public void cleanUp() {
        CLOCK.remove();
    }



    @Test
    public void testMidAndType() {
        String expected = "{\"objectType\":\"program\",\"mid\":\"MID_000001\",  \"type\" : \"CLIP\", \"embeddable\":true,\"broadcasters\":[],\"genres\":[], \"countries\":[],\"languages\":[]}";

        Program program = program().lean().type(ProgramType.CLIP).mid("MID_000001").build();
        Jackson2TestUtil.roundTripAndSimilarAndEquals(program, expected);
    }



    @Test
    public void testUnknownType() throws Exception {
        String odd = "{\"objectType\":\"program\",\"type\": \"FOOBAR\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[] }";

        Program program = Jackson2Mapper.getLenientInstance().readValue(odd, Program.class);
        assertThat(program.getType()).isNull();
    }

    @Test
    public void testHasSubtitles() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"hasSubtitles\":true,\"countries\":[],\"languages\":[],\"availableSubtitles\":[{\"language\":\"nl\",\"type\":\"CAPTION\"}]}";

        Program program = program().lean().withSubtitles().build();
        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }

    @Test
    public void testDatesCreatedAndModified() throws Exception {
        String expected = "{\"objectType\":\"program\",\"sortDate\":1,\"creationDate\":1,\"lastModified\":7200000,\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().creationInstant(Instant.ofEpochMilli(1))
            .lastModified(Instant.ofEpochSecond(2 * 60 * 60))
            .build();
        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }

    @Test
    public void testCreatedAndModifiedBy() throws Exception {
        Program program = program().lean().withCreatedBy().withLastModifiedBy().build();

        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[]}";
        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }

    @Test
    public void testPublishStartStop() throws Exception {
        String expected = "{\"objectType\":\"program\",\"sortDate\":1,\"publishStart\":1,\"publishStop\":7200000,\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().publishStart(Instant.ofEpochMilli(1))
            .publishStop(Instant.ofEpochSecond(2 * 60 * 60)).build();
        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }

    @Test
    public void testMergedTo() throws Exception {
        String expected = "{\"objectType\":\"program\",\"workflow\":\"MERGED\",\"mergedTo\":\"MERGE_TARGET\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean()
            .mergedTo(program()
                .mid("MERGE_TARGET")
                .build()
            ).build();
        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }

    @Test
    public void testCrids() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"crids\":[\"crid://bds.tv/9876\",\"crid://tmp.fragment.mmbase.vpro.nl/1234\"],\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withCrids().build();
        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }

    @Test
    public void testBroadcasters() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[{\"id\":\"BNN\",\"value\":\"BNN\"},{\"id\":\"AVRO\",\"value\":\"AVRO\"}],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withBroadcasters().build();
        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }


    @Test
    public void testExclusives() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"exclusives\":[\"STERREN24\",\"3VOOR12_GRONINGEN\"],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withPortalRestrictions().build();
        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }

    @Test
    public void testRegions() {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"regions\":[\"NL\",\"BENELUX\",\"TVVOD:NL\"],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withGeoRestrictions().build();

        Program rounded = Jackson2TestUtil.roundTripAndSimilar(program, expected);
        assertThat(rounded.getGeoRestrictions()).hasSize(3);
        assertThat(rounded.getGeoRestrictions().first()).isEqualTo(program.getGeoRestrictions().first());
        assertThat(rounded.getGeoRestrictions().contains(program.getGeoRestrictions().first())).isTrue();

        // FAILS because json in this is not a complete description (start/stop times are missing!)
        //assertThat(new ArrayList<>(rounded.getGeoRestrictions())).containsExactlyElementsOf(new ArrayList<>(program.getGeoRestrictions()));
    }

    @Test
    public void testPredictions() throws Exception {
        String expected = "{\"objectType\":\"program\",\"sortDate\":10,\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"predictions\":[{\"state\":\"REALIZED\",\"publishStart\":10,\"publishStop\":100,\"platform\":\"TVVOD\"}]}";

        Program program = program().lean().build();

        Prediction prediction = new Prediction(Platform.TVVOD);
        prediction.setState(Prediction.State.REALIZED);
        prediction.setPublishStartInstant(Instant.ofEpochMilli(10));
        prediction.setPublishStopInstant(Instant.ofEpochMilli(100));

        program.getPredictions().add(prediction);

        Prediction nonavailable = Prediction.builder()
            .plannedAvailability(false)
            .platform(Platform.INTERNETVOD)
            .build();

        program.getPredictions().add(nonavailable);

        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }

    @Test
    public void testUnmarshalPredictions() throws JsonProcessingException {
        String input = """
            {
              "objectType" : "program",
              "mid" : "MID_123",
              "workflow" : "FOR_PUBLICATION",
              "sortDate" : 1425596400000,
              "creationDate" : 1425596400000,
              "lastModified" : 1425600000000,
              "embeddable" : true,
              "predictions" : [ {
                  "state" : "REALIZED",
                  "platform" : "INTERNETVOD"
                } ],
              "locations" : [ {
                "programUrl" : "https://www.vpro.nl",
                "avAttributes" : {
                  "avFileFormat" : "UNKNOWN"
                },
                "owner" : "BROADCASTER",
                "creationDate" : 1636131600000,
                "workflow" : "PUBLISHED",
                "platform" : "INTERNETVOD"
              } ],
              "publishDate" : 1425603600000
            }
        """;
        Program program = Jackson2Mapper.getLenientInstance().readerFor(Program.class).readValue(input);
        assertThat(program.getPredictions()).hasSize(1);
        assertThat(program.getPredictionsForXml()).hasSize(1);
    }

    @Test
    public void testBackwardsCompatibleUnmarshalPredictions() throws IOException {
        String backwards = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"hasSubtitles\":false,\"countries\":[],\"languages\":[],\"predictions\":[\"INTERNETVOD\"]}";
        Program program = Jackson2Mapper.getInstance().readValue(new StringReader(backwards), Program.class);
        assertThat(program.getPredictions()
            .iterator()
            .next()
            .getPlatform())
            .isEqualTo(Platform.INTERNETVOD);
    }

    @Test
    public void testTitles() throws Exception {
        String expected = """
            {
              "objectType" : "program",
              "embeddable" : true,
              "broadcasters" : [ ],
              "titles" : [ {
                "value" : "Main title",
                "owner" : "BROADCASTER",
                "type" : "MAIN"
              }, {
                "value" : "Main title MIS",
                "owner" : "MIS",
                "type" : "MAIN"
              }, {
                "value" : "Short title",
                "owner" : "BROADCASTER",
                "type" : "SHORT"
              }, {
                "value" : "Episode title MIS",
                "owner" : "MIS",
                "type" : "SUB"
              } ],
              "genres" : [ ],
              "countries" : [ ],
              "languages" : [ ],
              "expandedTitles" : [ {
                "value" : "Main title",
                "owner" : "BROADCASTER",
                "type" : "MAIN"
              }, {
                "value" : "Main title",
                "owner" : "NPO",
                "type" : "MAIN"
              }, {
                "value" : "Short title",
                "owner" : "BROADCASTER",
                "type" : "SHORT"
              }, {
                "value" : "Short title",
                "owner" : "NPO",
                "type" : "SHORT"
              }, {
                "value" : "Episode title MIS",
                "owner" : "BROADCASTER",
                "type" : "SUB"
              }, {
                "value" : "Episode title MIS",
                "owner" : "NPO",
                "type" : "SUB"
              }, {
                "value" : "Main title",
                "owner" : "BROADCASTER",
                "type" : "LEXICO"
              }, {
                "value" : "Main title",
                "owner" : "NPO",
                "type" : "LEXICO"
              } ]
            }""";

        Program program = program().lean().withTitles().build();
        String actual = toPublisherJson(program);

        Jackson2TestUtil.assertThatJson(actual).isSimilarTo(expected);
    }

    @Test
    public void testDescriptions() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"descriptions\":[{\"value\":\"Main description\",\"owner\":\"BROADCASTER\",\"type\":\"MAIN\"},{\"value\":\"Main description MIS\",\"owner\":\"MIS\",\"type\":\"MAIN\"},{\"value\":\"Short description\",\"owner\":\"BROADCASTER\",\"type\":\"SHORT\"},{\"value\":\"Episode description MIS\",\"owner\":\"MIS\",\"type\":\"EPISODE\"}],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withDescriptions().build();
        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }

    @Test
    public void testGenres() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[{\"id\":\"3.0.1.7.21\",\"terms\":[\"Informatief\",\"Nieuws/actualiteiten\"]},{\"id\":\"3.0.1.8.25\",\"terms\":[\"Documentaire\",\"Natuur\"]}],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withGenres().build();
        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }



    @Test
    public void testReverseGenres() throws Exception {
        String input = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[{\"id\":\"3.0.1.7.21\",\"terms\":[\"Informatief\",\"Nieuws/actualiteiten\"]},{\"id\":\"3.0.1.8.25\",\"terms\":[\"Documentaire\",\"Natuur\"]}]}";

        Program program = Jackson2Mapper.getInstance().readerFor(Program.class).readValue(input);

        assertThat(program.getGenres()).hasSize(2);
        assertThat(program.getGenres().first().getTermId()).isEqualTo("3.0.1.7.21");
        assertThat(program.getGenres().last().getTermId()).isEqualTo("3.0.1.8.25");

    }

    @Test
    public void testTags() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"tags\":[\"tag1\",\"tag2\",\"tag3\"],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withTags().build();
        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }

    @Test
    public void testPortals() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"portals\":[{\"id\":\"3VOOR12_GRONINGEN\",\"value\":\"3voor12 Groningen\"},{\"id\":\"STERREN24\",\"value\":\"Sterren24\"}],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withPortals().build();
        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }

    @Test
    public void testDuration() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"duration\":7200000}";

        Program program = program().lean().withDuration().build();
        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }

    @Test
    public void testMemberOfAndDescendantOfGraph() throws Exception {
        String expected = """
            {
              "objectType" : "program",
              "embeddable" : true,
              "broadcasters" : [ ],
              "genres" : [ ],
              "countries" : [ ],
              "languages" : [ ],
              "descendantOf" : [ {
                "midRef" : "AVRO_5555555",
                "urnRef" : "urn:vpro:media:group:100",
                "type" : "SERIES"
              }, {
                "midRef" : "AVRO_7777777",
                "urnRef" : "urn:vpro:media:group:200",
                "type" : "SEASON"
              }, {
                "midRef" : "VPROWON_110",
                "urnRef" : "urn:vpro:media:segment:301",
                "type" : "SEGMENT"
              } ],
              "memberOf" : [ {
                "midRef" : "AVRO_7777777",
                "urnRef" : "urn:vpro:media:group:200",
                "type" : "SEASON",
                "index" : 1,
                "highlighted" : false,
                "memberOf" : [ {
                  "midRef" : "AVRO_5555555",
                  "type" : "SERIES",
                  "index" : 1
                } ],
                "episodeOf" : [ ],
                "added" : 0
              }, {
                "midRef" : "VPROWON_110",
                "urnRef" : "urn:vpro:media:segment:301",
                "type" : "SEGMENT",
                "index" : 2,
                "highlighted" : false,
                "memberOf" : [ ],
                "episodeOf" : [ ],
                "segmentOf" : {
                  "midRef" : "VPROWON_109",
                  "type" : "CLIP",
                  "memberOf" : [ {
                    "midRef" : "AVRO_5555555",
                    "type" : "SERIES",
                    "index" : 10
                  } ]
                }
              }, {
                "midRef" : "VPROWON_110",
                "urnRef" : "urn:vpro:media:segment:301",
                "type" : "SEGMENT",
                "index" : 3,
                "highlighted" : false,
                "memberOf" : [ ],
                "episodeOf" : [ ],
                "segmentOf" : {
                  "midRef" : "VPROWON_109",
                  "type" : "CLIP",
                  "memberOf" : [ {
                    "midRef" : "AVRO_5555555",
                    "type" : "SERIES",
                    "index" : 10
                  } ]
                }
              } ]
            }""";

        Program program = program().lean().withMemberOf(new AtomicLong(106)).build();
        /* Set MID to null first, then set it to the required MID; otherwise an IllegalArgumentException will be thrown setting the MID to another value */
        program.getMemberOf().first().getGroup().setMid(null);
        program.getMemberOf().first().getGroup().setMid("AVRO_7777777");
        /* Set MID to null first, then set it to the required MID; otherwise an IllegalArgumentException will be thrown setting the MID to another value */
        program.getMemberOf().first().getGroup().getMemberOf().first().getGroup().setMid(null);
        program.getMemberOf().first().getGroup().getMemberOf().first().getGroup().setMid("AVRO_5555555");
        program.getMemberOf().first().setAdded(Instant.EPOCH);
        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }

    @Test
    public void testEpisodeOfAndDescendantOfGraph() throws Exception {
        String expected = """
            {
              "objectType" : "program",
              "type" : "BROADCAST",
              "urn" : "urn:vpro:media:program:100",
              "embeddable" : true,
              "episodeOf" : [ {
                "midRef" : "AVRO_7777777",
                "urnRef" : "urn:vpro:media:group:102",
                "type" : "SEASON",
                "index" : 1,
                "highlighted" : false,
                "memberOf" : [ {
                  "midRef" : "AVRO_5555555",
                  "type" : "SERIES",
                  "index" : 1
                }, {
                  "midRef" : "VPROWON_106",
                  "type" : "SEGMENT",
                  "index" : 2,
                  "segmentOf" : {
                    "midRef" : "VPROWON_105",
                    "type" : "CLIP",
                    "memberOf" : [ {
                      "midRef" : "AVRO_5555555",
                      "type" : "SERIES",
                      "index" : 10
                    } ]
                  }
                } ],
                "episodeOf" : [ ],
                "added" : 0
              } ],
              "broadcasters" : [ ],
              "genres" : [ ],
              "countries" : [ ],
              "languages" : [ ],
              "descendantOf" : [ {
                "midRef" : "AVRO_5555555",
                "urnRef" : "urn:vpro:media:group:101",
                "type" : "SERIES"
              }, {
                "midRef" : "AVRO_7777777",
                "urnRef" : "urn:vpro:media:group:102",
                "type" : "SEASON"
              }, {
                "midRef" : "VPROWON_106",
                "type" : "SEGMENT"
              } ]
            }""";

        Program program = program().id(100L).lean().type(ProgramType.BROADCAST).withEpisodeOf(101L, 102L, new AtomicLong(103)).build();
        program.getEpisodeOf().first().setAdded(Instant.EPOCH);
        program.getEpisodeOf().first().getGroup().setMid(null);
        program.getEpisodeOf().first().getGroup().setMid("AVRO_7777777");
        program.getEpisodeOf().first().getGroup().getMemberOf().first().getGroup().setMid(null);
        program.getEpisodeOf().first().getGroup().getMemberOf().first().getGroup().setMid("AVRO_5555555");

        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }


    @Test
    public void testScheduleEvent() {
        String expected = """
            {
              "objectType" : "program",
              "sortDate" : 0,
              "urn" : "urn:vpro:media:program:100",
              "embeddable" : true,
              "broadcasters" : [ ],
              "genres" : [ ],
              "countries" : [ ],
              "languages" : [ ],
              "scheduleEvents" : [ {
                "channel" : "NED1",
                "start" : 0,
                "guideDay" : -90000000,
                "duration" : 100000,
                "midRef" : "VPRO_123456",
                "poProgID" : "VPRO_123456",
                "rerun" : false,
                "net" : "ZAPP",
                "urnRef" : "urn:vpro:media:program:100",
                "eventStart" : 0
              }, {
                "channel" : "NED2",
                "start" : 1,
                "guideDay" : -90000000,
                "duration" : 100000,
                "midRef" : "VPRO_123457",
                "poProgID" : "VPRO_123457",
                "repeat" : {
                  "value" : "herhaling",
                  "isRerun" : true
                },
                "rerun" : true,
                "urnRef" : "urn:vpro:media:program:100",
                "eventStart" : 1
              } ]
            }""";



        Program program = program().id(100L).lean()
            .scheduleEvents(
                ScheduleEvent.builder()
                    .channel(Channel.NED1)
                    .start(Instant.ofEpochMilli(0))
                    .duration(Duration.ofMillis(100000L))
                    .guideDay(LocalDate.of(1969, 12, 31))
                    .net(new Net("ZAPP"))
                    .midRef("VPRO_123456")
                    .build(),
                ScheduleEvent.builder()
                    .channel(Channel.NED2)
                    .start(Instant.ofEpochMilli(1))
                    .duration(Duration.ofMillis(100000L))
                    .midRef("VPRO_123457")
                    .rerun("herhaling")
                    .build()
            ).build();


        Program unmarshalled = Jackson2TestUtil.assertThatJson(Jackson2Mapper.getPrettyPublisherInstance(), program).isSimilarTo(expected).get();
        assertThat(unmarshalled.getScheduleEvents().first().getParent()).isNotNull();
    }

    @Test
    public void testCredits() throws Exception {
        String expected = """
            {
              "objectType" : "program",
              "urn" : "urn:vpro:media:program:100",
              "embeddable" : true,
              "broadcasters" : [ ],
              "genres" : [ ],
              "countries" : [ ],
              "languages" : [ ],
              "credits" : [ {
                "objectType" : "person",
                "givenName" : "Pietje",
                "familyName" : "Puk",
                "role" : "GUEST"
              } ]
            }""";

        Person person = new Person("Pietje", "Puk", RoleType.GUEST);
        Program program = program().id(100L).lean().persons(person).build();

        String actual = toApiJson(program);

        Jackson2TestUtil.assertJsonEquals(expected, actual);
    }

    @Test
    public void testLocations() throws Exception {
        String expected = """
                {
                    "objectType" : "program",
                    "urn" : "urn:vpro:media:program:100",
                    "embeddable" : true,
                    "broadcasters" : [ ],
                    "genres" : [ ],
                    "countries" : [ ],
                    "languages" : [ ],
                    "predictions" : [ {
                      "state" : "REALIZED",
                      "platform" : "INTERNETVOD"
                    } ],
                    "locations" : [ {
                      "programUrl" : "2",
                      "avAttributes" : {
                        "avFileFormat" : "UNKNOWN"
                      },
                      "owner" : "BROADCASTER",
                      "creationDate" : 1,
                      "workflow" : "PUBLISHED",
                      "platform" : "INTERNETVOD"
                    } ]
                  }

            """;

        Location location = new Location("2", OwnerType.BROADCASTER);
        location.setCreationInstant(Instant.ofEpochMilli(1));
        Program program = program().id(100L).lean().locations(location).build();

        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
        Jackson2TestUtil.roundTripAndSimilar(location,
            """
                {
                   "programUrl" : "2",
                   "avAttributes" : {
                     "avFileFormat" : "UNKNOWN"
                   },
                   "owner" : "BROADCASTER",
                   "creationDate" : 1,
                   "workflow" : "PUBLISHED",
                   "platform" : "INTERNETVOD"
                 }
                """);

    }

    @Test
    public void testImages() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"images\":[{\"imageUri\":\"http://images.poms.omroep.nl/plaatje\",\"owner\":\"BROADCASTER\",\"type\":\"PICTURE\",\"highlighted\":false,\"creationDate\":1,\"workflow\":\"PUBLISHED\"}]}";

        Image image = new Image(OwnerType.BROADCASTER, "http://images.poms.omroep.nl/plaatje");
        image.setCreationInstant(Instant.ofEpochMilli(1));
        image.setLastModifiedBy(Editor.builder().email("bla@foo.bar").build());
        Program program = program().id(100L).lean().images(image).build();

        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }


    @Test
    public void testTwitterRefs() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"twitter\":[{\"type\":\"HASHTAG\",\"value\":\"#vpro\"},{\"type\":\"ACCOUNT\",\"value\":\"@twitter\"}]}";

        Program program = program().id(100L).lean().withTwitterRefs().build();

        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }


    @Test
    public void testLanguages() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[{\"code\":\"nl\",\"value\":\"Nederlands\"}]}";

        Program program = program().id(100L).lean().languages("nl").build();

        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }


    @Test
    public void testCountries() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[{\"code\":\"NL\",\"value\":\"Nederland\"}],\"languages\":[]}";

        Program program = program().id(100L).lean().countries("NL").build();

        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }


    @Test
    public void testAgeRating() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"ageRating\":\"16\"}";

        Program program = program().id(100L).lean().ageRating(AgeRating._16).build();

        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }

    @Test
    public void testAgeRatingAll() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"ageRating\":\"ALL\"}";

        Program program = program().id(100L).lean().ageRating(AgeRating.ALL).build();

        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }


    @Test
    public void testAgeRatingUnknown() throws Exception {
        String odd = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"ageRating\":\"17\"}";

        Program program = Jackson2Mapper.getLenientInstance().readValue(odd, Program.class);
        assertThat(program.getAgeRating()).isNull();
    }


    @Test
    public void testAspectRatio() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"avAttributes\":{\"videoAttributes\":{\"aspectRatio\":\"16:9\"}}}";

        Program program = program().id(100L).lean().aspectRatio(AspectRatio._16x9).build();

        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
    }


    @Test
    public void testObjectType() throws IOException {
        String expected = "{\"objectType\":\"group\",\"urn\":\"urn:vpro:media:group:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"isOrdered\":true}";
        Group group = group().id(100L).lean().build();

        String actual = toPublisherJson(group);
        assertJsonEquals(expected, actual);


    }

    @Test
    public void testUnMarshalGroupWithoutObjectType()  {
        assertThatThrownBy(() -> {
            String expected = "{\"urn\":\"urn:vpro:media:group:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"hasSubtitles\":false,\"countries\":[],\"languages\":[],\"isOrdered\":true}";

            MediaObject mo = Jackson2Mapper.getInstance().readValue(expected, MediaObject.class);
            log.info("{}", mo);
        }).isInstanceOf(JsonMappingException.class);


    }

    @Test
    public void testWithLocations() {
        String expected = """
                        {
                           "objectType" : "program",
                           "urn" : "urn:vpro:media:program:100",
                           "embeddable" : true,
                           "broadcasters" : [ ],
                           "genres" : [ ],
                           "countries" : [ ],
                           "languages" : [ ],
                           "predictions" : [ {
                             "state" : "REALIZED",
                             "platform" : "INTERNETVOD"
                           } ],
                           "locations" : [ {
                             "programUrl" : "http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v",
                             "avAttributes" : {
                               "bitrate" : 1500,
                               "avFileFormat" : "MP4"
                             },
                             "owner" : "BROADCASTER",
                             "creationDate" : 1457102700000,
                             "workflow" : "PUBLISHED",
                             "offset" : 780000,
                             "duration" : 600000,
                             "platform" : "INTERNETVOD",
                             "publishStart" : 1487244180000
                           }, {
                             "programUrl" : "http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf",
                             "avAttributes" : {
                               "bitrate" : 3000,
                               "avFileFormat" : "WM"
                             },
                             "owner" : "BROADCASTER",
                             "creationDate" : 1457099100000,
                             "workflow" : "PUBLISHED",
                             "platform" : "INTERNETVOD"
                           }, {
                             "programUrl" : "http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf",
                             "avAttributes" : {
                               "bitrate" : 2000,
                               "avFileFormat" : "WM"
                             },
                             "owner" : "BROADCASTER",
                             "creationDate" : 1457095500000,
                             "workflow" : "PUBLISHED",
                             "duration" : 1833000,
                             "platform" : "INTERNETVOD"
                           }, {
                             "programUrl" : "http://player.omroep.nl/?aflID=4393288",
                             "avAttributes" : {
                               "bitrate" : 1000,
                               "avFileFormat" : "HTML"
                             },
                             "owner" : "NEBO",
                             "creationDate" : 1457091900000,
                             "workflow" : "PUBLISHED",
                             "platform" : "INTERNETVOD"
                           } ]
                         }""";

        Program program = program().id(100L)
            .lean()
            .withLocations()
            .build();
        program.getLocations().first().setPublishStartInstant(LocalDateTime.of(2017, 2, 16, 12, 23).atZone(Schedule.ZONE_ID).toInstant());

        Program out = Jackson2TestUtil.roundTripAndSimilar(program, expected);
        assertThat(out.getLocations().first().getDuration()).isEqualTo(Duration.of(10, ChronoUnit.MINUTES));
        assertThat(out.getLocations().first().getOffset()).isEqualTo(Duration.of(13, ChronoUnit.MINUTES));

    }

    @Test
    public void testWithLocationsWithUnknownOwner() throws Exception {
        String example = """
            {
              "objectType" : "program",
              "urn" : "urn:vpro:media:program:100",
              "embeddable" : true,
              "broadcasters" : [ ],
              "genres" : [ ],
              "hasSubtitles" : false,
              "countries" : [ ],
              "languages" : [ ],
              "locations" : [ {
                "programUrl" : "http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v",
                "avAttributes" : {
                  "avFileFormat" : "MP4"
                },
                "offset" : 780000,
                "duration" : 600000,
                "owner" : "UNKNOWN",
                "creationDate" : 1457102700000,
                "workflow" : "FOR_PUBLICATION",
                 "publishStart" : 1487244180000
              }, {
                "programUrl" : "http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf",
                "avAttributes" : {
                  "avFileFormat" : "WM"
                },
                "owner" : "BROADCASTER",
                "creationDate" : 1457099100000,
                "workflow" : "FOR_PUBLICATION"
              } ]
            }""";


        Program program = Jackson2Mapper.getLenientInstance().readerFor(Program.class).readValue(example);

        assertThat(program.getLocations().first().getOwner()).isNull();


    }

    @Test
    public void testWithIntentions() throws Exception {

        JsonNode jsonNode = Jackson2Mapper.getLenientInstance().readTree(getClass().getResourceAsStream("/intention-scenarios.json"));

        JsonNode  expected = jsonNode.get("mediaWithTwoIntention");
        Program program = program().lean().withIntentions().build();


        Jackson2TestUtil.roundTripAndSimilar(program, expected);

        Program marshalled = Jackson2Mapper.getInstance().readValue(toPublisherJson(program), Program.class);
        assertEquals(marshalled.intentions, program.intentions);
    }

    @Test
    public void testWithGeoLocations() throws Exception {

        JsonNode expected = Jackson2Mapper.getLenientInstance().readTree(getClass().getResourceAsStream("/geolocations-media-with-two-geolocations.json"));

        Program program = program().lean().withGeoLocations().build();

        Jackson2TestUtil.roundTripAndSimilar(Jackson2Mapper.getPublisherInstance(), program, expected);

    }

    @Test
    public void testMarshalWithFullGeoLocations() throws Exception {
        JsonNode expected = Jackson2Mapper.getLenientInstance().readTree(getClass().getResourceAsStream("/geolocations-media-with-one-full-geolocations.json"));

        GeoLocation value = GeoLocation.builder()
                .role(GeoRoleType.RECORDED_IN)
                .name("myName").scopeNote("myDescription").uri("myuri").gtaaStatus(GTAAStatus.approved)
                .build();
        SortedSet<GeoLocations> geoLocations =
            Stream.of(GeoLocations.builder().owner(OwnerType.BROADCASTER).value(value).build())
                .collect(Collectors.toCollection(TreeSet::new));
        JsonNode actual = Jackson2Mapper.getLenientInstance().valueToTree(geoLocations);
        Jackson2TestUtil.assertThatJson(actual).isSimilarTo(expected);
    }

    @Test
    public void testUnMarshalWithFullGeoLocations() throws Exception {
        String geoLocationsJson = """
          {
            "owner":"BROADCASTER",
            "values": [{
              "name":"myName",
              "scopeNotes": ["myDescription"],
              "gtaaUri": "myuri",
              "gtaaStatus": "approved",
              "role":"RECORDED_IN"
            }]
          }
          """;

        GeoLocations actualGeoLocations = Jackson2Mapper.getStrictInstance().readerFor(GeoLocations.class).readValue(new StringReader(geoLocationsJson));
        GeoLocation value = GeoLocation.builder()
                .role(GeoRoleType.RECORDED_IN)
                .name("myName").scopeNote("myDescription").uri("myuri").gtaaStatus(GTAAStatus.approved)
                .build();
        final GeoLocations expectedGeoLocations = GeoLocations.builder().owner(OwnerType.BROADCASTER).value(value).build();

        assertEquals(expectedGeoLocations, actualGeoLocations);

    }


    @Test
    public void testAvailableSubtitles() throws Exception {
    	ObjectNode media = JsonNodeFactory.instance.objectNode();
    	media.put("objectType", "program");
    	media.put("urn", "urn:vpro:media:program:100");
    	media.put("embeddable", true);
    	media.put("hasSubtitles", true);
    	media.set("broadcasters", JsonNodeFactory.instance.arrayNode());
    	media.set("genres", JsonNodeFactory.instance.arrayNode());
    	media.set("countries", JsonNodeFactory.instance.arrayNode());
    	media.set("languages", JsonNodeFactory.instance.arrayNode());
    	ArrayNode subs = JsonNodeFactory.instance.arrayNode();
    	ObjectNode subNLC = JsonNodeFactory.instance.objectNode();
    	subNLC.put("type", "CAPTION");
    	subNLC.put("language", "nl");
    	subs.add(subNLC);
    	ObjectNode subNLT = JsonNodeFactory.instance.objectNode();
    	subNLT.put("type", "TRANSLATION");
    	subNLT.put("language", "nl");
    	subs.add(subNLT);
    	media.set("availableSubtitles", subs);

    	Program program = program().id(100L).lean().build();
    	program.getAvailableSubtitles().add(new AvailableSubtitles(Locales.DUTCH,
            SubtitlesType.CAPTION));
    	program.getAvailableSubtitles().add(new AvailableSubtitles(Locales.DUTCH,
            SubtitlesType.TRANSLATION));

    	Program out = Jackson2TestUtil.roundTripAndSimilar(program, pretty(media));
        assertEquals(2, out.getAvailableSubtitles().size());

        List<AvailableSubtitles> availableSubtitles = new ArrayList<>(out.getAvailableSubtitles());
        assertEquals("nl", availableSubtitles.get(0).getLanguage().toString());
        assertEquals(SubtitlesType.CAPTION, availableSubtitles.get(0).getType());

        assertEquals("nl", availableSubtitles.get(1).getLanguage().toString());
        assertEquals(SubtitlesType.TRANSLATION, availableSubtitles.get(1).getType());

    }
	private String pretty(ObjectNode node) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
	}

    @Test
    public void testWithCredits() {
        Program program = program().lean().withCredits().build();

        Program rounded = Jackson2TestUtil.roundTripAndSimilar(program, """
            {
              "objectType" : "program",
              "embeddable" : true,
              "broadcasters" : [ ],
              "genres" : [ ],
              "countries" : [ ],
              "languages" : [ ],
              "credits" : [ {
                "objectType" : "person",
                "givenName" : "Bregtje",
                "familyName" : "van der Haak",
                "role" : "DIRECTOR",
                "gtaaUri" : "http://data.beeldengeluid.nl/gtaa/1234"
              }, {
                "objectType" : "person",
                "givenName" : "Hans",
                "familyName" : "Goedkoop",
                "role" : "PRESENTER"
              }, {
                "objectType" : "person",
                "givenName" : "Meta",
                "familyName" : "de Vries",
                "role" : "PRESENTER"
              }, {
                "objectType" : "person",
                "givenName" : "Claire",
                "familyName" : "Holt",
                "role" : "ACTOR"
              }, {
                "objectType" : "name",
                "role" : "COMPOSER",
                "name" : "Doe Maar",
                "scopeNotes" : [ "popgroep Nederland" ],
                "gtaaUri" : "http://data.beeldengeluid.nl/gtaa/51771"
              }, {
                "objectType" : "person",
                "givenName" : "Mark",
                "familyName" : "Rutte",
                "role" : "SUBJECT",
                "gtaaUri" : "http://data.beeldengeluid.nl/gtaa/149017"
              } ]
            }""");

        log.info("{}", rounded.getCredits());

    }

    @Test
    public void testUnmarshalOf() throws IOException {
        String example = "{\"tags\":[\"gepensioneerd\",\"Nell Koppen\",\"oudere werknemers\",\"pensioen\",\"vakbond\",\"werk\",\"werknemers\",\"Wim van den Brink\"],\"mid\":\"POMS_NOS_583461\",\"titles\":[{\"value\":\"De Laatste Dag\",\"owner\":\"BROADCASTER\",\"type\":\"MAIN\"}],\"avType\":\"AUDIO\",\"images\":[{\"description\":\"Pensioen\",\"imageUri\":\"urn:vpro:image:487099\",\"urn\":\"urn:vpro:media:image:43659204\",\"width\":640,\"publishStart\":1404943200000,\"type\":\"PICTURE\",\"highlighted\":false,\"title\":\"De laatste dag\",\"workflow\":\"PUBLISHED\",\"lastModified\":1404995300720,\"creationDate\":1404995300669,\"owner\":\"BROADCASTER\",\"height\":426}],\"urn\":\"urn:vpro:media:program:43659132\",\"genres\":[{\"id\":\"3.0.1.7\",\"terms\":[\"Informatief\"]},{\"id\":\"3.0.1.8\",\"terms\":[\"Documentaire\"]}],\"embeddable\":true,\"publishStart\":133916400000,\"type\":\"BROADCAST\",\"duration\":2400000,\"hasSubtitles\":false,\"countries\":[],\"objectType\":\"program\",\"locations\":[{\"programUrl\":\"http://download.omroep.nl/vpro/algemeen/woord/woord_radio/Delaatstedag1.mp3\",\"avAttributes\":{\"avFileFormat\":\"MP3\"},\"creationDate\":1404994995386,\"lastModified\":1404994995456,\"workflow\":\"PUBLISHED\",\"owner\":\"BROADCASTER\",\"urn\":\"urn:vpro:media:location:43659159\"}],\"workflow\":\"PUBLISHED\",\"lastModified\":1404995300722,\"sortDate\":133916400000,\"languages\":[],\"descriptions\":[{\"value\":\"Eerste van twee documentaires over pensionering en\\ngepensioneerden. In dit programma wordt gesproken over 'het\\nzwarte gat' waarin de 65-jarige werknemer valt na zijn\\nafscheid van het bedrijf. Als deskundigen komen aan het\\nwoord: gerontoloog prof. Schreuder, die een vrijwillige\\npensionering bepleit; voorlichter Maurice Akkermans van de\\nFederatie Bejaardenbeleid; een vakbondsman en een\\nwetenschappelijk medewerker. Afgewisseld met enkele\\nervaringen van zojuist gepensioneerden en hun vrouwen. Tevens\\neen gesprek met het acteursechtpaar Nell Koppen (62) en Wim\\nvan den Brink (65) en, onaangekondigd, een reactie door\\nprogrammamaker Bob Uschi (62). Met opnamen gemaakt tijdens\\nafscheidsrecepties.\",\"owner\":\"BROADCASTER\",\"type\":\"MAIN\"}],\"creationDate\":1404994811838,\"broadcasters\":[{\"id\":\"NOS\",\"value\":\"NOS\"}]}";
        Jackson2Mapper.getInstance().readValue(new StringReader(example), MediaObject.class);
    }

    @Test
    public void segmentWithEverything() throws Exception {
        Jackson2TestUtil.roundTripAndSimilar(Jackson2Mapper.getPrettyPublisherInstance(),
            MediaTestDataBuilder
                .segment()
                .withEverything()
                .build(),
            getClass().getResourceAsStream("/segment-with-everything.json"));
    }

    @Test
    public void programWithEverything() throws Exception {
        final Program program =  MediaTestDataBuilder
                .program()
                .withEverything()
                .build();

        StringWriter programJson = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/program-with-everything.json"), programJson, UTF_8);

        final Program rounded  = Jackson2TestUtil.roundTripAndSimilarAndEquals(program, programJson.toString());

        assertThat(rounded.getLocations().first().getId()).isEqualTo(6);
        assertThat(rounded.getMemberOf().first().getType()).isEqualTo(MediaType.SEASON);
    }

    @Test
    public void programWithEverythingPublisher() throws Exception {
        StringWriter programJson = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/program-with-everything-publisher.json"), programJson, UTF_8);
        Program program =  MediaTestDataBuilder
                .program()
                .withEverything()
                .build();
        Program rounded  = Jackson2TestUtil.roundTripAndSimilarAndEquals(Jackson2Mapper.getPublisherInstance(), program, programJson.toString());
        assertThat(rounded.getLocations().first().getId()).isEqualTo(6);
        assertThat(rounded.getMemberOf().first().getType()).isEqualTo(MediaType.SEASON);
    }

    @Test
    public void programWithUnpublishableLocation() {
        PublicationFilter.ENABLED.set(true);
        Program p = MediaTestDataBuilder
            .program()
            .predictions(Platform.INTERNETVOD)
            .correctPredictions()
            .locations(
                Location.builder()
                    .programUrl("https://vpro.nl/foo.mp3")
                    .publishStop(instant().minusSeconds(10))
                    .build(),
                Location.builder().programUrl("https://vpro.nl/bar.mp3").workflow(Workflow.DELETED).build(),
                Location.builder().programUrl("https://vpro.nl/bar2.mp3").workflow(Workflow.FOR_DELETION).build()
            )
            .build()
            ;
        Jackson2TestUtil.assertThatJson(Jackson2Mapper.getPrettyPublisherInstance(), p)
            .withoutRemarshalling()
            .isSimilarTo("""
                {
                   "objectType" : "program",
                   "workflow" : "FOR_PUBLICATION",
                   "sortDate" : 10,
                   "creationDate" : 10,
                   "embeddable" : true,
                   "broadcasters" : [ ],
                   "genres" : [ ],
                   "countries" : [ ],
                   "languages" : [ ],
                   "predictions" : [ {
                     "state" : "REVOKED",
                     "platform" : "INTERNETVOD",
                     "publishStop" : -9990
                   } ],
                   "locations" : [ ]
                 }""");
        PublicationFilter.ENABLED.remove();
    }


    @Test
    public void withMemberOf() {
        Program program = Program.builder()
            .creationDate(LocalDateTime.of(2019, 8, 20, 21, 0))

            .memberOf(MemberRef.builder().type(MediaType.SEASON).build()).build();

        Jackson2TestUtil.roundTripAndSimilar(program, """
            {
              "objectType" : "program",
              "workflow" : "FOR_PUBLICATION",
              "sortDate" : 1566327600000,
              "creationDate" : 1566327600000,
              "embeddable" : true,
              "broadcasters" : [ ],
              "genres" : [ ],
              "countries" : [ ],
              "languages" : [ ],
              "descendantOf" : [ {
                "type" : "SEASON"
              } ],
              "memberOf" : [ {
                "type" : "SEASON",
                "highlighted" : false,
                "memberOf" : [ ],
                "episodeOf" : [ ]
              } ]
            }""");

    }

    @Test
    public void testWithRelations() {

        Program program = program().lean().withRelations().build();

        Jackson2TestUtil.roundTripAndSimilar(program, """
            {
              "objectType" : "program",
              "embeddable" : true,
              "broadcasters" : [ ],
              "genres" : [ ],
              "countries" : [ ],
              "languages" : [ ],
              "relations" : [ {
                "value" : "synoniem",
                "type" : "THESAURUS",
                "broadcaster" : "AVRO",
                "urn" : "urn:vpro:media:relation:2"
              }, {
                "value" : "Ulfts Mannenkoor",
                "type" : "KOOR",
                "broadcaster" : "EO",
                "urn" : "urn:vpro:media:relation:4"
              }, {
                "value" : "Marco Borsato",
                "type" : "ARTIST",
                "broadcaster" : "VPRO",
                "urn" : "urn:vpro:media:relation:3"
              }, {
                "uriRef" : "http://www.bluenote.com/",
                "value" : "Blue Note",
                "type" : "LABEL",
                "broadcaster" : "VPRO",
                "urn" : "urn:vpro:media:relation:1"
              } ]
            }""");

    }

    private String toJson(Jackson2Mapper mapper, MediaObject program) throws IOException {
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, program);
        return writer.toString();
    }

    private String toPublisherJson(MediaObject program) throws IOException {
        return toJson(Jackson2Mapper.getPrettyPublisherInstance(), program);
    }

    private String toApiJson(MediaObject program) throws IOException {
        return toJson(Jackson2Mapper.getPrettyInstance(), program);
    }

    @SneakyThrows
    private void assertJsonEquals(String expected, String actual) {
        try {
            JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
        } catch (AssertionError e) {
            log.error(e.getMessage());
            assertThat(actual).isEqualTo(expected);
        }
    }
}
