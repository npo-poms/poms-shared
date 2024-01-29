/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.extern.slf4j.Slf4j;

import java.io.StringReader;
import java.time.Duration;

import jakarta.xml.bind.*;
import jakarta.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.media.AVType;
import nl.vpro.domain.media.AgeRating;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.validation.WarningValidatorGroup;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class SegmentUpdateTest extends MediaUpdateTest {

    @Test
    public void testCreate() {
        SegmentUpdate update = SegmentUpdate.create();
        update.setAVType(AVType.VIDEO);
        update.setMainTitle("main title");
        update.setVersion(null);
        update.setStart(Duration.ofMillis(100));
        update.setMidRef("MID_123");
        update.setAgeRating(AgeRating.ALL);
        update.setBroadcasters("VPRO");

        String expected = """
            <segment xmlns="urn:vpro:media:update:2009" avType="VIDEO" embeddable="true" midRef="MID_123" xmlns:shared="urn:vpro:shared:2009" xmlns:media="urn:vpro:media:2009">
                <broadcaster>VPRO</broadcaster>
                <title type="MAIN">main title</title>
                <credits/>
                <ageRating>ALL</ageRating>
                <locations/>
                <images/>
                <start>P0DT0H0M0.100S</start>
            </segment>
            """;

        SegmentUpdate rounded = JAXBTestUtil.roundTripAndSimilar(update, expected);
        assertThat(update.isStandalone()).isTrue();
        assertThat(rounded.isStandalone()).isTrue();

        assertThat(update.violations()).isEmpty();
        assertThat(update.violations(WarningValidatorGroup.class)).hasSize(1); // about duration

        update.setMidRef(null);
        assertThat(update.violations()).hasSize(1);
        log.info("{}", update.violations());


    }

    @Test
    public void unmarshal() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(SegmentUpdate.class, ProgramUpdate.class, GroupUpdate.class);

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<update:segment xmlns:update=\"urn:vpro:media:update:2009\" xmlns=\"urn:vpro:media:2009\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:media=\"urn:vpro:media:2009\" xmlns:s=\"urn:vpro:media:search:2012\" xmlns:shared=\"urn:vpro:shared:2009\" midRef=\"VARA_101377256\" avType=\"VIDEO\" embeddable=\"true\" publishStart=\"2016-05-11T00:18:00+02:00\">" +
                "<update:broadcaster>VARA</update:broadcaster><update:title type=\"MAIN\">Haalt Douwe Bob de finale van het Songfestival?</update:title><update:description type=\"MAIN\">Onze Douwe Bob nam het met zijn nummer Slow down op tegen Rusland, Malta en Armenië. Kenners voorspelden een finaleplek voor Douwe en dat heeft hij ook waar weten te maken. We belde direct na de halve finale met Cornald Maas, die in Stockholm was. Daarna gingen we nabeschouwen met Maan, Shirma Rouse en Gijs Staverman. </update:description><update:duration>P0DT0H14M40.000S</update:duration><update:credits><update:person role=\"UNDEFINED\"><update:givenName>Maan</update:givenName><update:familyName>de Steenwinkel</update:familyName></update:person><update:person role=\"UNDEFINED\"><update:givenName>Shirma</update:givenName><update:familyName>Rouse</update:familyName></update:person><update:person role=\"UNDEFINED\"><update:givenName>Gijs</update:givenName><update:familyName>Staverman</update:familyName></update:person><update:person role=\"UNDEFINED\"><update:givenName>Jeroen</update:givenName><update:familyName>Pauw</update:familyName></update:person></update:credits><update:website>http://pauw.vara.nl</update:website><update:locations/><update:scheduleEvents/><update:images><update:image type=\"STILL\" highlighted=\"false\"><update:title>Haalt Douwe Bob de finale van het Songfestival?</update:title><update:description>Onze Douwe Bob nam het met zijn nummer Slow down op tegen Rusland, Malta en Armenië. Kenners voorspelden een finaleplek voor Douwe en dat heeft hij ook waar weten te maken. We belde direct na de halve finale met Cornald Maas, die in Stockholm was. Daarna gingen we nabeschouwen met Maan, Shirma Rouse en Gijs Staverman. </update:description><update:imageLocation><update:url>http://download.omroep.nl/vara/2016/05/10/20160510-pauw-songfestival_7.jpg</update:url></update:imageLocation></update:image></update:images><update:start>P0DT0H1M15.000S</update:start></update:segment>";
        MediaUpdate<?> segment = (MediaUpdate<?>) jc.createUnmarshaller().unmarshal(new StringReader(xml));
        assertThat(segment).isInstanceOf(SegmentUpdate.class);

    }



    @Test
    public void ofProgram() {

        String expected =
            """
                <program xmlns="urn:vpro:media:update:2009"  type="CLIP" xmlns:shared="urn:vpro:shared:2009" xmlns:media="urn:vpro:media:2009">
                    <broadcaster>MAX</broadcaster>
                    <geoLocations/>
                    <topics/>
                    <credits/>
                    <locations/>
                    <scheduleEvents/>
                    <images/>
                    <segments>
                <segment  avType="VIDEO" embeddable="true" midRef="MID_123" xmlns:shared="urn:vpro:shared:2009" xmlns:media="urn:vpro:media:2009">
                    <title type="MAIN">main title</title>
                    <credits/>
                    <ageRating>ALL</ageRating>
                    <locations/>
                    <images/>
                    <start>P0DT0H0M0.100S</start>
                </segment>
                </segments></program>""";
        ProgramUpdate u = JAXB.unmarshal(new StringReader(expected), ProgramUpdate.class);
        u.setMainTitle("hoi");
        u.setAVType(AVType.VIDEO);
        assertThat(u.violations()).hasSize(1);
        u.setMid("MID_123");
        assertThat(u.violations()).hasSize(0);
        u.setMid("MID_124");
        assertThat(u.violations()).hasSize(1);





    }
    /**
     * Naar aanleiding van slack-communicatie met de VARA.
     */
    @Test
    public void testNamespaces() throws JAXBException, ParserConfigurationException, SAXException {
        String example = """
            <?xml version="1.0"?>
            <ns0:segment xmlns:ns0="urn:vpro:media:update:2009" avType="VIDEO" embeddable="true" mid="POMS_BV_12672829" midRef="BV_101386500"   sortDate="3333-01-24T10:12:00+00:00" urn="urn:vpro:media:segment:102562422">
              <ns0:title type="MAIN">Test poms export</ns0:title>
              <ns0:broadcaster>BNN</ns0:broadcaster>
              <broadcaster>BNVA</broadcaster>
              <broadcaster xmlns='urn:completelydifferent'>XXX</broadcaster>
            </ns0:segment>""";
        JAXBContext jaxbContext = JAXBContext.newInstance(SegmentUpdate.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();


        SegmentUpdate update = (SegmentUpdate) unmarshaller.unmarshal(new StringReader(example));
        assertThat(update.getBroadcasters()).containsExactly("BNN"); // FAILS, it will also read BNVA

        ///assertThat(update.getBroadcasters()).containsExactly("BNN", "BNVA", "XXX"); // It did this in older jaxb versions
    }
}
