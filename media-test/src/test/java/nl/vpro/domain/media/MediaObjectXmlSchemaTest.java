/*
 * Copyright (C) 2008 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;
import jakarta.xml.bind.*;
import jakarta.xml.bind.util.JAXBSource;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.media.update.ProgramUpdate;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.i18n.Locales;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static java.nio.charset.StandardCharsets.UTF_8;
import static nl.vpro.domain.media.MediaTestDataBuilder.program;
import static nl.vpro.test.util.jaxb.JAXBTestUtil.assertThatXml;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * This class verifies JAXB XML output format and whether this format complies to the vproMedia.xsd schema definitions.
 * It's located here because then it can use the test data builder for more concise code.
 */
@SuppressWarnings({"UnnecessaryLocalVariable"})
@Slf4j
public class MediaObjectXmlSchemaTest {

    private static JAXBContext jaxbContext;

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tashkent")); // Take an unlikely timezone to detect bugs related to that.
        try {
            jaxbContext = JAXBContext.newInstance("nl.vpro.domain.media");
        } catch(JAXBException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Marshaller marshaller;

    static {
        try {
            marshaller = jaxbContext.createMarshaller();
        } catch(JAXBException e) {
            log.error("Unable to create marshaller", e);
        }
    }

    public static Validator schemaValidator;

    static {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema;
        try {
            schema = factory.newSchema(new Source[]{
                new StreamSource(MediaObjectXmlSchemaTest.class.getResourceAsStream("/nl/vpro/domain/media/w3/xml.xsd")),
                new StreamSource(MediaObjectXmlSchemaTest.class.getResourceAsStream("/nl/vpro/domain/media/vproShared.xsd")),
                new StreamSource(MediaObjectXmlSchemaTest.class.getResourceAsStream("/nl/vpro/domain/media/vproMedia.xsd"))}
            );
            schemaValidator = schema.newValidator();
        } catch(SAXException e) {
            log.error("Unable to create schemaValidator", e);
        }

        ClassificationServiceLocator.setInstance(MediaClassificationService.getInstance());
    }

    @BeforeEach
    public void init() {
        Locale.setDefault(Locales.DUTCH);
    }

    @Test
    public void testMid() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program mid=\"MID_000001\" embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().mid("MID_000001").build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.hasDifferences(), diff.toString() + " " + actual);
    }

    @Test
    public void testAvailableSubtitles() {
        String expected = """
            <program embeddable="true" hasSubtitles="true" mid="MID_000001" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                <availableSubtitles language="nl" type="CAPTION" />
                <availableSubtitles language="nl" type="TRANSLATION" />
                <credits/>
                <locations/>
                <images/>
                <scheduleEvents/>
                <segments/>
            </program>""";

        Program program = program().lean().mid("MID_000001").build();
    	program.getAvailableSubtitles().add(new AvailableSubtitles(Locales.DUTCH,
            SubtitlesType.CAPTION));
    	program.getAvailableSubtitles().add(new AvailableSubtitles(Locales.DUTCH,
            SubtitlesType.TRANSLATION));

        Program rounded = JAXBTestUtil.roundTripAndSimilar(program, expected);
        assertThat(rounded.getAvailableSubtitles()).hasSize(2);
    }

