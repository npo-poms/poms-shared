package nl.vpro.w3.rdf;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Collections;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import nl.vpro.openarchives.oai.Label;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.util.BindingUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class RDFTest {

    @Test
    public void testJaxbBinding() {
        String in = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:openskos="http://openskos.org/xmlns#" xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:dcterms="http://purl.org/dc/terms/" \t\topenskos:numFound="122468"
                  openskos:start="0"
                  openskos:maxScore="5.2201943">
              <rdf:Description xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" rdf:about="http://data.beeldengeluid.nl/gtaa/123361">
                <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
                <openskos:status>approved</openskos:status>
                <skos:notation>123361</skos:notation>
                <dcterms:dateAccepted>2007-11-24T05:25:06Z</dcterms:dateAccepted>
                <dcterms:creator>NAA</dcterms:creator>
                <dcterms:dateSubmitted>2007-11-24T05:25:06Z</dcterms:dateSubmitted>
                <skos:prefLabel xml:lang="nl">Ligtendag, Wim</skos:prefLabel>
                <skos:hiddenLabel xml:lang="nl">Wim Ligtendag</skos:hiddenLabel>
                <skos:historyNote xml:lang="nl">Goedgekeurd door: NAA</skos:historyNote>
                <skos:inScheme rdf:resource="http://data.beeldengeluid.nl/gtaa/Persoonsnamen"/>
                <dcterms:modified>2013-01-11T14:48:10.557Z</dcterms:modified>
              </rdf:Description>
            </rdf:RDF>""";


        RDF out = JAXBTestUtil.roundTripAndSimilar(in, RDF.class, JAXBTestUtil.IGNORE_ELEMENT_ORDER);

        assertThat(out.getDescriptions()).hasSize(1);
        assertThat(out.getDescriptions().get(0).getPrefLabel().getValue()).isEqualTo("Ligtendag, Wim");

        RDF also = JAXBTestUtil.unmarshal(in, RDF.class);
        assertThat(also.getDescriptions()).hasSize(1);

        assertThat(also).isEqualTo(out);

    }

    @Test
    public void testXmlLangOnLabel() {
        RDF rdf = new RDF();
        rdf.setDescriptions(Collections.singletonList(
            Description
                .builder()
                .prefLabel(new Label("prefLabel"))
                .dateSubmitted(LocalDateTime.of(2018, 6, 19, 14, 28).atZone(BindingUtils.DEFAULT_ZONE))
                .build()
            )
        );

        final RDF out = JAXBTestUtil.roundTripAndSimilar(rdf, """
            <rdf:RDF xmlns="http://www.openarchives.org/OAI/2.0/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:skosxl="http://www.w3.org/2008/05/skos-xl#" xmlns:oai="http://www.openarchives.org/OAI/2.0/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:gtaa="urn:vpro:gtaa:2017" xmlns:openskos="http://openskos.org/xmlns#" xmlns:dc="http://purl.org/dc/elements/1.1/">
                <rdf:Description>
                    <skos:prefLabel xml:lang="nl">prefLabel</skos:prefLabel>
                    <dcterms:dateSubmitted>2018-06-19T14:28:00+02:00</dcterms:dateSubmitted>
                </rdf:Description>
            </rdf:RDF>""");
        assertThat(out.getDescriptions().get(0).getPrefLabel().getValue()).isEqualTo("prefLabel");
    }

    @Test
    public void testJaxbBindingWithXLLabel() {
        String in = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rdf:RDF xmlns:skosxl='http://www.w3.org/2008/05/skos-xl#' xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:openskos="http://openskos.org/xmlns#" xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:dcterms="http://purl.org/dc/terms/" \t\topenskos:numFound="122468"
                  openskos:start="0"
                  openskos:maxScore="5.2201943">
              <rdf:Description xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" rdf:about="http://data.beeldengeluid.nl/gtaa/123361">
                <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
                <openskos:status>approved</openskos:status>
                <skos:notation>123361</skos:notation>
                <dcterms:dateAccepted>2007-11-24T05:25:06Z</dcterms:dateAccepted>
                <dcterms:creator>NAA</dcterms:creator>
                <dcterms:dateSubmitted>2007-11-24T05:25:06Z</dcterms:dateSubmitted>
            <skosxl:prefLabel>
              <rdf:Description rdf:about="http://data.beeldengeluid.nl/api/labels/d0e34b9f-d31d-4858-b4e7-3bcbdd377c26">
                <rdf:type rdf:resource="http://www.w3.org/2008/05/skos-xl#Label"/>
                <skosxl:literalForm xml:lang="nl">doodstraf</skosxl:literalForm>
                <openskos:tenant>beg</openskos:tenant>
              </rdf:Description>
            </skosxl:prefLabel>
                <skos:hiddenLabel xml:lang="nl">Wim Ligtendag</skos:hiddenLabel>
                <skos:historyNote xml:lang="nl">Goedgekeurd door: NAA</skos:historyNote>
                <skos:inScheme rdf:resource="http://data.beeldengeluid.nl/gtaa/Persoonsnamen"/>
                <dcterms:modified>2013-01-11T14:48:10.557Z</dcterms:modified>
              </rdf:Description>
            </rdf:RDF>""";


        JAXB.unmarshal(new StringReader(in), RDF.class);

        RDF out = JAXBTestUtil.roundTripAndSimilar(in, RDF.class, JAXBTestUtil.IGNORE_ELEMENT_ORDER);

        assertThat(out.getDescriptions()).hasSize(1);
        assertThat(out.getDescriptions().get(0).getXlPrefLabel().getDescription().getLiteralForm().getValue()).isEqualTo("doodstraf");

        RDF also = JAXBTestUtil.unmarshal(in, RDF.class);
        assertThat(also.getDescriptions()).hasSize(1);

        assertThat(also).isEqualTo(out);

    }


    @Test
    @Disabled
    public void testJaxbBindingWithXLLabel2() throws IOException {
        InputStream input = RDFTest.class.getClassLoader().getResourceAsStream("response-acc.xml");
        RDF out = JAXBTestUtil.roundTripAndSimilar(input, RDF.class, JAXBTestUtil.IGNORE_ELEMENT_ORDER);

        assertThat(out.getDescriptions()).hasSize(1);
        assertThat(out.getDescriptions().get(0).getXlPrefLabel().getDescription().getLiteralForm().getValue()).isEqualTo("doodstraf");



    }
}
