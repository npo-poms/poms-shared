/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.test.JSONAssert;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.gtaa.Status;
import nl.vpro.domain.media.bind.BackwardsCompatibility;
import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.Editor;
import nl.vpro.i18n.Locales;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import static nl.vpro.domain.media.MediaTestDataBuilder.group;
import static nl.vpro.domain.media.MediaTestDataBuilder.program;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * @author Roelof Jan Koekoek
 * @since 1.7
 */
@Slf4j
public class MediaObjectJsonSchemaTest {

    @After
    public void after() {
        BackwardsCompatibility.clearCompatibility();
    }
    @BeforeClass
    public static void before() {
        Locale.setDefault(Locales.DUTCH);
        ClassificationServiceLocator.setInstance(new MediaClassificationService());
    }


    @Test
    public void testMid() throws Exception {
        String expected = "{\"objectType\":\"program\",\"mid\":\"MID_000001\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[], \"countries\":[],\"languages\":[]}";

        Program program = program().lean().mid("MID_000001").build();
        Jackson2TestUtil.roundTripAndSimilarAndEquals(program, expected);
    }

    @Test
    public void testHasSubtitles() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"hasSubtitles\":true,\"countries\":[],\"languages\":[],\"availableSubtitles\":[{\"language\":\"nl\",\"type\":\"CAPTION\"}]}";

        Program program = program().lean().withSubtitles().build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testDatesCreatedAndModified() throws Exception {
        String expected = "{\"objectType\":\"program\",\"sortDate\":1,\"creationDate\":1,\"lastModified\":7200000,\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().creationInstant(Instant.ofEpochMilli(1))
            .lastModified(Instant.ofEpochSecond(2 * 60 * 60))
            .build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testCreatedAndModifiedBy() throws Exception {
        Program program = program().lean().withCreatedBy().withLastModifiedBy().build();

        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[]}";
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testPublishStartStop() throws Exception {
        String expected = "{\"objectType\":\"program\",\"sortDate\":1,\"publishStart\":1,\"publishStop\":7200000,\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().publishStart(Instant.ofEpochMilli(1))
            .publishStop(Instant.ofEpochSecond(2 * 60 * 60)).build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testMergedTo() throws Exception {
        String expected = "{\"objectType\":\"program\",\"workflow\":\"MERGED\",\"mergedTo\":\"MERGE_TARGET\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().mergedTo(program().mid("MERGE_TARGET").build()).build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testCrids() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"crids\":[\"crid://bds.tv/9876\",\"crid://tmp.fragment.mmbase.vpro.nl/1234\"],\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withCrids().build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testBroadcasters() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[{\"id\":\"BNN\",\"value\":\"BNN\"},{\"id\":\"AVRO\",\"value\":\"AVRO\"}],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withBroadcasters().build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testV1Broadcasters() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[\"BNN\",\"AVRO\",\"Human\"],\"genres\":[],\"countries\":[],\"languages\":[]}";

        BackwardsCompatibility.setV1Compatibility(true);

        Program program = program().lean().withBroadcasters().broadcasters(new Broadcaster("HUMA", "Human")).build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);

    }

    @Test
    public void testReverseV1Broadcasters() throws Exception {
        BackwardsCompatibility.setV1Compatibility(true);

        String input = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[\"BNN\",\"AVRO\"]}";

        Program program = Jackson2Mapper.getInstance().readerFor(Program.class).readValue(input);

        assertThat(program.getBroadcasters()).hasSize(2);
    }

    @Test
    public void testExclusives() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"exclusives\":[\"STERREN24\",\"3VOOR12_GRONINGEN\"],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withPortalRestrictions().build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testRegions() throws Exception {
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

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);

    }

    @Test
    public void testBackwardsCompatibleUnmarshalPredictions() throws IOException {
        String backwards = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"hasSubtitles\":false,\"countries\":[],\"languages\":[],\"predictions\":[\"INTERNETVOD\"]}";
        Program program = Jackson2Mapper.INSTANCE.readValue(new StringReader(backwards), Program.class);
        assertThat(program.getPredictions()
            .iterator()
            .next()
            .getPlatform())
            .isEqualTo(Platform.INTERNETVOD);


    }

