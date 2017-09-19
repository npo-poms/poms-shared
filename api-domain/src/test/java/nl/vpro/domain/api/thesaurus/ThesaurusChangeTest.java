package nl.vpro.domain.api.thesaurus;

import nl.vpro.domain.media.Person;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.media.gtaa.GTAAPerson;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.Test;

import java.time.LocalDateTime;

public class ThesaurusChangeTest {


    @Test
    public void xml() {
        ThesaurusChange change =
                ThesaurusChange.builder()
                        .id("http://data.beeldengeluid.nl/gtaa/1672221")
                        .deleted(false)
                        .publishDate(LocalDateTime.of(2017, 1, 30, 11, 41).atZone(Schedule.ZONE_ID).toInstant())
                        .object(new GTAAPerson(new Person("pietje", "puk")))

                        .build();

        // TOO ns4?
        JAXBTestUtil.assertThatXml(change).isSimilarTo("<local:thesaurusChange publishDate=\"2017-01-30T11:41:00+01:00\" id=\"http://data.beeldengeluid.nl/gtaa/1672221\" deleted=\"false\" xmlns=\"http://www.openarchives.org/OAI/2.0/\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:api=\"urn:vpro:api:2013\" xmlns:ns4=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\" xmlns:openskos=\"http://openskos.org/xmlns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
                "    <ns4:person>\n" +
                "        <ns4:givenName>pietje</ns4:givenName>\n" +
                "        <ns4:familyName>puk</ns4:familyName>\n" +
                "    </ns4:person>\n" +
                "</local:thesaurusChange>\n");
    }


    @Test
    public void json() throws Exception {
        ThesaurusChange change =
                ThesaurusChange.builder()
                        .id("http://data.beeldengeluid.nl/gtaa/1672221")
                        .deleted(false)
                        .publishDate(LocalDateTime.of(2017, 1, 30, 11, 41).atZone(Schedule.ZONE_ID).toInstant())
                        .object(new GTAAPerson(new Person("pietje", "puk")))
                        .build();

        Jackson2TestUtil.roundTripAndSimilarAndEquals(change, "{\n" +
                "  \"publishDate\" : 1485772860000,\n" +
                "  \"id\" : \"http://data.beeldengeluid.nl/gtaa/1672221\",\n" +
                "  \"deleted\" : false,\n" +
                "  \"object\" : {\n" +
                "    \"objectType\" : \"person\",\n" +
                "    \"givenName\" : \"pietje\",\n" +
                "    \"familyName\" : \"puk\"\n" +
                "  }\n" +
                "}");

    }

}


