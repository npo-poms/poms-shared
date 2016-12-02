/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import net.sf.json.test.JSONAssert;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonMappingException;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.bind.BackwardsCompatibility;
import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.i18n.Locales;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import static nl.vpro.domain.media.MediaTestDataBuilder.group;
import static nl.vpro.domain.media.MediaTestDataBuilder.program;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 1.7
 */
public class MediaObjectJsonSchemaTest {

    @After
    public void after() {
        BackwardsCompatibility.clearCompatibility();
    }
    @Before
    public void before() {
        Locale.setDefault(Locales.DUTCH);
        ClassificationServiceLocator.setInstance(new MediaClassificationService());
    }


    @Test
    public void testMid() throws Exception {
        String expected = "{\"objectType\":\"program\",\"mid\":\"MID_000001\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().mid("MID_000001").build();
        String actual = toJson(program);
        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testHasSubtitles() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"hasSubtitles\":true,\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withSubtitles().build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testDatesCreatedAndModified() throws Exception {
        String expected = "{\"objectType\":\"program\",\"sortDate\":1,\"creationDate\":1,\"lastModified\":7200000,\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().creationDate(new Date(1)).lastModified(new Date(2 * 60 * 60 * 1000)).build();
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
    public void testPulishStartStop() throws Exception {
        String expected = "{\"objectType\":\"program\",\"sortDate\":1,\"publishStart\":1,\"publishStop\":7200000,\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().publishStart(new Date(1)).publishStop(new Date(2 * 60 * 60 * 1000)).build();
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

        Program program = Jackson2Mapper.getInstance().reader(Program.class).readValue(input);

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
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"regions\":[\"NL\",\"BENELUX\"],\"genres\":[],\"countries\":[],\"languages\":[]}";

        Program program = program().lean().withGeoRestrictions().build();
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testPredictions() throws Exception {
        String expected = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"predictions\":[{\"state\":\"REALIZED\",\"publishStart\":10,\"publishStop\":100,\"platform\":\"TVVOD\"}]}";

        Program program = program().lean().build();

        Prediction prediction = new Prediction(Platform.TVVOD);
        prediction.setState(Prediction.State.REALIZED);
        prediction.setPublishStart(new Date(10));
        prediction.setPublishStop(new Date(100));

        program.getPredictions().add(prediction);

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);

    }

    @Test
    public void testBackwardsCompatibleUnmarshalPredictions() throws IOException {
        String backwards = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"predictions\":[\"INTERNETVOD\"]}";
        Program program = Jackson2Mapper.INSTANCE.readValue(new StringReader(backwards), Program.class);
        assertThat(program.getPredictions().iterator().next().getPlatform()).isEqualTo(Platform.INTERNETVOD);


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

        Program program = Jackson2Mapper.getInstance().reader(Program.class).readValue(input);

        assertThat(program.getGenres()).hasSize(2);
        assertThat(program.getGenres().first().getTermId()).isEqualTo("3.0.1.7.21");
        assertThat(program.getGenres().last().getTermId()).isEqualTo("3.0.1.8.25");

    }

    @Test
    public void testReverseV1Genres() throws Exception {
        BackwardsCompatibility.setV1Compatibility(true);

        String input = "{\"objectType\":\"program\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"genres\":[\"Film\",\"Jeugd\",\"Serie/soap\"]}";

        Program program = Jackson2Mapper.getInstance().reader(Program.class).readValue(input);

        assertThat(program.getGenres()).hasSize(2);
    }

    @Test
    public void tesTtags() throws Exception {
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
        program.getMemberOf().first().getOwner().setMid("AVRO_7777777");
        program.getMemberOf().first().getOwner().getMemberOf().first().getOwner().setMid("AVRO_5555555");
        program.getMemberOf().first().setAdded(new Date(0));
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testEpisodeOfAndDescendantOfGraph() throws Exception {
        String expected = "{\"objectType\":\"program\",\"type\":\"BROADCAST\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"episodeOf\":[{\"midRef\":\"AVRO_7777777\",\"urnRef\":\"urn:vpro:media:group:102\",\"type\":\"SEASON\",\"index\":1,\"highlighted\":false,\"added\":0}],\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"descendantOf\":[{\"midRef\":\"AVRO_5555555\",\"urnRef\":\"urn:vpro:media:group:101\",\"type\":\"SERIES\"},{\"midRef\":\"AVRO_7777777\",\"urnRef\":\"urn:vpro:media:group:102\",\"type\":\"SEASON\"}]}";

        Program program = program().id(100L).lean().type(ProgramType.BROADCAST).withEpisodeOf(101L, 102L).build();
        program.getEpisodeOf().first().setAdded(new Date(0));
        program.getEpisodeOf().first().getOwner().setMid("AVRO_7777777");
        program.getEpisodeOf().first().getOwner().getMemberOf().first().getOwner().setMid("AVRO_5555555");
        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
    }

    @Test
    public void testScheduleEvent() throws Exception {
        String expected = "{\"objectType\":\"program\",\"sortDate\":0,\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"scheduleEvents\":[{\"guideDay\":-90000000,\"start\":0,\"duration\":100000,\"poProgID\":\"VPRO_123456\",\"channel\":\"NED1\",\"net\":\"ZAPP\",\"urnRef\":\"urn:vpro:media:program:100\",\"midRef\":\"VPRO_123456\"}]}";

        ScheduleEvent event = new ScheduleEvent(Channel.NED1, LocalDate.of(1970, 1, 1), Instant.ofEpochMilli(0), java.time.Duration.ofMillis(100000L));
        event.setGuideDate(LocalDate.of(1969, 12, 31));
        event.setNet(new Net("ZAPP", "Zapp"));
        event.setPoProgID("VPRO_123456");

        Program program = program().id(100L).lean().scheduleEvents(event).build();

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
        Program unmarshalled = Jackson2Mapper.getInstance().readValue(actual, Program.class);
        assertThat(unmarshalled.getScheduleEvents().first().getMediaObject()).isNotNull();
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
        location.setCreationDate(new Date(1));
        Program program = program().id(100L).lean().locations(location).build();

        String actual = toJson(program);

        JSONAssert.assertEquals(expected, actual);
        Jackson2TestUtil.roundTripAndSimilar(location, "{\"programUrl\":\"2\",\"avAttributes\":{\"avFileFormat\":\"UNKNOWN\"},\"owner\":\"BROADCASTER\",\"creationDate\":1,\"workflow\":\"FOR_PUBLICATION\"}");
    }

    @Test
    public void testImages() throws Exception {
        String expected = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"images\":[{\"imageUri\":\"http://images.poms.omroep.nl/plaatje\",\"owner\":\"BROADCASTER\",\"type\":\"PICTURE\",\"highlighted\":false,\"creationDate\":1,\"workflow\":\"FOR_PUBLICATION\"}]}";

        Image image = new Image(OwnerType.BROADCASTER, "http://images.poms.omroep.nl/plaatje");
        image.setCreationDate(new Date(1));
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

        String input = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"languages\":[\"NL\"]}";

        Program program = Jackson2Mapper.getInstance().reader(Program.class).readValue(input);

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

        String input = "{\"objectType\":\"program\",\"urn\":\"urn:vpro:media:program:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"countries\":[\"NL\"]}";

        Program program = Jackson2Mapper.getInstance().reader(Program.class).readValue(input);

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
        String expected = "{\"urn\":\"urn:vpro:media:group:100\",\"embeddable\":true,\"broadcasters\":[],\"genres\":[],\"countries\":[],\"languages\":[],\"isOrdered\":true}";

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
            "    \"workflow\" : \"FOR_PUBLICATION\"\n" +
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

        Program out = Jackson2TestUtil.roundTripAndSimilar(program, expected);
        assertThat(out.getLocations().first().getDuration()).isEqualTo(Duration.of(10, ChronoUnit.MINUTES));
        assertThat(out.getLocations().first().getOffset()).isEqualTo(Duration.of(13, ChronoUnit.MINUTES));

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
            "    \"role\" : \"DIRECTOR\"\n" +
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

    private String toJson(MediaObject program) throws IOException {
        StringWriter writer = new StringWriter();
        Jackson2Mapper.INSTANCE.writeValue(writer, program);
        return writer.toString();
    }
}
