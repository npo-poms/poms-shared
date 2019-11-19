package nl.vpro.domain.gtaa;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.util.BindingUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@RunWith(Parameterized.class)
@Slf4j
public class GTAAConceptTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.stream(Scheme.values()).filter(
            s -> s != Scheme.person // persons are tested separatedly
        ).map(s -> new Object[]{s}).collect(Collectors.toList());
    }


    Scheme scheme;
    AbstractGTAAConcept concept;

    String xmlType;
    String jsonType;

    public GTAAConceptTest(Scheme scheme) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        this.scheme = scheme;
        log.info("{}", this.scheme);
        xmlType = scheme.getXmlElement();
        jsonType = scheme.getJsonObjectType();
        assertThat(jsonType).isEqualTo(xmlType.toLowerCase());
        concept = (AbstractGTAAConcept) this.scheme.getImplementation().getConstructor().newInstance();
        concept.setName("concept");
        concept.setId(URI.create("http://gtaa/1234"));
        concept.setScopeNotes(Arrays.asList("bla"));
        concept.setLastModified(LocalDateTime.of(2017, 9, 20, 10, 43, 0).atZone(BindingUtils.DEFAULT_ZONE).toInstant());
        concept.setStatus(Status.approved);
    }

     @Test
    public void xml() {

        JAXBTestUtil.roundTripAndSimilarAndEquals(concept, "<gtaa:" + xmlType + " gtaa:id=\"http://gtaa/1234\" gtaa:status=\"approved\" gtaa:lastModified=\"2017-09-20T10:43:00+02:00\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\" xmlns:oai=\"http://www.openarchives.org/OAI/2.0/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:gtaa=\"urn:vpro:gtaa:2017\" xmlns:openskos=\"http://openskos.org/xmlns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
            "    <gtaa:name>concept</gtaa:name>\n" +
            "    <gtaa:scopeNote>bla</gtaa:scopeNote>\n" +
            "</gtaa:" + xmlType + ">");

    }

    @Test
    public void json() {

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
