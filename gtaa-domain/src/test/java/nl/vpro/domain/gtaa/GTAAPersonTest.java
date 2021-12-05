package nl.vpro.domain.gtaa;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.openarchives.oai.Label;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.util.BindingUtils;
import nl.vpro.w3.rdf.Description;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class GTAAPersonTest {
    @Test
    public void json() {
        GTAAPerson person = GTAAPerson
            .builder()
            .givenName("Pietje")
            .familyName("puk")
            .gtaaUri("http://data.beeldengeluid.nl/gtaa/167222")
            .build();

        Jackson2TestUtil.roundTripAndSimilarAndEquals(person,
            "{\"objectType\" : \"person\",\n" +
                "  \"id\" : \"http://data.beeldengeluid.nl/gtaa/167222\",\n" +
                "  \"name\" : \"puk, Pietje\",\n" +
                "  \"givenName\" : \"Pietje\",\n" +
                "  \"familyName\" : \"puk\"\n" +
                "}");

    }


    @Test
    public void json2() throws IOException {
        String example = "{ \"objectType\" : \"person\", \"familyName\":\"Puk\",\"givenName\":\"Pietje\",\"notes\":[null,\"vanuit POMS voor: POW_00700386\"]}\n" +
            "Name\n";
        GTAAPerson person = Jackson2Mapper.getLenientInstance().readValue(new StringReader(example), GTAAPerson.class);
        assertThat(person.getFamilyName()).isEqualTo("Puk");
        assertThat(person.getGivenName()).isEqualTo("Pietje");
    }

    @Test
    public void jsonAsThesaurusObjectReturnsPerson() throws Exception {


        GTAAConcept object = Jackson2Mapper.getInstance().readValue(new StringReader("{\n" +
            "  \"objectType\" : \"person\",\n" +
            "  \"familyName\" : \"puk\",\n" +
            "  \"name\" : \"null puk\",\n" +
            "  \"prefLabel\" : \"null puk\"\n" +
            "}"), GTAAConcept.class);

        assertThat(object).isInstanceOf(GTAAPerson.class);

    }


    @Test
    public void xml() {
        GTAAPerson person = GTAAPerson.builder()
            .scopeNotes(singletonList("bla"))
            .knownAs(singletonList(Names.builder().familyName("pietje").build()))
            .familyName("puk")
            .gtaaUri("http://gtaa/1234")
            .status(Status.approved)
            .lastModified(LocalDateTime.of(2017, 9, 20, 10, 43, 0).atZone(BindingUtils.DEFAULT_ZONE).toInstant())
            .build();

        JAXBTestUtil.roundTripAndSimilarAndEquals(person, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<gtaa:person gtaa:status=\"approved\" gtaa:lastModified=\"2017-09-20T10:43:00+02:00\" gtaa:id=\"http://gtaa/1234\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\" xmlns:oai=\"http://www.openarchives.org/OAI/2.0/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:gtaa=\"urn:vpro:gtaa:2017\" xmlns:openskos=\"http://openskos.org/xmlns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
            "    <gtaa:name>puk</gtaa:name>\n" +
            "    <gtaa:familyName>puk</gtaa:familyName>\n" +
            "    <gtaa:scopeNote>bla</gtaa:scopeNote>\n" +
            "    <gtaa:knownAs>\n" +
            "        <gtaa:familyName>pietje</gtaa:familyName>\n" +
            "    </gtaa:knownAs>\n" +
            "</gtaa:person>\n");

    }

    @Test
    public void create() {
        GTAAPerson person = new GTAAPerson("Pietje", "Puk", Status.approved);
        assertThat(person.getGivenName()).isEqualTo("Pietje");

        person = GTAAPerson.create(null, "prelabel");
        assertThat(person).isNull();

        person = GTAAPerson.create(Description.builder()
            .inScheme(Scheme.person)
            .prefLabel(Label.forValue("pietje puk"))
            .tenant("poms")
            .acceptedBy("bla")
            .status(Status.approved)
            .uuid(UUID.randomUUID())
            .about(URI.create("gtaa://person/1234"))
            .build(), "Puk, Pietje");

        assertThat(person.getGivenName()).isEqualTo("Pietje");
        assertThat(person.getId()).isEqualTo(URI.create("gtaa://person/1234"));
        assertThat(person.getGtaaUri()).isEqualTo("gtaa://person/1234");

    }
}