    @Test
    public void testTitles() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"titles\":[{\"value\":\"Main title\",\"owner\":\"BROADCASTER\",\"type\":\"MAIN\"},{\"value\":\"Main title MIS\",\"owner\":\"MIS\",\"type\":\"MAIN\"},{\"value\":\"Short title\",\"owner\":\"BROADCASTER\",\"type\":\"SHORT\"},{\"value\":\"Episode title MIS\",\"owner\":\"MIS\",\"type\":\"SUB\"}],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withTitles().build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testDescriptions() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"descriptions\":[{\"value\":\"Main description\",\"owner\":\"BROADCASTER\",\"type\":\"MAIN\"},{\"value\":\"Main description MIS\",\"owner\":\"MIS\",\"type\":\"MAIN\"},{\"value\":\"Short description\",\"owner\":\"BROADCASTER\",\"type\":\"SHORT\"},{\"value\":\"Episode description MIS\",\"owner\":\"MIS\",\"type\":\"EPISODE\"}],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withDescriptions().build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testGenres() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[{\"id\":\"3.0.1.7.21\",\"terms\":[\"Informatief\",\"Nieuws/actualiteiten\"]},{\"id\":\"3.0.1.8.25\",\"terms\":[\"Documentaire\",\"Natuur\"]}],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withGenres().build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testGenresV1() throws Exception {

        BackwardsCompatibility.setV1Compatibility(true);

        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[\"Documentaire\",\"Informatief\",\"Natuur\",\"Nieuws/actualiteiten\"],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withGenres().build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
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
    public void testReverseV1Genres() throws Exception {
        BackwardsCompatibility.setV1Compatibility(true);

        String input = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"hasSubtitles\":false,\"countries\":[],\"languages\":[],\"genres\":[\"Film\",\"Jeugd\",\"Serie/soap\"]}";

        Program program = Jackson2Mapper.getInstance().readerFor(Program.class).readValue(input);

        assertThat(program.getGenres()).hasSize(2);
    }

    @Test
    public void testTags() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"tags\":[\"tag1\",\"tag2\",\"tag3\"],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withTags().build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testPortals() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"portals\":[{\"id\":\"3VOOR12_GRONINGEN\",\"value\":\"3voor12 Groningen\"},{\"id\":\"STERREN24\",\"value\":\"Sterren24\"}],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withPortals().build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testDuration() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"duration\":7200000}";

        Program program = program().lean().withDuration().build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testMemberOfAndDescendantOfGraph() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"descendantOf\":[{\"midRef\":\"AVRO_5555555\",\"urnRef\":\"urn:vpro:media:group:100\",\"type\":\"SERIES\"},{\"midRef\":\"AVRO_7777777\",\"urnRef\":\"urn:vpro:media:group:200\",\"type\":\"SEASON\"}],\"memberOf\":[{\"midRef\":\"AVRO_7777777\",\"urnRef\":\"urn:vpro:media:group:200\",\"type\":\"SEASON\",\"index\":1,\"highlighted\":false,\"added\":0}]}";

        Program program = program().lean().withMemberOf().build();
        /* Set MID to null first, then set it to the required MID; otherwise an IllegalArgumentException will be thrown setting the MID to another value */
        program.getMemberOf().first().getGroup().setMid(null);
        program.getMemberOf().first().getGroup().setMid("AVRO_7777777");
        /* Set MID to null first, then set it to the required MID; otherwise an IllegalArgumentException will be thrown setting the MID to another value */
        program.getMemberOf().first().getGroup().getMemberOf().first().getGroup().setMid(null);
        program.getMemberOf().first().getGroup().getMemberOf().first().getGroup().setMid("AVRO_5555555");
        program.getMemberOf().first().setAdded(Instant.EPOCH);
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testEpisodeOfAndDescendantOfGraph() throws Exception {
        String expected = "{\"objectType\":\"program\",\"type\":\"BROADCAST\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"episodeOf\":[{\"midRef\":\"AVRO_7777777\",\"urnRef\":\"urn:vpro:media:group:102\",\"type\":\"SEASON\",\"index\":1,\"highlighted\":false,\"added\":0}],\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"descendantOf\":[{\"midRef\":\"AVRO_5555555\",\"urnRef\":\"urn:vpro:media:group:101\",\"type\":\"SERIES\"},{\"midRef\":\"AVRO_7777777\",\"urnRef\":\"urn:vpro:media:group:102\",\"type\":\"SEASON\"}]}";

        Program program = program().id(100L).lean().type(ProgramType.BROADCAST).withEpisodeOf(101L, 102L).build();
        program.getEpisodeOf().first().setAdded(Instant.EPOCH);
        program.getEpisodeOf().first().getGroup().setMid(null);
        program.getEpisodeOf().first().getGroup().setMid("AVRO_7777777");
        program.getEpisodeOf().first().getGroup().getMemberOf().first().getGroup().setMid(null);
        program.getEpisodeOf().first().getGroup().getMemberOf().first().getGroup().setMid("AVRO_5555555");
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
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
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"credits\":[{\"givenName\":\"Pietje\",\"familyName\":\"Puk\",\"role\":\"GUEST\"}]}";

        Person person = new Person("Pietje", "Puk", RoleType.GUEST);
        Program program = program().id(100L).lean().persons(person).build();

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testLocations() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"locations\":[{\"programUrl\":\"2\",\"avAttributes\":{\"avFileFormat\":\"UNKNOWN\"},\"owner\":\"BROADCASTER\",\"creationDate\":1,\"workflow\":\"FOR_PUBLICATION\"}]}";

        Location location = new Location("2", OwnerType.BROADCASTER);
        location.setCreationInstant(Instant.ofEpochMilli(1));
        Program program = program().id(100L).lean().locations(location).build();

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
        Jackson2TestUtil.roundTripAndSimilar(location, "{\"programUrl\":\"2\",\"avAttributes\":{\"avFileFormat\":\"UNKNOWN\"},\"owner\":\"BROADCASTER\",\"creationDate\":1,\"workflow\":\"FOR_PUBLICATION\"}");
    }

    @Test
    public void testImages() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"images\":[{\"imageUri\":\"http://images.poms.omroep.nl/plaatje\",\"owner\":\"BROADCASTER\",\"type\":\"PICTURE\",\"highlighted\":false,\"creationDate\":1,\"workflow\":\"FOR_PUBLICATION\"}]}";

        Image image = new Image(OwnerType.BROADCASTER, "http://images.poms.omroep.nl/plaatje");
        image.setCreationInstant(Instant.ofEpochMilli(1));
        image.setLastModifiedBy(Editor.builder().email("bla@foo.bar").build());
        Program program = program().id(100L).lean().images(image).build();

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }


