package nl.vpro.domain.gtaa;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import nl.vpro.jackson2.rs.JsonIdAdderBodyReader;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class GTAANewGenericConceptTest {

    final JsonIdAdderBodyReader reader = new JsonIdAdderBodyReader();

    @Test
    public void json() {

        GTAANewGenericConcept person =
            GTAANewGenericConcept.builder()
                .scheme(Scheme.genre)
                .name("new genre")
                .scopeNote("Bla")
                .build();


        Jackson2TestUtil.roundTripAndSimilar(person, """
            {
              "newObjectType" : "concept",
              "name" : "new genre",
              "scopeNotes" : [ "Bla" ],
              "objectType" : "genre"
            }""");

    }

     @Test
    public void jsonWithoutType() throws IOException {
        String json = """
            {
              "name" : "new genre",
              "scopeNotes" : [ "Bla" ],
              "objectType" : "genre"
            }""";

        GTAANewGenericConcept gtaaNewGenericConcept =
            (GTAANewGenericConcept) reader.readFrom(Object.class,
                GTAANewGenericConcept.class,
                null,
                APPLICATION_JSON_TYPE, null, new ByteArrayInputStream(json.getBytes()));

        assertThat(gtaaNewGenericConcept.getObjectType()).isEqualTo(Scheme.genre);
    }


    @Test
    public void xml() {
        GTAANewGenericConcept person = GTAANewGenericConcept
            .builder()
            .scheme(Scheme.maker)
            .name("Pietje Puk")
            .scopeNote("test").build();
        JAXBTestUtil.roundTripAndSimilar(person, """
            <gtaa:newConcept gtaa:objectType="maker" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:skosxl="http://www.w3.org/2008/05/skos-xl#" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:gtaa="urn:vpro:gtaa:2017" xmlns:openskos="http://openskos.org/xmlns#" xmlns:dc="http://purl.org/dc/elements/1.1/">
                <gtaa:name>Pietje Puk</gtaa:name>
                <gtaa:scopeNote>test</gtaa:scopeNote>
            </gtaa:newConcept>""");

    }

}
