package nl.vpro.domain.gtaa;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class GTAANewGenericConceptTest {


    @Test
    public void json() {

        GTAANewGenericConcept person =
            GTAANewGenericConcept.builder()
                .scheme(Scheme.genre)
                .name("new genre")
                .scopeNote("Bla")
                .build();


        Jackson2TestUtil.roundTripAndSimilar(person, "{\n" +
            "  \"newObjectType\" : \"concept\",\n" +
            "  \"name\" : \"new genre\",\n" +
            "  \"scopeNotes\" : [ \"Bla\" ],\n" +
            "  \"objectType\" : \"genre\"\n" +
            "}");

    }


    @Test
    public void xml() {
        GTAANewGenericConcept person = GTAANewGenericConcept
            .builder()
            .scheme(Scheme.maker)
            .name("Pietje Puk")
            .scopeNote("test").build();
        JAXBTestUtil.roundTripAndSimilar(person, "<gtaa:newConcept gtaa:objectType=\"maker\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:gtaa=\"urn:vpro:gtaa:2017\" xmlns:openskos=\"http://openskos.org/xmlns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
            "    <gtaa:name>Pietje Puk</gtaa:name>\n" +
            "    <gtaa:scopeNote>test</gtaa:scopeNote>\n" +
            "</gtaa:newConcept>");

    }

}
