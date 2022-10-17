package nl.vpro.domain.api;

import java.io.IOException;
import java.time.*;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonGenerator;

import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class MediaChangeTest {

    @Test
    public void tailJson() throws IOException {
        MediaChange tail = MediaChange.tail(100);
        assertThat(Jackson2Mapper.getInstance().writeValueAsString(tail)).isEqualTo("{\"revision\":100,\"tail\":true}");

        JsonGenerator jg = Jackson2Mapper.getInstance().getFactory().createGenerator(System.out);
        jg.writeObject(tail);
    }

    @Test
    public void json() {
        MediaChange change = MediaChange.builder()
            .publishDate(LocalDate.of(2016, 7, 20).atTime(13, 38).atZone(Schedule.ZONE_ID).toInstant())
            .mid("MID_123")
            .media(MediaTestDataBuilder.program().lean().build())
            .reasons(Arrays.asList("foo bar"))
            .build();

        Jackson2TestUtil.assertThatJson(change).isSimilarTo("{\n" +
                "  \"sequence\" : 1469014680000,\n" +
                "  \"publishDate\" : 1469014680000,\n" +
                "  \"id\" : \"MID_123\",\n" +
                "  \"mid\" : \"MID_123\",\n" +
                "  \"reasons\" : [ \"foo bar\" ],\n" +
                "  \"media\" : {\n" +
                "    \"objectType\" : \"program\",\n" +
                "    \"embeddable\" : true,\n" +
                "    \"broadcasters\" : [ ],\n" +
                "    \"genres\" : [ ],\n" +
                "    \"countries\" : [ ],\n" +
                "    \"languages\" : [ ]\n" +
                "  }\n" +
                "}");
    }

    @Test
    public void xml() {
        MediaChange change = MediaChange.builder()
            .publishDate(LocalDate.of(2016, 7, 20).atTime(13, 38).atZone(Schedule.ZONE_ID).toInstant())
            .mid("MID_123")
            .media(MediaTestDataBuilder.program().lean().build())
            .reasons(Arrays.asList("foo", "bar"))
            .build()
        ;

        JAXBTestUtil.assertThatXml(change).isSimilarTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<api:change publishDate=\"2016-07-20T13:38:00+02:00\" id=\"MID_123\" sequence=\"1469014680000\" mid=\"MID_123\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <api:reasons>\n" +
            "        <api:reason>foo</api:reason>\n" +
            "        <api:reason>bar</api:reason>\n" +
            "    </api:reasons>\n" +
            "    <api:media xsi:type=\"media:programType\" embeddable=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "        <media:credits/>\n" +
            "        <media:locations/>\n" +
            "        <media:images/>\n" +
            "        <media:scheduleEvents/>\n" +
            "        <media:segments/>\n" +
            "    </api:media>\n" +
            "</api:change>");

    }


    @Test
    public void jsonDelete() {
        MediaChange change = MediaChange.builder()
            .publishDate(LocalDate.of(2016, 7, 20).atTime(13, 38).atZone(Schedule.ZONE_ID).toInstant())
            .mid("MID_123")
            .media(MediaTestDataBuilder.program().lean().workflow(Workflow.DELETED).build())
            .build();

        Jackson2TestUtil.assertThatJson(change).isSimilarTo("{\n" +
            "  \"sequence\" : 1469014680000,\n" +
            "  \"publishDate\" : 1469014680000,\n" +
            "  \"id\" : \"MID_123\",\n" +
            "  \"mid\" : \"MID_123\",\n" +
            "  \"deleted\" : true,\n" +
            "  \"media\" : {\n" +
            "    \"objectType\" : \"program\",\n" +
            "    \"embeddable\" : true,\n" +
            "    \"broadcasters\" : [ ],\n" +
            "    \"genres\" : [ ],\n" +
            "    \"countries\" : [ ],\n" +
            "    \"languages\" : [ ]\n" +
            "  }\n" +
            "}");
    }

    @Test
    public void jsonDeleteTree() throws IOException {
        Instant publishDate = Instant.ofEpochMilli(1533041167873L);
        String json="{\"publishDate\":1533041167873,\"mid\":\"POW_00107979\",\"deleted\":true}";
        MediaChange change = Jackson2Mapper.getPrettyInstance().readValue(json, MediaChange.class);
        assertThat(change.getPublishDate()).isEqualTo(publishDate);
        assertThat(change.isDeleted()).isTrue();
        assertThat(change.getMid()).isEqualTo("POW_00107979");
    }


    @Test
    public void jsonRealPublishDate() {
        MediaChange change = MediaChange.builder()
            .publishDate(LocalDate.of(2016, 7, 20).atTime(13, 38).atZone(Schedule.ZONE_ID).toInstant())
            .mid("MID_123")
            .deleted(true)
            .media(MediaTestDataBuilder.program().lean().build())
            .build();

        change.setPublishDate(change.getPublishDate().plus(Duration.ofMillis(10)));

        Jackson2TestUtil.assertThatJson(change).isSimilarTo("{\n" +
            "  \"sequence\" : 1469014680000,\n" +
            "  \"publishDate\" : 1469014680010,\n" +
            "  \"realPublishDate\" : 1469014680000,\n" +
            "  \"id\" : \"MID_123\",\n" +
            "  \"mid\" : \"MID_123\",\n" +
            "  \"deleted\" : true,\n" +
            "  \"media\" : {\n" +
            "    \"objectType\" : \"program\",\n" +
            "    \"embeddable\" : true,\n" +
            "    \"broadcasters\" : [ ],\n" +
            "    \"genres\" : [ ],\n" +
            "    \"countries\" : [ ],\n" +
            "    \"languages\" : [ ]\n" +
            "  }\n" +
            "}");
    }

}
