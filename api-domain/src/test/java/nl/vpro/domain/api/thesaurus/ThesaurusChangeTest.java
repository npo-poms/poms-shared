package nl.vpro.domain.api.thesaurus;

import java.time.LocalDateTime;

import org.junit.Test;

import nl.vpro.domain.gtaa.GTAAPerson;
import nl.vpro.domain.media.Schedule;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

public class ThesaurusChangeTest {


    @Test
    public void xml() {
        ThesaurusChange change =
            ThesaurusChange.builder()
                .id("http://data.beeldengeluid.nl/gtaa/1672221")
                .deleted(false)
                .publishDate(LocalDateTime.of(2017, 1, 30, 11, 41).atZone(Schedule.ZONE_ID).toInstant())
                .object(GTAAPerson.builder().givenName("pietje").familyName("puk").build())
                .build();


        JAXBTestUtil.assertThatXml(change).isSimilarTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<api:thesaurusChange publishDate=\"2017-01-30T11:41:00+01:00\" id=\"http://data.beeldengeluid.nl/gtaa/1672221\" deleted=\"false\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\" xmlns:oai=\"http://www.openarchives.org/OAI/2.0/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:gtaa=\"urn:vpro:gtaa:2017\" xmlns:openskos=\"http://openskos.org/xmlns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
            "    <api:person>\n" +
            "        <gtaa:value>puk, pietje</gtaa:value>\n" +
            "        <gtaa:givenName>pietje</gtaa:givenName>\n" +
            "        <gtaa:familyName>puk</gtaa:familyName>\n" +
            "    </api:person>\n" +
            "</api:thesaurusChange>");
    }


    @Test
    public void json() throws Exception {
        ThesaurusChange change =
            ThesaurusChange.builder()
                .id("http://data.beeldengeluid.nl/gtaa/1672221")
                .deleted(false)
                .publishDate(LocalDateTime.of(2017, 1, 30, 11, 41).atZone(Schedule.ZONE_ID).toInstant())
                .object(GTAAPerson.builder().familyName("puk").givenName("pietje").gtaaUri("http://data.beeldengeluid.nl/gtaa/1672221").build())
                .build();

        Jackson2TestUtil.roundTripAndSimilarAndEquals(change, "{\n" +
            "  \"publishDate\" : 1485772860000,\n" +
            "  \"id\" : \"http://data.beeldengeluid.nl/gtaa/1672221\",\n" +
            "  \"deleted\" : false,\n" +
            "  \"object\" : {\n" +
            "    \"objectType\" : \"person\",\n" +
            "    \"givenName\" : \"pietje\",\n" +
            "    \"familyName\" : \"puk\",\n" +
            "    \"value\" : \"puk, pietje\",\n" +
            "    \"id\" : \"http://data.beeldengeluid.nl/gtaa/1672221\"\n" +
            "  }\n" +
            "}");

    }

}


