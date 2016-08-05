/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.subtitles;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import javax.xml.bind.JAXB;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class SubtitlesTest {



    @Test
    public void testMarshalToXml() throws IOException, SAXException {
        Subtitles subtitles = new Subtitles("VPRO_1234", Duration.ofMillis(2 * 60 * 1000), "Ondertiteling tekst");
        subtitles.setCreationDate(Instant.ofEpochMilli(0));
        subtitles.setLastModified(Instant.ofEpochMilli(0));


        subtitles.setLanguage(new Locale("nl", "NL"));


        JAXBTestUtil.roundTripAndSimilar(subtitles,
                "<subtitles mid=\"VPRO_1234\" creationDate=\"1970-01-01T01:00:00.000+01:00\" lastModified=\"1970-01-01T01:00:00.000+01:00\" type=\"CAPTION\" format=\"WEBVTT\" xml:lang=\"nl-NL\" xmlns=\"urn:vpro:media:subtitles:2009\">\n" +
                "    <offset>P0DT0H2M0.000S</offset>\n" +
                "    <content>Ondertiteling tekst</content>\n" +
                "</subtitles>");
    }

    @Test
    public void testUnmarshallFromXml() {
        String xml =
            "<subtitles mid=\"VPRO_1234\" creationDate=\"1970-01-01T01:00:00+01:00\" xmlns=\"urn:vpro:media:subtitles:2009\">\n" +
            "    <offset>P0DT0H2M0.000S</offset>\n" +
            "    <content>Ondertiteling tekst</content>\n" +
            "</subtitles>\n";

        StringReader reader = new StringReader(xml);

        Subtitles subtitles = JAXB.unmarshal(reader, Subtitles.class);

        assertThat(subtitles.getMid()).isEqualTo("VPRO_1234");
        assertThat(subtitles.getOffset()).isEqualTo(Duration.ofMillis(120000));
        assertThat(subtitles.getContent()).isEqualTo("Ondertiteling tekst");
    }
}
