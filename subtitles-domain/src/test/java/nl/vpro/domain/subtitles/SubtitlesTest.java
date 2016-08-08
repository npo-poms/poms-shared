/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.subtitles;

import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;

import javax.xml.bind.JAXB;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class SubtitlesTest {



    @Test
    public void testMarshalToXml() throws IOException, SAXException {
        Subtitles subtitles = Subtitles.webvtt("VPRO_1234", Duration.ofMillis(2 * 60 * 1000), SubtitlesUtil.DUTCH,  "Ondertiteling tekst");
        subtitles.setCreationDate(Instant.ofEpochMilli(0));
        subtitles.setLastModified(Instant.ofEpochMilli(0));


        JAXBTestUtil.roundTripAndSimilar(subtitles,
                "<subtitles mid=\"VPRO_1234\" offset=\"P0DT0H2M0.000S\" creationDate=\"1970-01-01T01:00:00.000+01:00\" lastModified=\"1970-01-01T01:00:00.000+01:00\" type=\"CAPTION\" xml:lang=\"nl-NL\" xmlns=\"urn:vpro:media:subtitles:2009\">\n" +
                    "    <content format=\"WEBVTT\">Ondertiteling tekst</content>\n" +
                    "</subtitles>");
    }

    @Test
    public void testUnmarshallFromXml() {
        String xml =
            "<subtitles mid=\"VPRO_1234\" offset=\"P0DT0H2M0.000S\" creationDate=\"1970-01-01T01:00:00.000+01:00\" lastModified=\"1970-01-01T01:00:00.000+01:00\" type=\"CAPTION\" xml:lang=\"nl-NL\" xmlns=\"urn:vpro:media:subtitles:2009\">\n" +
                "    <content format=\"WEBVTT\">Ondertiteling tekst</content>\n" +
                "</subtitles>";

        StringReader reader = new StringReader(xml);

        Subtitles subtitles = JAXB.unmarshal(reader, Subtitles.class);

        assertThat(subtitles.getMid()).isEqualTo("VPRO_1234");
        assertThat(subtitles.getOffset()).isEqualTo(Duration.ofMillis(120000));
        assertThat(subtitles.getContent()).isEqualTo("Ondertiteling tekst");
    }

    @Test
    public void json() throws Exception {
        Subtitles subtitles = Subtitles.webvtt("VPRO_1234", Duration.ofMillis(2 * 60 * 1000), SubtitlesUtil.DUTCH, "Ondertiteling tekst");
        subtitles.setCreationDate(Instant.ofEpochMilli(0));
        subtitles.setLastModified(Instant.ofEpochMilli(0));

        Jackson2TestUtil.roundTripAndSimilar(subtitles, "{\n" +
            "  \"mid\" : \"VPRO_1234\",\n" +
            "  \"offset\" : 120000,\n" +
            "  \"content\" : {\n" +
            "    \"value\" : \"Ondertiteling tekst\",\n" +
            "    \"format\" : \"WEBVTT\"\n" +
            "  },\n" +
            "  \"creationDate\" : \"1970-01-01T01:00:00.000+01:00\",\n" +
            "  \"lastModified\" : \"1970-01-01T01:00:00.000+01:00\",\n" +
            "  \"type\" : \"CAPTION\",\n" +
            "  \"lang\" : \"nl-NL\"\n" +
            "}");
    }
}
