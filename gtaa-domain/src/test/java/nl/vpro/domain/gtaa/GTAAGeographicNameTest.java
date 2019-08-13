package nl.vpro.domain.gtaa;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.util.BindingUtils;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class GTAAGeographicNameTest {
    GTAAGeographicName geoName = GTAAGeographicName.builder()
            .scopeNotes(Arrays.asList("bla"))
            .value("Amsterdam")
            .id(URI.create("http://gtaa/1234"))
            .status(Status.approved)
            .lastModified(LocalDateTime.of(2017, 9, 20, 10, 43, 0).atZone(BindingUtils.DEFAULT_ZONE).toInstant())
            .build();


     @Test
    public void xml() throws Exception {

        JAXBTestUtil.roundTripAndSimilarAndEquals(geoName, "<gtaa:geographicName gtaa:id=\"http://gtaa/1234\" gtaa:status=\"approved\" gtaa:lastModified=\"2017-09-20T10:43:00+02:00\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\" xmlns:oai=\"http://www.openarchives.org/OAI/2.0/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:gtaa=\"urn:vpro:gtaa:2017\" xmlns:openskos=\"http://openskos.org/xmlns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
            "    <gtaa:value>Amsterdam</gtaa:value>\n" +
            "    <gtaa:scopeNote>bla</gtaa:scopeNote>\n" +
            "</gtaa:geographicName>");

    }

    @Test
    public void json() throws Exception {

        Jackson2TestUtil.roundTripAndSimilarAndEquals(geoName, "{\n" +
            "  \"objectType\" : \"geographicname\",\n" +
            "  \"value\" : \"Amsterdam\",\n" +
            "  \"scopeNotes\" : [ \"bla\" ],\n" +
            "  \"id\" : \"http://gtaa/1234\",\n" +
            "  \"status\" : \"approved\",\n" +
            "  \"lastModified\" : 1505896980000\n" +
            "}");

    }
}
