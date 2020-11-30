package nl.vpro.domain.gtaa;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.util.BindingUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */

@Slf4j
public class GTAAConceptTest {


    public static Stream<Object[]> data() {
        return Arrays.stream(Scheme.values()).filter(
            s -> s != Scheme.person // persons are tested separatedly
        )
            .map(scheme -> {
                final String xmlType =  scheme.getXmlElement();
                final String jsonType = scheme.getJsonObjectType();
                assertThat(jsonType).isEqualTo(xmlType.toLowerCase());
                try {
                    AbstractGTAAConcept concept = (AbstractGTAAConcept) scheme.getImplementation().getConstructor().newInstance();

                    concept.setName("concept");
                    concept.setId(URI.create("http://gtaa/1234"));
                    concept.setScopeNotes(Arrays.asList("bla"));
                    concept.setLastModified(LocalDateTime.of(2017, 9, 20, 10, 43, 0).atZone(BindingUtils.DEFAULT_ZONE).toInstant());
                    concept.setStatus(Status.approved);
                    return new Object[]{concept, xmlType, jsonType};
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
    }




    @ParameterizedTest
    @MethodSource("data")
    public void xml(AbstractGTAAConcept concept, String xmlType, String jsonType) {
        JAXBTestUtil.roundTripAndSimilarAndEquals(concept, "<gtaa:" + xmlType + " gtaa:id=\"http://gtaa/1234\" gtaa:status=\"approved\" gtaa:lastModified=\"2017-09-20T10:43:00+02:00\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\" xmlns:oai=\"http://www.openarchives.org/OAI/2.0/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:gtaa=\"urn:vpro:gtaa:2017\" xmlns:openskos=\"http://openskos.org/xmlns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
            "    <gtaa:name>concept</gtaa:name>\n" +
            "    <gtaa:scopeNote>bla</gtaa:scopeNote>\n" +
            "</gtaa:" + xmlType + ">");

    }

    @ParameterizedTest
    @MethodSource("data")
    public void json(AbstractGTAAConcept concept, String xmlType, String jsonType) {

        Jackson2TestUtil.roundTripAndSimilarAndEquals(concept, "{\n" +
            "  \"objectType\" : \"" + jsonType + "\",\n" +
            "  \"id\" : \"http://gtaa/1234\",\n" +
            "  \"name\" : \"concept\",\n" +
            "   \"scopeNotes\" : [ \"bla\" ],\n" +
            "  \"status\" : \"approved\",\n" +
            "  \"lastModified\" : 1505896980000\n" +
            "}");

    }



}