    @Test
    public void testTwitterRefs() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"twitter\":[{\"type\":\"HASHTAG\",\"value\":\"#vpro\"},{\"type\":\"ACCOUNT\",\"value\":\"@twitter\"}]}";

        Program program = program().id(100L).lean().withTwitterRefs().build();

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }


    @Test
    public void testLanguages() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[{\"code\":\"nl\",\"value\":\"Nederlands\"}]}";

        Program program = program().id(100L).lean().languages("nl").build();

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testLanguagesV1() throws Exception {
        BackwardsCompatibility.setV1Compatibility(true);

        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[\"NL\"]}";

        Program program = program().id(100L).lean().languages("nl").build();

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }


    @Test
    public void testReverseV1Languages() throws Exception {
        BackwardsCompatibility.setV1Compatibility(true);

        String input = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"hasSubtitles\":false,\"countries\":[],\"languages\":[],\"languages\":[\"NL\"]}";

        Program program = Jackson2Mapper.getInstance().readerFor(Program.class).readValue(input);

        assertThat(program.getLanguages()).hasSize(1);
    }

    @Test
    public void testCountries() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[{\"code\":\"NL\",\"value\":\"Nederland\"}],\"languages\":[]}";

        Program program = program().id(100L).lean().countries("NL").build();

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testCountriesV1() throws Exception {
        BackwardsCompatibility.setV1Compatibility(true);

        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[\"NL\"],\"languages\":[]}";

        Program program = program().id(100L).lean().countries("NL").build();

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testReverseV1Countries() throws Exception {
        BackwardsCompatibility.setV1Compatibility(true);

        String input = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"hasSubtitles\":false,\"countries\":[],\"languages\":[],\"hasSubtitles\":false,\"countries\":[\"NL\"]}";

        Program program = Jackson2Mapper.getInstance().readerFor(Program.class).readValue(input);

        assertThat(program.getCountries()).hasSize(1);
    }


    @Test
    public void testAgeRating() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"ageRating\":\"16\"}";

        Program program = program().id(100L).lean().ageRating(AgeRating._16).build();

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testAgeRatingAll() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"ageRating\":\"ALL\"}";

        Program program = program().id(100L).lean().ageRating(AgeRating.ALL).build();

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }


    @Test
    public void testAgeRatingV1() throws Exception {
        BackwardsCompatibility.setV1Compatibility(true);

        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"ageRating\":16}";

        Program program = program().id(100L).lean().ageRating(AgeRating._16).build();

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testAgeRatingAllV1() throws Exception {

        BackwardsCompatibility.setV1Compatibility(true);

        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"ageRating\":null}";

        Program program = program().id(100L).lean().ageRating(AgeRating.ALL).build();

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }


    @Test
    public void testAspectRatio() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"avAttributes\":{\"videoAttributes\":{\"aspectRatio\":\"16:9\"}}}";

        Program program = program().id(100L).lean().aspectRatio(AspectRatio._16x9).build();

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }


    @Test
    public void testObjectType() throws IOException {
        String expected = "{\"objectType\":\"group\",\"urn\":\"urn:vpro:media:group:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"isOrdered\":true}";
        Group group = group().id(100L).lean().build();

        String actual = toJson(group);
        JSONAssert.assertEquals(expected, actual);


    }

    @Test(expected = JsonMappingException.class)
    public void testUnMarshalGroupWithoutObjectType() throws IOException {
        String expected = "{\"urn\":\"urn:vpro:media:group:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"hasSubtitles\":false,\"countries\":[],\"languages\":[],\"isOrdered\":true}";

        MediaObject mo = Jackson2Mapper.getInstance().readValue(expected, MediaObject.class);
        System.out.println(mo);


    }

    @Test
    public void testWithLocations() throws Exception {
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
            "      \"avFileFormat\" : \"MP4\"\n" +
            "    },\n" +
            "    \"offset\" : 780000,\n" +
            "    \"duration\" : 600000,\n" +
            "    \"owner\" : \"BROADCASTER\",\n" +
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
            "  }, {\n" +
            "    \"programUrl\" : \"http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf\",\n" +
            "    \"avAttributes\" : {\n" +
            "      \"avFileFormat\" : \"WM\"\n" +
            "    },\n" +
            "    \"duration\" : 1833000,\n" +
            "    \"owner\" : \"BROADCASTER\",\n" +
            "    \"creationDate\" : 1457095500000,\n" +
            "    \"workflow\" : \"FOR_PUBLICATION\"\n" +
            "  }, {\n" +
            "    \"programUrl\" : \"http://player.omroep.nl/?aflID=4393288\",\n" +
            "    \"avAttributes\" : {\n" +
            "      \"avFileFormat\" : \"HTML\"\n" +
            "    },\n" +
            "    \"owner\" : \"NEBO\",\n" +
            "    \"creationDate\" : 1457091900000,\n" +
            "    \"workflow\" : \"FOR_PUBLICATION\"\n" +
            "  } ]\n" +
            "}";

        Program program = program().id(100L).lean().withLocations().build();
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


        Program program = Jackson2Mapper.LENIENT.readerFor(Program.class).readValue(example);

        assertThat(program.getLocations().first().getOwner()).isNull();


    }

    @Test
    public void testWithIntentions() throws Exception {
        StringWriter segment = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/intention-scenarios.json"), segment, "UTF-8");
        Map expected = JsonPath.read(segment.toString(),"$.mediaWithTwoIntention");
        log.info(expected.toString());

        Program program = program().lean().withIntentions().build();
        Map actual = JsonPath.read(toJson(program),"$");

        JSONAssert.assertEquals(expected, actual);

        Jackson2TestUtil.roundTripAndSimilar(program, "{\n" +
            "  \"objectType\" : \"program\",\n" +
            "  \"embeddable\" : true,\n" +
            "  \"broadcasters\" : [ ],\n" +
            "  \"genres\" : [ ],\n" +
            "  \"intentions\" : [ {\n" +
            "    \"owner\" : \"BROADCASTER\",\n" +
            "    \"values\" : [ \"ACTIVATING\", \"INFORM_INDEPTH\" ]\n" +
            "  }, {\n" +
            "    \"owner\" : \"NPO\",\n" +
            "    \"values\" : [ \"ENTERTAINMENT_INFORMATIVE\", \"INFORM\" ]\n" +
            "  } ],\n" +
            "  \"countries\" : [ ],\n" +
            "  \"languages\" : [ ]\n" +
            "}");

        //Marshal
        Program marshalled = Jackson2Mapper.INSTANCE.readValue(toJson(program), Program.class);
        assertEquals(marshalled.intentions, program.intentions);
    }

    @Test
    public void testWithGeoLocations() throws Exception {
        StringWriter segment = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/geolocations-scenarios.json"), segment, "UTF-8");
        Map expected = JsonPath.read(segment.toString(),"$.mediaWithTwoGeoLocations");
        log.info(expected.toString());

        Program program = program().lean().withGeoLocations().build();
        Map actual = JsonPath.read(toJson(program),"$");

        JSONAssert.assertEquals(expected, actual);

        Jackson2TestUtil.roundTripAndSimilar(program, "{\n" +
                "  \"objectType\" : \"program\",\n" +
                "  \"embeddable\" : true,\n" +
                "  \"broadcasters\" : [ ],\n" +
                "  \"genres\" : [ ],\n" +
                "  \"countries\" : [ ],\n" +
                "  \"languages\" : [ ],\n" +
                "  \"geoLocations\" : [ {\n" +
                "    \"owner\" : \"BROADCASTER\",\n" +
                "    \"values\" : [ {\n" +
                "      \"role\" : \"SUBJECT\",\n" +
                "      \"name\" : \"Africa\",\n" +
                "      \"description\" : \"Continent\",\n" +
                "      \"gtaaUri\" : \"http://gtaa/1231\"\n" +
                "    } ]\n" +
                "  }, {\n" +
                "    \"owner\" : \"NPO\",\n" +
                "    \"values\" : [ {\n" +
                "      \"role\" : \"SUBJECT\",\n" +
                "      \"name\" : \"England\",\n" +
                "      \"gtaaStatus\" : \"approved\",\n" +
                "      \"gtaaUri\" : \"http://gtaa/1232\"\n" +
                "    }, {\n" +
                "      \"role\" : \"RECORDED_IN\",\n" +
                "      \"name\" : \"UK\",\n" +
                "      \"gtaaUri\" : \"http://gtaa/1233\"\n" +
                "    } ]\n" +
                "  } ]\n" +
                "}");
    }

    @Test
    public void testMarshalWithFullGeoLocations() throws Exception {
        StringWriter segment = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/geolocations-scenarios.json"), segment, "UTF-8");
        List expected = JsonPath.read(segment.toString(),"$.OneFullGeoLocations");

        GeoLocation value = GeoLocation.builder()
                .role(GeoRoleType.RECORDED_IN)
                .name("myName").description("myDescription").gtaaUri("myuri").gtaaStatus(Status.approved)
                .build();
        SortedSet geoLocations = Stream.of(GeoLocations.builder().owner(OwnerType.BROADCASTER).value(value).build()).collect(Collectors.toCollection(TreeSet::new));

        List actual = JsonPath.read(toJson2(geoLocations),"$");

        JSONAssert.assertEquals(expected, actual);

    }

    @Test
    public void testUnMarshalWithFullGeoLocations() throws Exception {
        String geoLocationsJson = "      {\n" +
                "        \"owner\":\"BROADCASTER\",\n" +
                "        \"values\": [{\n" +
                "          \"name\":\"myName\",\n" +
                "          \"description\": \"myDescription\",\n" +
                "          \"role\":\"RECORDED_IN\",\n" +
                "          \"gtaaUri\": \"myuri\",\n" +
                "          \"gtaaStatus\": \"approved\"\n" +
                "        }]\n" +
                "      }";

        GeoLocations actualGeoLocations = Jackson2Mapper.STRICT.readerFor(GeoLocations.class).readValue(new StringReader(geoLocationsJson));
        GeoLocation value = GeoLocation.builder()
                .role(GeoRoleType.RECORDED_IN)
                .name("myName").description("myDescription").gtaaUri("myuri").gtaaStatus(Status.approved)
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
    	String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
		return pretty;
	}

    @Test
    public void testWithPersons() throws Exception {
        Program program = program().lean().withPersons().build();

        Jackson2TestUtil.roundTripAndSimilar(program, "{\n" +
            "  \"objectType\" : \"program\",\n" +
            "  \"embeddable\" : true,\n" +
            "  \"broadcasters\" : [ ],\n" +
            "  \"genres\" : [ ],\n" +
            "  \"countries\" : [ ],\n" +
            "  \"languages\" : [ ],\n" +
            "  \"credits\" : [ {\n" +
            "    \"givenName\" : \"Bregtje\",\n" +
            "    \"familyName\" : \"van der Haak\",\n" +
            "    \"role\" : \"DIRECTOR\",\n" +
            "    \"gtaaUri\" : \"http://gtaa/1234\"\n" +
            "  }, {\n" +
            "    \"givenName\" : \"Hans\",\n" +
            "    \"familyName\" : \"Goedkoop\",\n" +
            "    \"role\" : \"PRESENTER\"\n" +
            "  }, {\n" +
            "    \"givenName\" : \"Meta\",\n" +
            "    \"familyName\" : \"de Vries\",\n" +
            "    \"role\" : \"PRESENTER\"\n" +
            "  }, {\n" +
            "    \"givenName\" : \"Claire\",\n" +
            "    \"familyName\" : \"Holt\",\n" +
            "    \"role\" : \"ACTOR\"\n" +
            "  } ]\n" +
            "}");

    }

    @Test
    public void testUnmarshalOf() throws IOException {
        String example = "{\"tags\":[\"gepensioneerd\",\"Nell Koppen\",\"oudere werknemers\",\"pensioen\",\"vakbond\",\"werk\",\"werknemers\",\"Wim van den Brink\"],\"mid\":\"POMS_NOS_583461\",\"titles\":[{\"value\":\"De Laatste Dag\",\"owner\":\"BROADCASTER\",\"type\":\"MAIN\"}],\"avType\":\"AUDIO\",\"images\":[{\"description\":\"Pensioen\",\"imageUri\":\"urn:vpro:image:487099\",\"urn\":\"urn:vpro:media:image:43659204\",\"width\":640,\"publishStart\":1404943200000,\"type\":\"PICTURE\",\"highlighted\":false,\"title\":\"De laatste dag\",\"workflow\":\"PUBLISHED\",\"lastModified\":1404995300720,\"creationDate\":1404995300669,\"owner\":\"BROADCASTER\",\"height\":426}],\"urn\":\"urn:vpro:media:program:43659132\",\"genres\":[{\"id\":\"3.0.1.7\",\"terms\":[\"Informatief\"]},{\"id\":\"3.0.1.8\",\"terms\":[\"Documentaire\"]}],\"embeddable\":true,\"publishStart\":133916400000,\"type\":\"BROADCAST\",\"duration\":2400000,\"hasSubtitles\":false,\"countries\":[],\"objectType\":\"program\",\"locations\":[{\"programUrl\":\"http://download.omroep.nl/vpro/algemeen/woord/woord_radio/Delaatstedag1.mp3\",\"avAttributes\":{\"avFileFormat\":\"MP3\"},\"creationDate\":1404994995386,\"lastModified\":1404994995456,\"workflow\":\"PUBLISHED\",\"owner\":\"BROADCASTER\",\"urn\":\"urn:vpro:media:location:43659159\"}],\"workflow\":\"PUBLISHED\",\"lastModified\":1404995300722,\"sortDate\":133916400000,\"languages\":[],\"descriptions\":[{\"value\":\"Eerste van twee documentaires over pensionering en\\ngepensioneerden. In dit programma wordt gesproken over 'het\\nzwarte gat' waarin de 65-jarige werknemer valt na zijn\\nafscheid van het bedrijf. Als deskundigen komen aan het\\nwoord: gerontoloog prof. Schreuder, die een vrijwillige\\npensionering bepleit; voorlichter Maurice Akkermans van de\\nFederatie Bejaardenbeleid; een vakbondsman en een\\nwetenschappelijk medewerker. Afgewisseld met enkele\\nervaringen van zojuist gepensioneerden en hun vrouwen. Tevens\\neen gesprek met het acteursechtpaar Nell Koppen (62) en Wim\\nvan den Brink (65) en, onaangekondigd, een reactie door\\nprogrammamaker Bob Uschi (62). Met opnamen gemaakt tijdens\\nafscheidsrecepties.\",\"owner\":\"BROADCASTER\",\"type\":\"MAIN\"}],\"creationDate\":1404994811838,\"broadcasters\":[{\"id\":\"NOS\",\"value\":\"NOS\"}]}";
        MediaObject mo = Jackson2Mapper.getInstance().readValue(new StringReader(example), MediaObject.class);


    }

    @Test
    public void segmentWithEverything() throws Exception {
        StringWriter segment = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/segment-with-everything.json"), segment, "UTF-8");
        Jackson2TestUtil.roundTripAndSimilar(MediaTestDataBuilder
                .segment()
                .withEverything()
                .build(),
            segment.toString());
    }

    @Test
    public void programWithEverything() throws Exception {
        StringWriter programJson = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/program-with-everything.json"), programJson, "UTF-8");
        Program program =  MediaTestDataBuilder
                .program()
                .withEverything()
                .build();
        Program rounded  = Jackson2TestUtil.roundTripAndSimilar(program, programJson.toString());
        assertThat(rounded.getLocations().first().getId()).isEqualTo(6);
    }

    @Test
    public void programWithEverythingMarshUnmarsh() throws Exception {
        StringWriter programJson = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/program-with-everything.json"), programJson, "UTF-8");
        Program program =  MediaTestDataBuilder
                .program()
                .withEverything()
                .build();
        Jackson2TestUtil.roundTripAndSimilar(program, programJson.toString());
    }

    @Test
    public void publisherView() throws IOException {

        String publisherString = Jackson2Mapper.getPublisherInstance()
            .writeValueAsString(MediaTestDataBuilder.program().withEverything().build());
        Map<String, Object> map = Jackson2Mapper.getInstance().readValue(publisherString, new TypeReference<Map<String, Object>>() {
        });
        assertThat(map.get("expandedTitles")).isNotNull();
        assertThat(((List) map.get("expandedTitles")).get(0)).isNotNull();
        assertThat(((List) map.get("expandedGeoLocations")).get(0)).isNotNull();
        assertThat(((Map<String, Object>)(((List) map.get("expandedTitles")).get(0))).get("value")).isEqualTo("Main title");

        log.info("{}", publisherString);

        Program p = Jackson2Mapper.getLenientInstance().readValue(publisherString, Program.class);
        assertThat(p.getMainTitle()).isEqualTo("Main title");
    }

    @Test
    public void publisherViewGeoLocations() throws IOException {

        final Program program = program().withGeoLocations().build();
        final GeoLocations broadcasterGeo = program.getGeoLocations().first();
        program.getGeoLocations().remove(broadcasterGeo);
        final GeoLocations newGeoLocations = GeoLocations.builder().owner(OwnerType.MIS).values(broadcasterGeo.getValues()).build();
        program.getGeoLocations().add(newGeoLocations);

        String publisherString = Jackson2Mapper.getPublisherInstance()
                .writeValueAsString(program);
        Map<String, Object> map = Jackson2Mapper.getInstance().readValue(publisherString, new TypeReference<Map<String, Object>>() {
        });
        final List expandedGeoLocations = (List) map.get("expandedGeoLocations");
        assertThat(expandedGeoLocations.size()).isEqualTo(3);

        final Map<String,Object> broadcasterGeoLoc =  (Map<String,Object>)expandedGeoLocations.get(0);
        final Map<String,Object>  npoGeoLoc = (Map<String,Object> ) expandedGeoLocations.get(1);
        final Map<String,Object>  misGeoLoc = (Map<String,Object> ) expandedGeoLocations.get(2);
        assertThat(broadcasterGeoLoc.get("owner")).isEqualTo("BROADCASTER");
        assertThat(((List)broadcasterGeoLoc.get("values")).size()).isEqualTo(2);

        assertThat(npoGeoLoc.get("owner")).isEqualTo("NPO");
        assertThat(((List)npoGeoLoc.get("values")).size()).isEqualTo(2);

        assertThat(misGeoLoc.get("owner")).isEqualTo("MIS");
        assertThat(((List)misGeoLoc.get("values")).size()).isEqualTo(1);



        log.info("{}", publisherString);

    }

    @Test
    public void normalView() throws IOException {
        String normalString = Jackson2Mapper.getInstance().writeValueAsString(MediaTestDataBuilder.program().withTitles().build());

        Map<String, Object> map = Jackson2Mapper.getInstance().readValue(normalString, new TypeReference<Map<String, Object>>() {
        });
        assertThat(map.get("expandedTitles")).isNull();

        log.info("{}", normalString);

        Program p = Jackson2Mapper.getLenientInstance().readValue(normalString, Program.class);
        assertThat(p.getMainTitle()).isEqualTo("Main title");
    }

    private String toJson(MediaObject program) throws IOException {
        StringWriter writer = new StringWriter();
        Jackson2Mapper.getPublisherInstance().writeValue(writer, program);
        return writer.toString();
    }

    private <O>  String toJson2(O javaObject) throws IOException {
        StringWriter writer = new StringWriter();
        Jackson2Mapper.INSTANCE.writeValue(writer, javaObject);
        return writer.toString();
    }
}
