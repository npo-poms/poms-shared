/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.subtitles;

import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;

import javax.xml.bind.JAXB;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static java.nio.charset.StandardCharsets.UTF_8;
import static nl.vpro.i18n.Locales.NETHERLANDISH;
import static org.assertj.core.api.Assertions.assertThat;

public class SubtitlesTest {



    @Test
    public void testMarshalToXml()  {
        Subtitles subtitles = Subtitles.webvtt("VPRO_1234", Duration.ofMillis(2 * 60 * 1000), NETHERLANDISH,  "WEBVTT\n\n1\n00:00:00.000 --> 00:01:04.000\nbla\n\n");
        subtitles.setCreationInstant(Instant.ofEpochMilli(0));
        subtitles.setLastModifiedInstant(Instant.ofEpochMilli(0));


        JAXBTestUtil.roundTripAndSimilar(subtitles,
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<subtitles:subtitles mid=\"VPRO_1234\" offset=\"P0DT0H2M0.000S\" creationDate=\"1970-01-01T01:00:00+01:00\" lastModified=\"1970-01-01T01:00:00+01:00\" type=\"CAPTION\" xml:lang=\"nl-NL\" owner=\"BROADCASTER\" workflow=\"FOR_PUBLICATION\" cueCount=\"1\" xmlns:subtitles=\"urn:vpro:media:subtitles:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
                    "    <subtitles:content format=\"WEBVTT\" charset=\"UTF-8\">V0VCVlRUCgoxCjAwOjAwOjAwLjAwMCAtLT4gMDA6MDE6MDQuMDAwCmJsYQoK</subtitles:content>\n" +
                    "</subtitles:subtitles>");
    }

    @Test
    public void testUnmarshallFromXml()  {
        String xml =
            "<subtitles mid=\"VPRO_1234\" offset=\"P0DT0H2M0.000S\" creationDate=\"1970-01-01T01:00:00+01:00\" lastModified=\"1970-01-01T01:00:00+01:00\" type=\"CAPTION\" xml:lang=\"nl-NL\"  owner=\"BROADCASTER\" workflow=\"FOR_PUBLICATION\" xmlns=\"urn:vpro:media:subtitles:2009\">\n" +
                "    <content format=\"WEBVTT\">" + Base64.getEncoder().encodeToString("Ondertiteling tekst".getBytes()) + "</content>\n" +
                "</subtitles>";

        StringReader reader = new StringReader(xml);

        Subtitles subtitles = JAXB.unmarshal(reader, Subtitles.class);

        assertThat(subtitles.getMid()).isEqualTo("VPRO_1234");
        assertThat(subtitles.getOffset()).isEqualTo(Duration.ofMillis(120000));
        assertThat(new String(subtitles.getContent().getValue(), UTF_8)).isEqualTo("Ondertiteling tekst");
    }

    @Test
    public void json() {
        Subtitles subtitles = Subtitles.webvtt("VPRO_1234",
            Duration.ofMillis(2 * 60 * 1000), NETHERLANDISH,
            "WEBVTT\n" +
                "\n" +
                "1\n" +
                "00:00:02.200 --> 00:00:04.150\n" +
                "888\n" +
                "\n");
        subtitles.setCreationInstant(Instant.ofEpochMilli(0));
        subtitles.setLastModifiedInstant(Instant.ofEpochMilli(0));
        assertThat(subtitles.getCueCount()).isEqualTo(1);

        Jackson2TestUtil.roundTripAndSimilar(subtitles, "{\n" +
            "  \"mid\" : \"VPRO_1234\",\n" +
            "  \"offset\" : 120000,\n" +
            "  \"content\" : {\n" +
            "    \"format\" : \"WEBVTT\",\n" +
            "    \"value\" : \"V0VCVlRUCgoxCjAwOjAwOjAyLjIwMCAtLT4gMDA6MDA6MDQuMTUwCjg4OAoK\",\n" +
            "    \"charset\" : \"UTF-8\"\n" +
            "  },\n" +
            "  \"creationDate\" : \"1970-01-01T01:00:00+01:00\",\n" +
            "  \"lastModified\" : \"1970-01-01T01:00:00+01:00\",\n" +
            "  \"type\" : \"CAPTION\",\n" +
            "  \"owner\" : \"BROADCASTER\",\n" +
            "  \"workflow\" : \"FOR_PUBLICATION\",\n" +
            "  \"lang\" : \"nl-NL\",\n" +
            "  \"cueCount\" : 1\n" +
            "}");
    }

    @Test
    public void from() {
        Subtitles subtitles = Subtitles.from(Arrays.asList(
                StandaloneCue.tt888(Cue.forMid(
                "mid").sequence(1).start(Duration.ZERO).end(Duration.ofSeconds(64)).content("bla").build())).iterator());
        Jackson2TestUtil.roundTripAndSimilar(subtitles, "{" +
            "  \"mid\" : \"mid\",\n" +
            "  \"content\" : {\n" +
            "    \"format\" : \"WEBVTT\",\n" +
            "    \"value\" : \"V0VCVlRUCgoxCjAwOjAwOjAwLjAwMCAtLT4gMDA6MDE6MDQuMDAwCmJsYQoK\",\n" +
            "    \"charset\" : \"UTF-8\"\n" +
            "  },\n" +
            "  \"type\" : \"CAPTION\",\n" +
            "   \"owner\" : \"BROADCASTER\",\n" +
            "  \"workflow\" : \"FOR_PUBLICATION\",\n" +
            "  \"lang\" : \"nl\",\n" +
            "  \"cueCount\" : 1\n" +
            "}");

    }

    @Test

    public void guessFormat() {
        Subtitles subtitles = Subtitles.builder()
            .value(getClass().getResourceAsStream("/WO_NPO_14933889.vtt"))
            .build();

        assertThat(subtitles.getContent().getFormat()).isEqualTo(SubtitlesFormat.WEBVTT);
    }
}
