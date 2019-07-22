package nl.vpro.domain.gtaa;

import org.junit.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class GTAANewThesaurusObjectTest {


    @Test
    public void json() throws Exception {

        GTAANewThesaurusObject person =
            GTAANewThesaurusObject.builder().value("bla")
                .scheme(Scheme.genre)
                .value("new genre")
                .note("Bla")
                .build();


        Jackson2TestUtil.roundTripAndSimilar(person, "{\n" +
            "  \"newObjectType\" : \"concept\",\n" +
            "  \"value\" : \"new genre\",\n" +
            "  \"notes\" : [ \"Bla\" ],\n" +
            "  \"objectType\" : \"genre\"\n" +
            "}");

    }


    @Test
    public void xml() throws Exception {
        GTAANewThesaurusObject person = GTAANewThesaurusObject
            .builder()
            .scheme(Scheme.maker)
            .value("Pietje Puk")
            .note("test").build();
        JAXBTestUtil.roundTripAndSimilar(person, "<gtaa:newConcept gtaa:objectType=\"maker\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:gtaa=\"urn:vpro:gtaa:2017\" xmlns:openskos=\"http://openskos.org/xmlns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
            "    <gtaa:value>Pietje Puk</gtaa:value>\n" +
            "    <gtaa:note>test</gtaa:note>\n" +
            "</gtaa:newConcept>");

    }

}
