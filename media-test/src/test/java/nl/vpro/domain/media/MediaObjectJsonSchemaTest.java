/*
 * Copyright (C) 2012 All rights reserved
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
@SuppressWarnings("ConstantConditions")
@Slf4j
public class MediaObjectJsonSchemaTest {

    @BeforeAll
    public static void before() {
        CLOCK.set(Clock.fixed(Instant.ofEpochMilli(10), Schedule.ZONE_ID));
        Locale.setDefault(Locales.DUTCH);
        ClassificationServiceLocator.setInstance(new MediaClassificationService());
        MediaObjects.autoCorrectPredictions = false;
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

        Program program = program().lean().mergedTo(program().mid("MERGE_TARGET").build()).build();
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
        String expected = "{\n" +
            "  \"objectType\" : \"program\",\n" +
            "  \"embeddable\" : true,\n" +
            "  \"broadcasters\" : [ ],\n" +
            "  \"titles\" : [ {\n" +
            "    \"value\" : \"Main title\",\n" +
            "    \"owner\" : \"BROADCASTER\",\n" +
            "    \"type\" : \"MAIN\"\n" +
            "  }, {\n" +
            "    \"value\" : \"Main title MIS\",\n" +
            "    \"owner\" : \"MIS\",\n" +
            "    \"type\" : \"MAIN\"\n" +
            "  }, {\n" +
            "    \"value\" : \"Short title\",\n" +
            "    \"owner\" : \"BROADCASTER\",\n" +
            "    \"type\" : \"SHORT\"\n" +
            "  }, {\n" +
            "    \"value\" : \"Episode title MIS\",\n" +
            "    \"owner\" : \"MIS\",\n" +
            "    \"type\" : \"SUB\"\n" +
            "  } ],\n" +
            "  \"genres\" : [ ],\n" +
            "  \"countries\" : [ ],\n" +
            "  \"languages\" : [ ],\n" +
            "  \"expandedTitles\" : [ {\n" +
            "    \"value\" : \"Main title\",\n" +
            "    \"owner\" : \"BROADCASTER\",\n" +
            "    \"type\" : \"MAIN\"\n" +
            "  }, {\n" +
            "    \"value\" : \"Main title\",\n" +
            "    \"owner\" : \"NPO\",\n" +
            "    \"type\" : \"MAIN\"\n" +
            "  }, {\n" +
            "    \"value\" : \"Short title\",\n" +
            "    \"owner\" : \"BROADCASTER\",\n" +
            "    \"type\" : \"SHORT\"\n" +
            "  }, {\n" +
            "    \"value\" : \"Short title\",\n" +
            "    \"owner\" : \"NPO\",\n" +
            "    \"type\" : \"SHORT\"\n" +
            "  }, {\n" +
            "    \"value\" : \"Episode title MIS\",\n" +
            "    \"owner\" : \"BROADCASTER\",\n" +
            "    \"type\" : \"SUB\"\n" +
            "  }, {\n" +
            "    \"value\" : \"Episode title MIS\",\n" +
            "    \"owner\" : \"NPO\",\n" +
            "    \"type\" : \"SUB\"\n" +
            "  }, {\n" +
            "    \"value\" : \"Main title\",\n" +
            "    \"owner\" : \"BROADCASTER\",\n" +
            "    \"type\" : \"LEXICO\"\n" +
            "  }, {\n" +
            "    \"value\" : \"Main title\",\n" +
            "    \"owner\" : \"NPO\",\n" +
            "    \"type\" : \"LEXICO\"\n" +
            "  } ]\n" +
            "}";

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
        String expected = "{\n" +
            "  \"objectType\" : \"program\",\n" +
            "  \"embeddable\" : true,\n" +
            "  \"broadcasters\" : [ ],\n" +
            "  \"genres\" : [ ],\n" +
            "  \"countries\" : [ ],\n" +
            "  \"languages\" : [ ],\n" +
            "  \"descendantOf\" : [ {\n" +
            "    \"midRef\" : \"AVRO_5555555\",\n" +
            "    \"urnRef\" : \"urn:vpro:media:group:100\",\n" +
            "    \"type\" : \"SERIES\"\n" +
            "  }, {\n" +
            "    \"midRef\" : \"AVRO_7777777\",\n" +
            "    \"urnRef\" : \"urn:vpro:media:group:200\",\n" +
            "    \"type\" : \"SEASON\"\n" +
            "  }, {\n" +
            "    \"midRef\" : \"VPROWON_110\",\n" +
            "    \"urnRef\" : \"urn:vpro:media:segment:301\",\n" +
            "    \"type\" : \"SEGMENT\"\n" +
            "  } ],\n" +
            "  \"memberOf\" : [ {\n" +
            "    \"midRef\" : \"AVRO_7777777\",\n" +
            "    \"urnRef\" : \"urn:vpro:media:group:200\",\n" +
            "    \"type\" : \"SEASON\",\n" +
            "    \"index\" : 1,\n" +
            "    \"highlighted\" : false,\n" +
            "    \"memberOf\" : [ {\n" +
            "      \"midRef\" : \"AVRO_5555555\",\n" +
            "      \"type\" : \"SERIES\",\n" +
            "      \"index\" : 1\n" +
            "    } ],\n" +
            "    \"episodeOf\" : [ ],\n" +
            "    \"added\" : 0\n" +
            "  }, {\n" +
            "    \"midRef\" : \"VPROWON_110\",\n" +
            "    \"urnRef\" : \"urn:vpro:media:segment:301\",\n" +
            "    \"type\" : \"SEGMENT\",\n" +
            "    \"index\" : 2,\n" +
            "    \"highlighted\" : false,\n" +
            "    \"memberOf\" : [ ],\n" +
            "    \"episodeOf\" : [ ],\n" +
            "    \"segmentOf\" : {\n" +
            "      \"midRef\" : \"VPROWON_109\",\n" +
            "      \"type\" : \"CLIP\",\n" +
            "      \"memberOf\" : [ {\n" +
            "        \"midRef\" : \"AVRO_5555555\",\n" +
            "        \"type\" : \"SERIES\",\n" +
            "        \"index\" : 10\n" +
            "      } ]\n" +
            "    }\n" +
            "  }, {\n" +
            "    \"midRef\" : \"VPROWON_110\",\n" +
            "    \"urnRef\" : \"urn:vpro:media:segment:301\",\n" +
            "    \"type\" : \"SEGMENT\",\n" +
            "    \"index\" : 3,\n" +
            "    \"highlighted\" : false,\n" +
            "    \"memberOf\" : [ ],\n" +
            "    \"episodeOf\" : [ ],\n" +
            "    \"segmentOf\" : {\n" +
            "      \"midRef\" : \"VPROWON_109\",\n" +
            "      \"type\" : \"CLIP\",\n" +
            "      \"memberOf\" : [ {\n" +
            "        \"midRef\" : \"AVRO_5555555\",\n" +
            "        \"type\" : \"SERIES\",\n" +
            "        \"index\" : 10\n" +
            "      } ]\n" +
            "    }\n" +
            "  } ]\n" +
            "}";

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
        String expected = "{\n" +
            "  \"objectType\" : \"program\",\n" +
            "  \"type\" : \"BROADCAST\",\n" +
            "  \"urn\" : \"urn:vpro:media:program:100\",\n" +
            "  \"embeddable\" : true,\n" +
            "  \"episodeOf\" : [ {\n" +
            "    \"midRef\" : \"AVRO_7777777\",\n" +
            "    \"urnRef\" : \"urn:vpro:media:group:102\",\n" +
            "    \"type\" : \"SEASON\",\n" +
            "    \"index\" : 1,\n" +
            "    \"highlighted\" : false,\n" +
            "    \"memberOf\" : [ {\n" +
            "      \"midRef\" : \"AVRO_5555555\",\n" +
            "      \"type\" : \"SERIES\",\n" +
            "      \"index\" : 1\n" +
            "    }, {\n" +
            "      \"midRef\" : \"VPROWON_106\",\n" +
            "      \"type\" : \"SEGMENT\",\n" +
            "      \"index\" : 2,\n" +
            "      \"segmentOf\" : {\n" +
            "        \"midRef\" : \"VPROWON_105\",\n" +
            "        \"type\" : \"CLIP\",\n" +
            "        \"memberOf\" : [ {\n" +
            "          \"midRef\" : \"AVRO_5555555\",\n" +
            "          \"type\" : \"SERIES\",\n" +
            "          \"index\" : 10\n" +
            "        } ]\n" +
            "      }\n" +
            "    } ],\n" +
            "    \"episodeOf\" : [ ],\n" +
            "    \"added\" : 0\n" +
            "  } ],\n" +
            "  \"broadcasters\" : [ ],\n" +
            "  \"genres\" : [ ],\n" +
            "  \"countries\" : [ ],\n" +
            "  \"languages\" : [ ],\n" +
            "  \"descendantOf\" : [ {\n" +
            "    \"midRef\" : \"AVRO_5555555\",\n" +
            "    \"urnRef\" : \"urn:vpro:media:group:101\",\n" +
            "    \"type\" : \"SERIES\"\n" +
            "  }, {\n" +
            "    \"midRef\" : \"AVRO_7777777\",\n" +
            "    \"urnRef\" : \"urn:vpro:media:group:102\",\n" +
            "    \"type\" : \"SEASON\"\n" +
            "  }, {\n" +
            "    \"midRef\" : \"VPROWON_106\",\n" +
            "    \"type\" : \"SEGMENT\"\n" +
            "  } ]\n" +
            "}";

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
        String expected = "{\n" +
            "  \"objectType\" : \"program\",\n" +
            "  \"sortDate\" : 0,\n" +
            "  \"urn\" : \"urn:vpro:media:program:100\",\n" +
            "  \"embeddable\" : true,\n" +
            "  \"broadcasters\" : [ ],\n" +
            "  \"genres\" : [ ],\n" +
            "  \"countries\" : [ ],\n" +
            "  \"languages\" : [ ],\n" +
            "  \"scheduleEvents\" : [ {\n" +
            "    \"channel\" : \"NED1\",\n" +
            "    \"start\" : 0,\n" +
            "    \"guideDay\" : -90000000,\n" +
            "    \"duration\" : 100000,\n" +
            "    \"midRef\" : \"VPRO_123456\",\n" +
            "    \"poProgID\" : \"VPRO_123456\",\n" +
            "    \"rerun\" : false,\n" +
            "    \"net\" : \"ZAPP\",\n" +
            "    \"urnRef\" : \"urn:vpro:media:program:100\",\n" +
            "    \"eventStart\" : 0\n" +
            "  }, {\n" +
            "    \"channel\" : \"NED2\",\n" +
            "    \"start\" : 1,\n" +
            "    \"guideDay\" : -90000000,\n" +
            "    \"duration\" : 100000,\n" +
            "    \"midRef\" : \"VPRO_123457\",\n" +
            "    \"poProgID\" : \"VPRO_123457\",\n" +
            "    \"repeat\" : {\n" +
            "      \"value\" : \"herhaling\",\n" +
            "      \"isRerun\" : true\n" +
            "    },\n" +
            "    \"rerun\" : true,\n" +
            "    \"urnRef\" : \"urn:vpro:media:program:100\",\n" +
            "    \"eventStart\" : 1\n" +
            "  } ]\n" +
            "}";



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
        String expected = "{\n" +
            "  \"objectType\" : \"program\",\n" +
            "  \"urn\" : \"urn:vpro:media:program:100\",\n" +
            "  \"embeddable\" : true,\n" +
            "  \"broadcasters\" : [ ],\n" +
            "  \"genres\" : [ ],\n" +
            "  \"countries\" : [ ],\n" +
            "  \"languages\" : [ ],\n" +
            "  \"credits\" : [ {\n" +
            "    \"objectType\" : \"person\",\n" +
            "    \"givenName\" : \"Pietje\",\n" +
            "    \"familyName\" : \"Puk\",\n" +
            "    \"role\" : \"GUEST\"\n" +
            "  } ]\n" +
            "}";

        Person person = new Person("Pietje", "Puk", RoleType.GUEST);
        Program program = program().id(100L).lean().persons(person).build();

        String actual = toApiJson(program);

        Jackson2TestUtil.assertJsonEquals(expected, actual);
    }

    @Test
    public void testLocations() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"locations\":[{\"programUrl\":\"2\",\"avAttributes\":{\"avFileFormat\":\"UNKNOWN\"},\"owner\":\"BROADCASTER\",\"creationDate\":1,\"workflow\":\"FOR_PUBLICATION\"}]}";

        Location location = new Location("2", OwnerType.BROADCASTER);
        location.setCreationInstant(Instant.ofEpochMilli(1));
        Program program = program().id(100L).lean().locations(location).build();

        String actual = toPublisherJson(program);

        assertJsonEquals(expected, actual);
        Jackson2TestUtil.roundTripAndSimilar(location, "{\"programUrl\":\"2\",\"avAttributes\":{\"avFileFormat\":\"UNKNOWN\"},\"owner\":\"BROADCASTER\",\"creationDate\":1,\"workflow\":\"FOR_PUBLICATION\"}");
    }

    @Test
    public void testImages() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"images\":[{\"imageUri\":\"http://images.poms.omroep.nl/plaatje\",\"owner\":\"BROADCASTER\",\"type\":\"PICTURE\",\"highlighted\":false,\"creationDate\":1,\"workflow\":\"FOR_PUBLICATION\"}]}";

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
        String expected = "{\n" +
            "  \"objectType\" : \"program\",\n" +
            "  \"urn\" : \"urn:vpro:media:program:100\",\n" +
            "  \"embeddable\" : true,\n" +
            "  \"broadcasters\" : [ ],\n" +
            "  \"genres\" : [ ],\n" +
            "  \"countries\" : [ ],\n" +
            "  \"languages\" : [ ],\n" +
            "  \"locations\" : [ {\n" +
            "    \"programUrl\" : \"http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v\",\n" +
            "    \"avAttributes\" : {\n" +
            "      \"bitrate\" : 1500,\n" +
            "      \"avFileFormat\" : \"MP4\"\n" +
            "    },\n" +
            "    \"owner\" : \"BROADCASTER\",\n" +
            "    \"creationDate\" : 1457102700000,\n" +
            "    \"workflow\" : \"FOR_PUBLICATION\",\n" +
            "    \"offset\" : 780000,\n" +
            "    \"duration\" : 600000,\n" +
            "    \"publishStart\" : 1487244180000\n" +
            "  }, {\n" +
            "    \"programUrl\" : \"http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf\",\n" +
            "    \"avAttributes\" : {\n" +
            "      \"bitrate\" : 3000,\n" +
            "      \"avFileFormat\" : \"WM\"\n" +
            "    },\n" +
            "    \"owner\" : \"BROADCASTER\",\n" +
            "    \"creationDate\" : 1457099100000,\n" +
            "    \"workflow\" : \"FOR_PUBLICATION\"\n" +
            "  }, {\n" +
            "    \"programUrl\" : \"http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf\",\n" +
            "    \"avAttributes\" : {\n" +
            "      \"bitrate\" : 2000,\n" +
            "      \"avFileFormat\" : \"WM\"\n" +
            "    },\n" +
            "    \"owner\" : \"BROADCASTER\",\n" +
            "    \"creationDate\" : 1457095500000,\n" +
            "    \"workflow\" : \"FOR_PUBLICATION\",\n" +
            "    \"duration\" : 1833000\n" +
            "  }, {\n" +
            "    \"programUrl\" : \"http://player.omroep.nl/?aflID=4393288\",\n" +
            "    \"avAttributes\" : {\n" +
            "      \"bitrate\" : 1000,\n" +
            "      \"avFileFormat\" : \"HTML\"\n" +
            "    },\n" +
            "    \"owner\" : \"NEBO\",\n" +
            "    \"creationDate\" : 1457091900000,\n" +
            "    \"workflow\" : \"FOR_PUBLICATION\"\n" +
            "  } ]\n" +
            "}";

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
        String example = "{\n" +
            "  \"objectType\" : \"program\",\n" +
            "  \"urn\" : \"urn:vpro:media:program:100\",\n" +
            "  \"embeddable\" : true,\n" +
            "  \"broadcasters\" : [ ],\n" +
            "  \"genres\" : [ ],\n" +
            "  \"hasSubtitles\" : false,\n" +
            "  \"countries\" : [ ],\n" +
            "  \"languages\" : [ ],\n" +
            "  \"locations\" : [ {\n" +
            "    \"programUrl\" : \"http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v\",\n" +
            "    \"avAttributes\" : {\n" +
            "      \"avFileFormat\" : \"MP4\"\n" +
            "    },\n" +
            "    \"offset\" : 780000,\n" +
            "    \"duration\" : 600000,\n" +
            "    \"owner\" : \"UNKNOWN\",\n" +
            "    \"creationDate\" : 1457102700000,\n" +
            "    \"workflow\" : \"FOR_PUBLICATION\",\n" +
            "     \"publishStart\" : 1487244180000\n" +
            "  }, {\n" +
            "    \"programUrl\" : \"http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf\",\n" +
            "    \"avAttributes\" : {\n" +
            "      \"avFileFormat\" : \"WM\"\n" +
            "    },\n" +
            "    \"owner\" : \"BROADCASTER\",\n" +
            "    \"creationDate\" : 1457099100000,\n" +
            "    \"workflow\" : \"FOR_PUBLICATION\"\n" +
            "  } ]\n" +
            "}";


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
        String geoLocationsJson = "      {\n" +
                "        \"owner\":\"BROADCASTER\",\n" +
                "        \"values\": [{\n" +
                "          \"name\":\"myName\",\n" +
                "          \"scopeNotes\": [\"myDescription\"],\n" +
                "          \"gtaaUri\": \"myuri\",\n" +
                "          \"gtaaStatus\": \"approved\",\n" +
                "          \"role\":\"RECORDED_IN\"\n" +
                "        }]\n" +
                "      }";

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
        assertEquals("nl", out.getAvailableSubtitles().get(0).getLanguage().toString());
        assertEquals(SubtitlesType.CAPTION, out.getAvailableSubtitles().get(0).getType());
        assertEquals("nl", out.getAvailableSubtitles().get(1).getLanguage().toString());
        assertEquals(SubtitlesType.TRANSLATION, out.getAvailableSubtitles().get(1).getType());

    }
	private String pretty(ObjectNode node) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
	}

    @Test
    public void testWithCredits() {
        Program program = program().lean().withCredits().build();

        Program rounded = Jackson2TestUtil.roundTripAndSimilar(program, "{\n" +
            "  \"objectType\" : \"program\",\n" +
            "  \"embeddable\" : true,\n" +
            "  \"broadcasters\" : [ ],\n" +
            "  \"genres\" : [ ],\n" +
            "  \"countries\" : [ ],\n" +
            "  \"languages\" : [ ],\n" +
            "  \"credits\" : [ {\n" +
            "    \"objectType\" : \"person\",\n" +
            "    \"givenName\" : \"Bregtje\",\n" +
            "    \"familyName\" : \"van der Haak\",\n" +
            "    \"role\" : \"DIRECTOR\",\n" +
            "    \"gtaaUri\" : \"http://gtaa/1234\"\n" +
            "  }, {\n" +
            "    \"objectType\" : \"person\",\n" +
            "    \"givenName\" : \"Hans\",\n" +
            "    \"familyName\" : \"Goedkoop\",\n" +
            "    \"role\" : \"PRESENTER\"\n" +
            "  }, {\n" +
            "    \"objectType\" : \"person\",\n" +
            "    \"givenName\" : \"Meta\",\n" +
            "    \"familyName\" : \"de Vries\",\n" +
            "    \"role\" : \"PRESENTER\"\n" +
            "  }, {\n" +
            "    \"objectType\" : \"person\",\n" +
            "    \"givenName\" : \"Claire\",\n" +
            "    \"familyName\" : \"Holt\",\n" +
            "    \"role\" : \"ACTOR\"\n" +
            "  }, {\n" +
            "    \"objectType\" : \"name\",\n" +
            "    \"role\" : \"COMPOSER\",\n" +
            "    \"name\" : \"Doe Maar\",\n" +
            "    \"scopeNotes\" : [ \"popgroep Nederland\" ],\n" +
            "    \"gtaaUri\" : \"http://data.beeldengeluid.nl/gtaa/51771\"\n" +
            "  }, {\n" +
            "    \"objectType\" : \"person\",\n" +
            "    \"givenName\" : \"Mark\",\n" +
            "    \"familyName\" : \"Rutte\",\n" +
            "    \"role\" : \"SUBJECT\",\n" +
            "    \"gtaaUri\" : \"http://data.beeldengeluid.nl/gtaa/149017\"\n" +
            "  } ]\n" +
            "}");

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
        StringWriter programJson = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/program-with-everything.json"), programJson, UTF_8);
        Program program =  MediaTestDataBuilder
                .program()
                .withEverything()
                .build();
        Program rounded  = Jackson2TestUtil.roundTripAndSimilarAndEquals(program, programJson.toString());
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
            .locations(
                Location.builder().programUrl("https://vpro.nl/foo.mp3").publishStop(instant().minusSeconds(10)).build(),
                Location.builder().programUrl("https://vpro.nl/bar.mp3").workflow(Workflow.DELETED).build(),
                Location.builder().programUrl("https://vpro.nl/bar2.mp3").workflow(Workflow.FOR_DELETION).build()
            )
            .build()
            ;
        Jackson2TestUtil.assertThatJson(Jackson2Mapper.getPrettyPublisherInstance(), p)
            .withoutRemarshalling()
            .isSimilarTo("{\n" +
                "  \"objectType\" : \"program\",\n" +
                "  \"workflow\" : \"FOR_PUBLICATION\",\n" +
                "  \"sortDate\" : 10,\n" +
                "  \"creationDate\" : 10,\n" +
                "  \"embeddable\" : true,\n" +
                "  \"broadcasters\" : [ ],\n" +
                "  \"genres\" : [ ],\n" +
                "  \"countries\" : [ ],\n" +
                "  \"languages\" : [ ],\n" +
                "  \"locations\" : [ ]\n" +
                "}");
        PublicationFilter.ENABLED.remove();

    }


    @Test
    public void withMemberOf() {
        Program program = Program.builder()
            .creationDate(LocalDateTime.of(2019, 8, 20, 21, 0))

            .memberOf(MemberRef.builder().type(MediaType.SEASON).build()).build();

        Jackson2TestUtil.roundTripAndSimilar(program, "{\n" +
            "  \"objectType\" : \"program\",\n" +
            "  \"workflow\" : \"FOR_PUBLICATION\",\n" +
            "  \"sortDate\" : 1566327600000,\n" +
            "  \"creationDate\" : 1566327600000,\n" +
            "  \"embeddable\" : true,\n" +
            "  \"broadcasters\" : [ ],\n" +
            "  \"genres\" : [ ],\n" +
            "  \"countries\" : [ ],\n" +
            "  \"languages\" : [ ],\n" +
            "  \"descendantOf\" : [ {\n" +
            "    \"type\" : \"SEASON\"\n" +
            "  } ],\n" +
            "  \"memberOf\" : [ {\n" +
            "    \"type\" : \"SEASON\",\n" +
            "    \"highlighted\" : false,\n" +
            "    \"memberOf\" : [ ],\n" +
            "    \"episodeOf\" : [ ]\n" +
            "  } ]\n" +
            "}");

    }

    @Test
    public void testWithRelations() {

        Program program = program().lean().withRelations().build();

        Jackson2TestUtil.roundTripAndSimilar(program, "{\n" +
                "  \"objectType\" : \"program\",\n" +
                "  \"embeddable\" : true,\n" +
                "  \"broadcasters\" : [ ],\n" +
                "  \"genres\" : [ ],\n" +
                "  \"countries\" : [ ],\n" +
                "  \"languages\" : [ ],\n" +
                "  \"relations\" : [ {\n" +
                "    \"value\" : \"synoniem\",\n" +
                "    \"type\" : \"THESAURUS\",\n" +
                "    \"broadcaster\" : \"AVRO\",\n" +
                "    \"urn\" : \"urn:vpro:media:relation:2\"\n" +
                "  }, {\n" +
                "    \"value\" : \"Ulfts Mannenkoor\",\n" +
                "    \"type\" : \"KOOR\",\n" +
                "    \"broadcaster\" : \"EO\",\n" +
                "    \"urn\" : \"urn:vpro:media:relation:4\"\n" +
                "  }, {\n" +
                "    \"value\" : \"Marco Borsato\",\n" +
                "    \"type\" : \"ARTIST\",\n" +
                "    \"broadcaster\" : \"VPRO\",\n" +
                "    \"urn\" : \"urn:vpro:media:relation:3\"\n" +
                "  }, {\n" +
                "    \"uriRef\" : \"http://www.bluenote.com/\",\n" +
                "    \"value\" : \"Blue Note\",\n" +
                "    \"type\" : \"LABEL\",\n" +
                "    \"broadcaster\" : \"VPRO\",\n" +
                "    \"urn\" : \"urn:vpro:media:relation:1\"\n" +
                "  } ]\n" +
                "}");

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