    @Test
    public void testMidSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withMid().build()));
    }

    @Test
    public void testHasSubtitles() {
        String expected = """
            <program embeddable="true" hasSubtitles="true" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                <availableSubtitles language="nl" type="CAPTION" />
                <credits/>
                <locations/>
                <images/>
                <scheduleEvents/>
                <segments/>
            </program>
            """;

        Program program = program().lean()
            .withSubtitles().build();
        Program rounded = JAXBTestUtil.roundTripAndSimilar(program, expected);
        assertThat(rounded.hasSubtitles()).isTrue();
    }

    @Test
    public void testHasSubtitlesSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withSubtitles().build()));
    }

    @Test
    public void testDatesCreatedAndModified() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" lastModified=\"1970-01-01T03:00:00+01:00\" creationDate=\"1970-01-01T01:00:00+01:00\" sortDate=\"1970-01-01T01:00:00+01:00\"  xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().creationInstant(Instant.EPOCH).lastModified(Instant.ofEpochMilli(2 * 60 * 60 * 1000)).build();
        String actual = toXml(program);
        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.hasDifferences(), diff.toString() + " " + actual);
    }

    @Test
    public void testCreatedAndModifiedBy() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><credits/><locations/><images /><scheduleEvents/><segments/></program>";

        Program program = program().lean().withCreatedBy().withLastModifiedBy().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.hasDifferences(), diff.toString() + " " + actual);
    }

    @Test
    public void testCreatedAndModifiedBySchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withCreatedBy().withLastModifiedBy().build()));
    }

    @Test
    public void testPublishStartStop() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" publishStop=\"1970-01-01T03:00:00+01:00\" publishStart=\"1970-01-01T01:00:00+01:00\" sortDate=\"1970-01-01T01:00:00+01:00\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().publishStart(Instant.EPOCH).publishStop(Instant.ofEpochMilli(2 * 60 * 60 * 1000)).build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse( diff.hasDifferences(), diff.toString() + " " + actual);
    }

    @Test
    public void testPublishStartStopSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withPublishStart().withPublishStop().build()));
    }

    @Test
    public void testCrids() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><crid>crid://bds.tv/9876</crid><crid>crid://tmp.fragment.mmbase.vpro.nl/1234</crid><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withCrids().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.hasDifferences(), diff.toString() + " " + actual);
    }

    @Test
    public void testCridsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withCrids().build()));
    }

    @Test
    public void testBroadcasters() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\"  xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><broadcaster id=\"BNN\">BNN</broadcaster><broadcaster id=\"AVRO\">AVRO</broadcaster><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withBroadcasters().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.hasDifferences(), diff.toString() + " " + actual);
    }

    @Test
    public void testExclusives() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><exclusive portalId=\"STERREN24\"/><exclusive portalId=\"3VOOR12_GRONINGEN\" stop=\"1970-01-01T01:01:40+01:00\" start=\"1970-01-01T01:00:00+01:00\"/><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withPortalRestrictions().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.hasDifferences(), diff.toString() + " " + actual);
    }

    @Test
    public void testExclusivesSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withPortalRestrictions().build()));
    }

    @Test
    public void testRegions() {

        JAXBTestUtil.roundTripAndSimilar(program().lean().withGeoRestrictions().build(),
            """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <program embeddable="true" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                    <region regionId="NL" platform="INTERNETVOD"/>
                    <region regionId="BENELUX" platform="INTERNETVOD" start="1970-01-01T01:00:00+01:00" stop="1970-01-01T01:01:40+01:00"/>
                    <region regionId="NL" platform="TVVOD" start="1970-01-01T01:00:00+01:00" stop="1970-01-01T01:01:40+01:00"/>
                    <credits/>
                    <locations/>
                    <images/>
                    <scheduleEvents/>
                    <segments/>
                </program>
                """);
    }

    @Test
    public void testRegionsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withGeoRestrictions().build()));
    }

    @Test
    public void testDuration() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"><duration>P0DT2H0M0.000S</duration><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withDuration().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.hasDifferences(), diff.toString() + " " + actual);
    }

    @Test
    public void testDurationSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withDuration().build()));
    }

    @Test
    public void testPredictions() throws Exception {
        String expected = """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <program embeddable="true"  xmlns="urn:vpro:media:2009">
           <credits/>
           <prediction state="REVOKED">INTERNETVOD</prediction>
           <locations/>
           <images/>
           <scheduleEvents/>
           <segments/>
        </program>
        """;

        Program program = program().lean().build();

        Prediction prediction = new Prediction(Platform.INTERNETVOD);
        prediction.setState(Prediction.State.REVOKED);
        prediction.setIssueDate(Instant.EPOCH);

        Prediction unavailable = new Prediction(Platform.TVVOD);
        unavailable.setState(Prediction.State.REVOKED);
        unavailable.setIssueDate(Instant.EPOCH);
        unavailable.setPlannedAvailability(false);

        program.getPredictions().add(prediction);
        program.getPredictions().add(unavailable);

        String actual = toXml(program);

        assertThatXml(actual).isSimilarTo(expected);
    }

    @Test
    public void testPredictionsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withPredictions().build()));
    }

    @Test
    public void testTitles() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\"  xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><title type=\"MAIN\" owner=\"BROADCASTER\">Main title</title><title type=\"MAIN\" owner=\"MIS\">Main title MIS</title><title type=\"SHORT\" owner=\"BROADCASTER\">Short title</title><title type=\"SUB\" owner=\"MIS\">Episode title MIS</title><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withTitles().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.hasDifferences(), diff.toString() + " " + actual);
    }

    @Test
    public void testDescriptions() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><description type=\"MAIN\" owner=\"BROADCASTER\">Main description</description><description type=\"MAIN\" owner=\"MIS\">Main description MIS</description><description type=\"SHORT\" owner=\"BROADCASTER\">Short description</description><description type=\"EPISODE\" owner=\"MIS\">Episode description MIS</description><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withDescriptions().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.hasDifferences(), diff.toString() + " " + actual);
    }

    @Test
    public void testDescriptionsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withDescriptions().build()));
    }

    @Test
    public void testGenres() {
        Program program = program().withGenres().withFixedDates().build();

        Program result = JAXBTestUtil.roundTripAndSimilar(program, """
            <program embeddable="true" sortDate="2015-03-06T00:00:00+01:00" workflow="FOR PUBLICATION" creationDate="2015-03-06T00:00:00+01:00" lastModified="2015-03-06T01:00:00+01:00" publishDate="2015-03-06T02:00:00+01:00" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                <genre id="3.0.1.7.21">
                    <term>Informatief</term>
                    <term>Nieuws/actualiteiten</term>
                </genre>
                <genre id="3.0.1.8.25">
                    <term>Documentaire</term>
                    <term>Natuur</term>
                </genre>
                <credits/>
                <locations/>
                <images/>
                <scheduleEvents/>
                <segments/>
            </program>""");

        assertThat(result.getGenres()).hasSize(2);
    }

    @Test
    public void testGenresSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withGenres().build()));
    }

    @Test
    public void testAgeRating() {
        Program program = program().withAgeRating().build();

        Program result = JAXBTestUtil.roundTripContains(program, "<ageRating xmlns='urn:vpro:media:2009'>12</ageRating>");

        assertThat(result.getAgeRating()).isNotNull();
    }

    @Test
    public void testAgeRatingSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withAgeRating().build()));
    }

    @Test
    public void testContentRating() {
        Program program = program().withContentRating().build();

        Program result = JAXBTestUtil.roundTripContains(program, "<contentRating>ANGST</contentRating>",
            "<contentRating>DRUGS_EN_ALCOHOL</contentRating>");

        assertThat(result.getContentRatings()).hasSize(2);
    }

    @Test
    public void testContentRatingSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withContentRating().build()));
    }

    @Test
    public void testTags() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\"  xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><tag>tag1</tag><tag>tag2</tag><tag>tag3</tag><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withTags().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.hasDifferences(), diff.toString() + " " + actual);
    }

    @Test
    public void testTagsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withTags().build()));
    }

    @Test
    public void testPortals() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><portal id=\"3VOOR12_GRONINGEN\">3voor12 Groningen</portal><portal id=\"STERREN24\">Sterren24</portal><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withPortals().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.hasDifferences(), diff.toString() + " " + actual);
    }

    @Test
    public void testPortalsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withPortals().build()));
    }

    @Test
    public void testMemberOfAndDescendantOfGraph() {
        AtomicLong id = new AtomicLong(100L);
        String expected =
            """
                <program xmlns="urn:vpro:media:2009" embeddable="true" xmlns:shared="urn:vpro:shared:2009">
                    <credits/>
                    <descendantOf urnRef="urn:vpro:media:group:100" midRef="AVRO_5555555" type="SERIES"/>
                    <descendantOf urnRef="urn:vpro:media:group:200" midRef="AVRO_7777777" type="SEASON"/>
                    <descendantOf urnRef="urn:vpro:media:segment:301" midRef="VPROWON_104" type="SEGMENT"/>
                    <memberOf added="1970-01-01T01:00:00+01:00" highlighted="false" midRef="AVRO_7777777" index="1" type="SEASON" urnRef="urn:vpro:media:group:200">
                        <memberOf midRef="AVRO_5555555" type="SERIES" index="1"/>
                    </memberOf>
                    <memberOf highlighted="false" midRef="VPROWON_104" index="2" type="SEGMENT" urnRef="urn:vpro:media:segment:301">
                        <segmentOf midRef="VPROWON_103" type="CLIP">
                            <memberOf midRef="AVRO_5555555" type="SERIES" index="10"/>
                        </segmentOf>
                    </memberOf>
                    <memberOf highlighted="false" midRef="VPROWON_104" index="3" type="SEGMENT" urnRef="urn:vpro:media:segment:301">
                        <segmentOf midRef="VPROWON_103" type="CLIP">
                            <memberOf midRef="AVRO_5555555" type="SERIES" index="10"/>
                        </segmentOf>
                    </memberOf>
                    <locations/>
                    <images/>
                    <scheduleEvents/>
                    <segments/>
                </program>""";

        Program program = program().lean()
                .withMemberOf(id)
                .build();

        List<MemberRef> refsAsList = new ArrayList<>(program.getMemberOf()); // make accessible by index

        refsAsList.get(0).getGroup().setMid(null);
        refsAsList.get(0).getGroup().setMid("AVRO_7777777");
        refsAsList.get(0).getGroup().getMemberOf().first().getGroup().setMid(null);
        refsAsList.get(0).getGroup().getMemberOf().first().getGroup().setMid("AVRO_5555555");
        refsAsList.get(0).setAdded(Instant.EPOCH);


        assertThat(refsAsList.get(0).getMemberOf().toString()).isEqualTo("[SERIES:AVRO_5555555:AVRO_7777777]"); //

        assertThat(refsAsList.get(1).getSegmentOf().toString()).isEqualTo("CLIP:VPROWON_103:VPROWON_104"); //
        /* Set MID to null first, then set it to the required MID; otherwise an IllegalArgumentException will be thrown setting the MID to another value */


        assertThatXml(program).isSimilarTo(expected);

    }

    @Test
    public void testEpisodeOfAndDescendantOfGraph() {

        AtomicLong id = new AtomicLong(100);
        String expected = """
            <?xml version="1.0" encoding="UTF-8"?><program xmlns="urn:vpro:media:2009" type="BROADCAST" embeddable="true" urn="urn:vpro:media:program:100" xmlns:shared="urn:vpro:shared:2009">
                <credits/>
                <descendantOf urnRef="urn:vpro:media:group:101" midRef="AVRO_5555555" type="SERIES"/>
                <descendantOf urnRef="urn:vpro:media:group:102" midRef="AVRO_7777777" type="SEASON"/>
                <descendantOf midRef="VPROWON_106" type="SEGMENT"/>
                <locations/>
                <images/>
                <scheduleEvents/>
                <episodeOf added="1970-01-01T01:00:00+01:00" highlighted="false" midRef="AVRO_7777777" index="1" type="SEASON" urnRef="urn:vpro:media:group:102">
                    <memberOf midRef="AVRO_5555555" type="SERIES" index="1"/>
                    <memberOf midRef="VPROWON_106" type="SEGMENT" index="2">
                        <segmentOf midRef="VPROWON_105" type="CLIP">
                            <memberOf midRef="AVRO_5555555" type="SERIES" index="10"/>
                        </segmentOf>
                    </memberOf>
                </episodeOf>
                <segments/>
            </program>
            """;

        Program program = program().id(id.getAndIncrement()).lean()
                .type(ProgramType.BROADCAST)
                .withEpisodeOf(id.getAndIncrement(), id.getAndIncrement(), id)
                .build();

        program.getEpisodeOf().first().setAdded(Instant.EPOCH);
        /* Set MID to null first, then set it to the required MID; otherwise an IllegalArgumentException will be thrown setting the MID to another value */
        program.getEpisodeOf().first().getGroup().setMid(null);
        program.getEpisodeOf().first().getGroup().setMid("AVRO_7777777");
        program.getEpisodeOf().first().getGroup().getMemberOf().first().getGroup().setMid(null);
        program.getEpisodeOf().first().getGroup().getMemberOf().first().getGroup().setMid("AVRO_5555555");

        assertThatXml(program).isSimilarTo(expected);
    }

    @Test
    public void testRelations() {
        String expected = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <program embeddable="true" sortDate="1970-01-01T01:00:00+01:00" creationDate="1970-01-01T01:00:00+01:00" urn="urn:vpro:media:program:100" workflow="PUBLISHED" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                <credits/>
                <locations/>
                <relation broadcaster="AVRO" type="THESAURUS" urn="urn:vpro:media:relation:2">synoniem</relation>
                <relation broadcaster="EO" type="KOOR" urn="urn:vpro:media:relation:4">Ulfts Mannenkoor</relation>
                <relation broadcaster="VPRO" type="ARTIST" urn="urn:vpro:media:relation:3">Marco Borsato</relation>
                <relation uriRef="http://www.bluenote.com/" broadcaster="VPRO" type="LABEL" urn="urn:vpro:media:relation:1">Blue Note</relation>
            \s
                <images/>
                <scheduleEvents/>
                <segments/>
            </program>""";

        Program program = program().id(100L).lean().creationDate(Instant.EPOCH).workflow(Workflow.PUBLISHED).withRelations().build();


        JAXBTestUtil.roundTripAndSimilar(program, expected);
    }


    @Test
    public void testRelationsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withRelations().build()));
    }

    @Test
    public void testScheduleEvents() throws Exception {

        Program program = program().id(100L).lean().withScheduleEvents().build();
        String actual = toXml(program);

        assertThatXml(actual).isSimilarTo("""
            <?xml version="1.0" encoding="UTF-8"?>
            <program embeddable="true" sortDate="1970-01-01T01:00:00.100+01:00"
                urn="urn:vpro:media:program:100" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                <credits/>
                <locations/>
                <images/>
                <scheduleEvents>
                    <scheduleEvent channel="NED3" urnRef="urn:vpro:media:program:100">
                        <title owner="BROADCASTER" type="MAIN">Main ScheduleEvent Title</title>
                        <description owner="BROADCASTER" type="MAIN">Main ScheduleEvent Description</description>
                        <textSubtitles>Teletekst ondertitels</textSubtitles>
                        <textPage>888</textPage>
                        <guideDay>1969-12-31+01:00</guideDay>
                        <start>1970-01-01T01:00:00.100+01:00</start>
                        <duration>P0DT0H0M0.200S</duration>
                        <primaryLifestyle>Praktische Familiemensen</primaryLifestyle>
                        <secondaryLifestyle>Zorgzame Duizendpoten</secondaryLifestyle>
                    </scheduleEvent>
                    <scheduleEvent channel="NED3" net="ZAPP" urnRef="urn:vpro:media:program:100">
                        <repeat isRerun="true"/>
                        <guideDay>1970-01-03+01:00</guideDay>
                        <start>1970-01-04T01:00:00.300+01:00</start>
                        <duration>P0DT0H0M0.050S</duration>
                    </scheduleEvent>
                    <scheduleEvent channel="HOLL" urnRef="urn:vpro:media:program:100">
                        <repeat isRerun="true"/>
                        <guideDay>1970-01-08+01:00</guideDay>
                        <start>1970-01-09T01:00:00.350+01:00</start>
                        <duration>P0DT0H0M0.250S</duration>
                    </scheduleEvent>
                    <scheduleEvent channel="CONS" urnRef="urn:vpro:media:program:100">
                        <repeat isRerun="true"/>
                        <guideDay>1970-01-10+01:00</guideDay>
                        <start>1970-01-11T01:00:00.600+01:00</start>
                        <duration>P0DT0H0M0.200S</duration>
                    </scheduleEvent>
                </scheduleEvents>
                <segments/>
            </program>
            """);

        String withScheduleEventOnOldLocation = """
            <?xml version="1.0" encoding="UTF-8"?>
            <program embeddable="true" sortDate="1970-01-01T01:00:00.100+01:00"
                urn="urn:vpro:media:program:100" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                <credits/>
                <locations/>
                <scheduleEvents>
                    <scheduleEvent channel="NED3" urnRef="urn:vpro:media:program:100">
                        <guideDay>1969-12-31+01:00</guideDay>
                        <start>1970-01-01T01:00:00.100+01:00</start>
                        <duration>P0DT0H0M0.200S</duration>
                    </scheduleEvent>
                    <scheduleEvent channel="NED3" net="ZAPP" urnRef="urn:vpro:media:program:100">
                        <repeat isRerun="true"/>
                        <guideDay>1970-01-03+01:00</guideDay>
                        <start>1970-01-04T01:00:00.300+01:00</start>
                        <duration>P0DT0H0M0.050S</duration>
                    </scheduleEvent>
                    <scheduleEvent channel="HOLL" urnRef="urn:vpro:media:program:100">
                        <repeat isRerun="true"/>
                        <guideDay>1970-01-08+01:00</guideDay>
                        <start>1970-01-09T01:00:00.350+01:00</start>
                        <duration>P0DT0H0M0.250S</duration>
                    </scheduleEvent>
                    <scheduleEvent channel="CONS" urnRef="urn:vpro:media:program:100">
                        <repeat isRerun="true"/>
                        <guideDay>1970-01-10+01:00</guideDay>
                        <start>1970-01-11T01:00:00.600+01:00</start>
                        <duration>P0DT0H0M0.200S</duration>
                    </scheduleEvent>
                </scheduleEvents>
                <images/>
                <segments/>
            </program>""";

        Program unmarshalled = JAXB.unmarshal(new StringReader(withScheduleEventOnOldLocation), Program.class);
        assertThat(unmarshalled.getScheduleEvents()).hasSize(4);
    }

    @Test
    public void testScheduleEventsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withScheduleEvents().build()));
    }

    @Test
    public void testScheduleEventsWithNet() {
        String expected = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <program embeddable="true" sortDate="1970-01-01T01:00:00+01:00" urn="urn:vpro:media:program:100" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                <credits/>
                <locations/>
                <images/>
                <scheduleEvents>
                    <scheduleEvent channel="NED1" midRef="VPRO_123456" net="ZAPP" urnRef="urn:vpro:media:program:100">
                        <guideDay>1970-01-01+01:00</guideDay>
                        <start>1970-01-01T01:00:00+01:00</start>
                        <duration>P0DT0H1M40.000S</duration>
                        <poProgID>VPRO_123456</poProgID>
                        <primaryLifestyle>Onbezorgde Trendbewusten</primaryLifestyle>
                        <secondaryLifestyle>Zorgzame Duizendpoten</secondaryLifestyle>
                    </scheduleEvent>
                </scheduleEvents>
                <segments/>
            </program>""";

        ScheduleEvent event = new ScheduleEvent(Channel.NED1, Instant.EPOCH, java.time.Duration.ofSeconds((100)));
        event.setGuideDate(LocalDate.ofEpochDay(0));
        event.setNet(new Net("ZAPP", "Zapp"));
        event.setPoProgID("VPRO_123456");
        event.setPrimaryLifestyle(new Lifestyle("Onbezorgde Trendbewusten"));
        event.setSecondaryLifestyle(new SecondaryLifestyle("Zorgzame Duizendpoten"));

        Program program = program().lean().id(100L).scheduleEvents(event).build();

        JAXBTestUtil.roundTripAndSimilar(program, expected);

        JAXB.unmarshal(new StringReader(expected), Program.class);
    }

    @Test
    public void testScheduleEventsWithNetSchema() throws Exception {
        ScheduleEvent event = new ScheduleEvent(Channel.NED1, Instant.EPOCH,
            java.time.Duration.ofSeconds(100));
        event.setGuideDate(LocalDate.of(1970, 1, 1));
        event.setNet(new Net("ZAPP", "Zapp"));
        event.setPoProgID("VPRO_123456");

        Program program = program().constrained().scheduleEvents(event).build();

        schemaValidator.validate(new JAXBSource(marshaller, program));
    }

    @Test
    @Disabled("Used to generate an example XML document")
    public void generateExample() throws Exception {
        Segment segment = MediaTestDataBuilder
            .segment()
            .withPublishStart()
            .withPublishStop()
            .duration(java.time.Duration.ofSeconds(100))
            .start(java.time.Duration.ofSeconds(5000))
            .withImages()
            .withTitles()
            .withDescriptions()
            .build();

        MediaTestDataBuilder.ProgramTestDataBuilder testBuilder = MediaTestDataBuilder
            .program()
            .type(ProgramType.BROADCAST)
            .withPublishStart()
            .withPublishStop()
            .withCrids()
            .withBroadcasters()
            .withTitles()
            .withDescriptions()
            .withDuration()
            .withMemberOf()
            .withEmail()
            .withWebsites()
            .withLocations()
            .withScheduleEvents()
            .withRelations()
            .withImages()
            .withEpisodeOf()
            .segments(segment)
            .withSegments();

        ProgramUpdate example = ProgramUpdate.create(testBuilder.build());

        System.out.println(toXml(example));
    }

    @Test
    public void testSchedule() throws Exception {

        Schedule schedule = new Schedule(Channel.NED1, Instant.ofEpochMilli(0), Instant.ofEpochMilli(350 + 8 * 24 * 3600 * 1000));
        Program program = program().id(100L).lean().withScheduleEvents().build();
        schedule.addScheduleEventsFromMedia(program);

        String actual = toXml(schedule);

        assertThatXml(actual).isSimilarTo("""
            <?xml version="1.0" encoding="UTF-8"?>
            <schedule channel="NED1" start="1970-01-01T01:00:00+01:00"
                stop="1970-01-11T01:00:00.800+01:00" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                <scheduleEvent channel="NED3" urnRef="urn:vpro:media:program:100">
                    <title owner="BROADCASTER" type="MAIN">Main ScheduleEvent Title</title>
                    <description owner="BROADCASTER" type="MAIN">Main ScheduleEvent Description</description>
                    <textSubtitles>Teletekst ondertitels</textSubtitles>
                    <textPage>888</textPage>
                    <guideDay>1969-12-31+01:00</guideDay>
                    <start>1970-01-01T01:00:00.100+01:00</start>
                    <duration>P0DT0H0M0.200S</duration>
                    <primaryLifestyle>Praktische Familiemensen</primaryLifestyle>
                    <secondaryLifestyle>Zorgzame Duizendpoten</secondaryLifestyle>
                </scheduleEvent>
                <scheduleEvent channel="NED3" net="ZAPP" urnRef="urn:vpro:media:program:100">
                    <repeat isRerun="true"/>
                    <guideDay>1970-01-03+01:00</guideDay>
                    <start>1970-01-04T01:00:00.300+01:00</start>
                    <duration>P0DT0H0M0.050S</duration>
                </scheduleEvent>
                <scheduleEvent channel="HOLL" urnRef="urn:vpro:media:program:100">
                    <repeat isRerun="true"/>
                    <guideDay>1970-01-08+01:00</guideDay>
                    <start>1970-01-09T01:00:00.350+01:00</start>
                    <duration>P0DT0H0M0.250S</duration>
                </scheduleEvent>
                <scheduleEvent channel="CONS" urnRef="urn:vpro:media:program:100">
                    <repeat isRerun="true"/>
                    <guideDay>1970-01-10+01:00</guideDay>
                    <start>1970-01-11T01:00:00.600+01:00</start>
                    <duration>P0DT0H0M0.200S</duration>
                </scheduleEvent>
            </schedule>""");


        Schedule unmarshalled = JAXB.unmarshal(new StringReader(actual), Schedule.class);
        assertThat(unmarshalled.getNet()).isNull();
    }


    @Test
    public void testScheduleWithFilter() throws Exception {

        Schedule schedule = new Schedule(Channel.NED3, Instant.ofEpochMilli(0), Instant.ofEpochMilli(350 + 8 * 24 * 3600 * 1000));
        schedule.setFiltered(true);
        Program program = program().id(100L).lean().withScheduleEvents().build();
        schedule.addScheduleEventsFromMedia(program);

        String actual = toXml(schedule);

        assertThatXml(actual).isSimilarTo("""
            <?xml version="1.0" encoding="UTF-8"?>
            <schedule channel="NED3" start="1970-01-01T01:00:00+01:00"
                stop="1970-01-09T01:00:00.350+01:00" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                <scheduleEvent channel="NED3" urnRef="urn:vpro:media:program:100">
                    <title owner="BROADCASTER" type="MAIN">Main ScheduleEvent Title</title>
                    <description owner="BROADCASTER" type="MAIN">Main ScheduleEvent Description</description>
                    <textSubtitles>Teletekst ondertitels</textSubtitles>
                    <textPage>888</textPage>
                    <guideDay>1969-12-31+01:00</guideDay>
                    <start>1970-01-01T01:00:00.100+01:00</start>
                    <duration>P0DT0H0M0.200S</duration>
                    <primaryLifestyle>Praktische Familiemensen</primaryLifestyle>
                    <secondaryLifestyle>Zorgzame Duizendpoten</secondaryLifestyle>
                </scheduleEvent>
                <scheduleEvent channel="NED3" net="ZAPP" urnRef="urn:vpro:media:program:100">
                    <repeat isRerun="true"/>
                    <guideDay>1970-01-03+01:00</guideDay>
                    <start>1970-01-04T01:00:00.300+01:00</start>
                    <duration>P0DT0H0M0.050S</duration>
                </scheduleEvent>
            </schedule>
            """);
    }

    @Test
    public void testScheduleWithNetFilter() throws Exception {


        Schedule schedule = Schedule.builder()
            .net(new Net("ZAPP"))
            .start(Instant.EPOCH)
            .stop(Instant.EPOCH.plus(Duration.ofDays(8).plusMillis(350)))
            .filtered(true)
            .build();

        Program program = program().id(100L).lean().withScheduleEvents().build();
        schedule.addScheduleEventsFromMedia(program);

        assertThatXml(toXml(schedule)).isSimilarTo("""
            <?xml version="1.0" encoding="UTF-8"?>
            <schedule net="ZAPP" start="1970-01-01T01:00:00+01:00"
                stop="1970-01-09T01:00:00.350+01:00" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                <scheduleEvent channel="NED3" net="ZAPP" urnRef="urn:vpro:media:program:100">
                    <repeat isRerun="true"/>
                    <guideDay>1970-01-03+01:00</guideDay>
                    <start>1970-01-04T01:00:00.300+01:00</start>
                    <duration>P0DT0H0M0.050S</duration>
                </scheduleEvent>
            </schedule>""");
    }

    @Test
    public void testCountries() {
        Program program = program().withCountries().build();

        Program result = JAXBTestUtil.roundTripContains(program, "<country code=\"GB\">Verenigd Koninkrijk</country>");

        assertThat(result.getCountries()).hasSize(2);
    }


    @Test
    public void testCountriesSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withCountries().build()));
    }

    @Test
    public void testLanguages() {
        Program program = program().withLanguages().build();

        Program result = JAXBTestUtil.roundTripContains(program, "<language code=\"nl\" usage=\"DUBBED\">Nederlands</language>");

        assertThat(result.getLanguages()).hasSize(2);
    }


    @Test
    public void testLanguagesSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withLanguages().build()));
    }

    @Test
    public void testTwitter() throws JAXBException, IOException, SAXException {
        Program program = program().constrained().build();
        program.setTwitterRefs(Arrays.asList(new TwitterRef("@vpro"), new TwitterRef("#vpro")));
        StringWriter writer = new StringWriter();
        JAXB.marshal(program, writer);
        program = JAXB.unmarshal(new StringReader(writer.toString()), Program.class);
        assertThat(program.getTwitterRefs()).containsExactly(new TwitterRef("@vpro"), new TwitterRef("#vpro"));
        schemaValidator.validate(new JAXBSource(marshaller, program));
    }

    @Test
    public void testWithLocations() {
        String expected = """
            <?xml version="1.0" encoding="UTF-8"?>
            <program xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" embeddable="true" urn="urn:vpro:media:program:100">
                <credits/>
                <prediction state="REALIZED">INTERNETVOD</prediction>
                <locations>
                  <location owner="BROADCASTER" platform="INTERNETVOD" workflow="PUBLISHED" creationDate="2016-03-04T15:45:00+01:00">
                    <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v</programUrl>
                    <avAttributes>
                      <bitrate>1500</bitrate>
                      <avFileFormat>MP4</avFileFormat>
                    </avAttributes>
                    <offset>P0DT0H13M0.000S</offset>
                    <duration>P0DT0H10M0.000S</duration>
                  </location>
                  <location owner="BROADCASTER" platform="INTERNETVOD" workflow="PUBLISHED" creationDate="2016-03-04T14:45:00+01:00">
                    <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf</programUrl>
                    <avAttributes>
                      <bitrate>3000</bitrate>
                      <avFileFormat>WM</avFileFormat>
                    </avAttributes>
                  </location>
                  <location owner="BROADCASTER" platform="INTERNETVOD" workflow="PUBLISHED" creationDate="2016-03-04T13:45:00+01:00">
                    <programUrl>http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf</programUrl>
                    <avAttributes>
                      <bitrate>2000</bitrate>
                      <avFileFormat>WM</avFileFormat>
                    </avAttributes>
                    <duration>P0DT0H30M33.000S</duration>
                  </location>
                  <location owner="NEBO" platform="INTERNETVOD" workflow="PUBLISHED" creationDate="2016-03-04T12:45:00+01:00">
                    <programUrl>http://player.omroep.nl/?aflID=4393288</programUrl>
                    <avAttributes>
                      <bitrate>1000</bitrate>
                      <avFileFormat>HTML</avFileFormat>
                    </avAttributes>
                  </location>
                </locations>
                <images/>
                <scheduleEvents/>
                <segments/>
              </program>
            """;

        Program program = program().id(100L).lean().withLocations().build();

        JAXBTestUtil.roundTripAndSimilar(program, expected);
    }

    @Test
    public void testWithLocationWithUnknownOwner() {
        String example = """
            <program embeddable="true" hasSubtitles="false" urn="urn:vpro:media:program:100" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                <locations>
                    <location owner="UNKNOWN" creationDate="2016-03-04T15:45:00+01:00" workflow="PUBLISHED">
                        <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v</programUrl>
                        <avAttributes>
                            <avFileFormat>MP4</avFileFormat>
                        </avAttributes>
                        <offset>P0DT0H13M0.000S</offset>
                        <duration>P0DT0H10M0.000S</duration>
                    </location>
                </locations>
            </program>""";

        Program program = JAXBTestUtil.unmarshal(example, Program.class);
        assertThat(program.getLocations().first().getOwner()).isNull();
    }

    @Test
    public void testWithDescendantOf() {
        Program program = program().lean().withDescendantOf().build();
        JAXBTestUtil.roundTripAndSimilar(program, """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <program embeddable="true" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                <credits/>
                <descendantOf midRef="MID_123456" type="SEASON"/>
                <descendantOf urnRef="urn:vpro:media:group:2" type="SERIES"/>
                <descendantOf urnRef="urn:vpro:media:program:1" type="BROADCAST"/>
                <locations/>
                <images/>
                <scheduleEvents/>
                <segments/>
            </program>""");
    }

    @Test
    public void testWithIntentions() throws IOException, JAXBException {
        StringWriter segment = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/intention-scenarios.xml"), segment, UTF_8);
        String expected = segment.toString();
        log.info(expected);

        Intentions intentions = Intentions.builder()
                .owner(OwnerType.NPO)
                .values(Arrays.asList(IntentionType.ACTIVATING, IntentionType.INFORM_INDEPTH))
                .build();
        Program program = program().lean()
                .mid("9").avType(AVType.AUDIO)
                .type(ProgramType.BROADCAST).embeddable(true)
                .build();

        program.setSortInstant(LocalDate.of(2015, 3, 6).atStartOfDay(Schedule.ZONE_ID).toInstant());

        MediaObjectOwnableLists.addOrUpdateOwnableList(program, program.getIntentions(), intentions);

        String actual = toXml(program);

        assertThatXml(actual).isSimilarTo(segment.toString());

        intentions.setParent(null);
        Intentions intentionsWithoutParent = intentions;
        Program programExpected = JAXBTestUtil.unmarshal(expected, Program.class);
        assertThat((Object) programExpected.getIntentions().iterator().next()).isEqualTo(intentionsWithoutParent);
    }

    @Test
    public void testWithGeoLocations() throws Exception {
        StringWriter segment = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/geolocations-scenarios.xml"), segment, UTF_8);
        String expected = segment.toString();
        log.info("Expected: " + expected);

        Program program = program().lean().withGeoLocations()
                .mid("9").avType(AVType.AUDIO)
                .type(ProgramType.BROADCAST).embeddable(true)
                .build();

        program.setSortInstant(LocalDate.of(2015, 3, 6).atStartOfDay(Schedule.ZONE_ID).toInstant());

        JAXBTestUtil.roundTripAndSimilarAndEquals(program, expected);

        String actual = toXml(program);

        assertThatXml(actual).isSimilarTo(segment.toString());

        GeoLocations geoLocations = program.getGeoLocations().first();
        geoLocations.setParent(null);
        GeoLocations geoLocationsWithoutParent = geoLocations;
        Program programExpected = JAXBTestUtil.unmarshal(expected, Program.class);
        assertThat((Object) programExpected.getGeoLocations().iterator().next()).isEqualTo(geoLocationsWithoutParent);
    }

    @Test
    public void testUnmarshalWithNullIntentions() throws IOException {
        StringWriter segment = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/intention-null-scenarios.xml"), segment, UTF_8);
        String xmlInput = segment.toString();
        log.info(xmlInput);

        Program programExpected = JAXBTestUtil.unmarshal(xmlInput, Program.class);
        assertThat(programExpected.intentions).isNull();
    }

    @Test
    public void testUnmarshalWithEmptyIntentions() throws IOException {
        StringWriter segment = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/intention-empty-scenarios.xml"), segment, UTF_8);
        String xmlInput = segment.toString();
        log.info(xmlInput);

        Program programExpected = JAXBTestUtil.unmarshal(xmlInput, Program.class);
        assertThat(programExpected.getIntentions().first().getOwner()).isEqualTo(OwnerType.NPO);
    }

    /**
     * See MSE-4879
     */
    @Test
    public void expectSAXParseException() {
        String example = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <segment xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" midRef="RBX_NTR_2647822" type="SEGMENT" urnRef="urn:vpro:media:program:80684549" avType="AUDIO" embeddable="true" mid="RBX_NTR_5074546" sortDate="2016-09-10T17:30:00+02:00" workflow="PUBLISHED" creationDate="2016-09-10T19:09:11.870+02:00" lastModified="2016-09-10T19:09:11.995+02:00" publishDate="2020-07-20T12:37:55.591+02:00" urn="urn:vpro:media:segment:81654017">
                <crid>crid://item.radiobox2/374086</crid>
                <broadcaster id="NTR">NTR</broadcaster>
                <title owner="RADIOBOX" type="MAIN">Wat kunt u zoal verwachten?</title>
                <description owner="RADIOBOX" type="MAIN">Podium op Zaterdag met muzieknieuws en nieuwe CD's, o.a. laatste CD van de zusjes Baiba en Lauma Skride en van Charlotte Haesen Franse chansons. \u0005Als nieuwe crossover CD The Magical Forest, met een sfeervolle mix van Noorse folk, jazz en het vocaal Trio Mediaeval . Poëzie is er van de Groningse dichter Jean-Pierre Rawie, van hem het gedicht ‘Adieu’. Als filmtip L'Avenir van Mia Hansen-Løve. En in het Open Podium het jonge Nederlandse Gióvani Kwartet, dat hier te horen is in de oude bezetting. Doe mee met het Open Podium en stuur je opname naar: openpodium@ntr.nl.</description>
                <tag>podium</tag>
                <duration>P0DT0H3M0.000S</duration>
                <credits/>
                <descendantOf urnRef="urn:vpro:media:group:13405550" midRef="AUTO_PODIUM" type="SERIES"/>
                <descendantOf urnRef="urn:vpro:media:program:80684549" midRef="RBX_NTR_2647822" type="BROADCAST"/>
                <locations/>
                <images>
                    <shared:image owner="RADIOBOX" type="PICTURE" highlighted="false" workflow="PUBLISHED" creationDate="2016-09-10T19:09:11.511+02:00" lastModified="2016-09-10T19:09:11.873+02:00" urn="urn:vpro:media:image:81654019">
                        <shared:title>magical forest cover.jpg</shared:title>
                        <shared:imageUri>urn:vpro:image:801058</shared:imageUri>
                        <shared:height>372</shared:height>
                        <shared:width>620</shared:width>
                    </shared:image>
                </images>
                <segmentOf midRef="RBX_NTR_2647822" type="BROADCAST">
                    <episodeOf midRef="AUTO_PODIUM" type="SERIES" index="612"/>
                </segmentOf>
                <start>P0DT0H30M0.000S</start>
            </segment>""";
        assertThatThrownBy(() -> {
            Segment unmarshal = JAXB.unmarshal(new StringReader(example), Segment.class);
        }).isInstanceOf(jakarta.xml.bind.DataBindingException.class).hasMessageContaining("An invalid XML character");

    }

    @Test
    public void roundTripWithPrediction() {
        String example =
            """
                <program xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" type="BROADCAST" avType="VIDEO" embeddable="true" mid="VPWON_1199058" sortDate="2013-04-09T15:25:00+02:00" workflow="FOR PUBLICATION" creationDate="2013-03-17T06:48:59.719+01:00" lastModified="2018-02-07T11:58:43.578+01:00" publishDate="2015-01-08T17:42:19.202+01:00" urn="urn:vpro:media:program:23197206">
                  <broadcaster id="VPRO">VPRO</broadcaster>
                  <credits/>
                  <prediction state="ANNOUNCED" publishStop="2020-01-02T14:54:44+01:00">INTERNETVOD</prediction>
                  <prediction state="ANNOUNCED" publishStop="2020-01-02T14:54:44+01:00">TVVOD</prediction>
                  <locations />
                  <images/>
                  <scheduleEvents/>
                  <segments/>
                </program>
                """;
        JAXBTestUtil.roundTripAndSimilar(example, Program.class);
    }

    @Test
    public void programWithEverything() throws IOException {
        Program withEverything = MediaTestDataBuilder.program()
            .withEverything()
            .build();
        JAXBTestUtil.roundTripAndSimilar(withEverything, getClass().getResourceAsStream("/program-with-everything.xml"));
    }

    /**
     * Tests wether 'withEveryting' is indeed valid according to manually maintained XSD
     */
    @Test
    public void testUpdateSchema() throws IOException, SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(
            XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema xsdSchema = factory.newSchema(getClass().getResource("/nl/vpro/domain/media/update/vproMediaUpdate.xsd"));
        Validator xsdValidator = xsdSchema.newValidator();

        ProgramUpdate update = ProgramUpdate.create(MediaTestDataBuilder.program()
            .withEverything()
            .build());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXB.marshal(update, out);
        log.info(out.toString());

        Source streamSource = new StreamSource(new ByteArrayInputStream(out.toByteArray()));
        xsdValidator.validate(streamSource);
    }

    /**
     * Tests wether 'withEverything' is indeed valid according to manually maintained XSD
     */
    @Test
    public void testSchema() throws IOException, SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema xsdSchema = factory.newSchema(getClass().getResource("/nl/vpro/domain/media/vproMedia.xsd"));
        Validator xsdValidator = xsdSchema.newValidator();

        Program program = MediaTestDataBuilder.program().withEverything().build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXB.marshal(program, out);
        log.info(out.toString());

        Source streamSource = new StreamSource(new ByteArrayInputStream(out.toByteArray()));
        xsdValidator.validate(streamSource);
    }

    @Test

    public void MSE_5778() {
        String segmentXml = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <segment xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" midRef="RBX_NCRV_720421" type="SEGMENT" urnRef="urn:vpro:media:program:57740391" avType="AUDIO" embeddable="true" mid="RBX_NCRV_1467358" sortDate="2015-07-22T19:00:00+02:00" workflow="PUBLISHED" creationDate="2015-07-22T20:14:22.655+02:00" lastModified="2015-07-22T20:14:22.800+02:00" publishDate="2024-04-03T18:54:04.320+02:00" urn="urn:vpro:media:segment:59275771">
                <crid>crid://item.radiobox2/302512</crid>
                <broadcaster id="NCRV">NCRV</broadcaster>
                <title owner="RADIOBOX" type="MAIN">19.30 Toi toi voor Paul Komen</title>
                <description owner="RADIOBOX" type="MAIN">In de Abdijkerk in het Groningse Aduard staat vanavond de smaak van Emmy Verhey centraal. Op het programma: Messiaen, Ravel, Dvorak en Loevendie. Nata Tsvereli, Christophe Weidmann en Amparo Lacruz zijn van de partij, evenals pianist Paul Komen. Hem spreken wij vlak voor aanvang.</description>
                <tag>passaggio</tag>
                <duration>P0DT0H3M0.000S</duration>
                <credits/>
                <descendantOf urnRef="urn:vpro:media:group:33293178" midRef="POMS_S_NCRV_444205" type="SERIES"/>
                <descendantOf urnRef="urn:vpro:media:program:57740391" midRef="RBX_NCRV_720421" type="BROADCAST"/>
                <locations/>
                <images>
                    <shared:image owner="RADIOBOX" type="PICTURE" highlighted="false" workflow="PUBLISHED" creationDate="2015-07-22T20:14:22.549+02:00" lastModified="2015-07-22T20:14:22.658+02:00" urn="urn:vpro:media:image:59275773">
                        <shared:title>Paul Komen</shared:title>
                        <shared:imageUri>urn:vpro:image:633687</shared:imageUri>
                        <shared:height>262</shared:height>
                        <shared:width>230</shared:width>
                    </shared:image>
                </images>
                <segmentOf midRef="RBX_NCRV_720421" type="BROADCAST">
                    <episodeOf midRef="POMS_S_NCRV_444205" type="SERIES" index="49"/>
                </segmentOf>
                <start>P0DT0H54M27.000S</start>
            </segment>
            """;

        //[org.xml.sax.SAXParseException; lineNumber: 6; columnNumber: 273; An invalid XML character (Unicode: 0xf) was found in the element content of the document.]
        assertThatThrownBy( () -> JAXB.unmarshal(new StringReader(segmentXml), Segment.class)).isInstanceOf(DataBindingException.class);



    }

    protected String toXml(Object o) throws JAXBException {
        Writer writer = new StringWriter();
        marshaller.marshal(o, writer);
        return writer.toString();
    }
}
