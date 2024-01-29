package nl.vpro.domain.media.nebo.webonly.v1_4;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.time.Duration;
import java.util.Arrays;

import jakarta.xml.bind.JAXB;
import jakarta.xml.datatype.DatatypeConfigurationException;
import jakarta.xml.transform.Source;
import jakarta.xml.transform.stream.StreamSource;
import jakarta.xml.validation.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;

import static nl.vpro.jassert.assertions.MediaAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Michiel Meeuwissen
 */
@Log4j2
public class WebonlyTypeTest {

    public static final Validator schemaValidator;

    static {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = null;
        try {
            schema = factory.newSchema(new Source[]{new StreamSource(WebonlyTypeTest.class.getResourceAsStream("/nl/vpro/domain/media/nebo/webonly_import_1.2.xsd"))});
        } catch(SAXException e) {
            log.info(e.getMessage(), e);
        }
        schemaValidator = schema.newValidator();

    }

    @BeforeAll
    public static void init() {
        ClassificationServiceLocator.setInstance(new MediaClassificationService());
    }

    public static NeboXmlWebOnly getTestWebonly() throws DatatypeConfigurationException {
        return new NeboXmlWebOnly(getTestProgram(), getTestGroup().getMid());
    }

    protected static Program getTestProgram() {
        Program program = MediaTestDataBuilder
            .program()
            .constrained()
            .mid("WO_mijn_poprogid")
            //.poSeriesID("WO_S_mijnposeriesid")
            .withBroadcasters()
            .genres(Genre.valueOfMis(MisGenreType.ENTERTAINMENT, MisGenreType.YOUTH))
            .locations(Location.builder()
                .programUrl("http://location.nl/abcde1234.mp4")
                .owner(OwnerType.BROADCASTER)
                .avAttributes(new AVAttributes(1000000, AVFileFormat.MP4))
                .duration(Duration.ofMillis(36000L)).build())
            .segments(
                MediaTestDataBuilder
                    .segment()
                    .constrained()
                    .descriptions(new Description("Beschrijving", OwnerType.NEBO, TextualType.MAIN))
                    .build()
            )
            .build();
        program.setAgeRating(AgeRating._16);
        program.setContentRatings(Arrays.asList(ContentRating.GEWELD));
        return program;
    }
    protected static Group getTestGroup() {
        Group group = MediaTestDataBuilder.group().constrained().mid("WO_S_mijnposeriesid").build();
        return group;
    }

    /**
     * Just test whether the WebonlyType marshals without exceptions, and shows the result.
     * The XML is validated in NeboExporter (and NeboExporterTest) of the npo-publish application, so that
     * also tests it further.
     */
    @Test
    public void marshal() throws Exception {
        Program program = getTestProgram();
        NeboXmlWebOnly rootType = new NeboXmlWebOnly(program, getTestGroup().getMid());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXB.marshal(rootType, out);
        schemaValidator.validate(new StreamSource(new ByteArrayInputStream(out.toByteArray())));

    }

    @Test
    public void unmarshal() throws Exception {

        Program program = getTestProgram();

        NeboXmlWebOnly rootType = new NeboXmlWebOnly(program, getTestGroup().getMid());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXB.marshal(rootType, out);


        NeboXmlWebOnly xml = JAXB.unmarshal(new ByteArrayInputStream(out.toByteArray()), NeboXmlWebOnly.class);
        Program newProgram = xml.getProgram();
        assertEquals(program.getMid(), newProgram.getMid());
        //assertEquals(program.getPoSeriesID(), newProgram.getPoSeriesID());
        assertEquals(program.getMainTitle(), newProgram.getMainTitle());
        assertEquals(program.getSubTitle(), newProgram.getSubTitle());
        assertEquals(program.getEmail(), newProgram.getEmail());
        assertEquals(program.getAgeRating(), newProgram.getAgeRating());
        assertEquals(program.getContentRatings(), newProgram.getContentRatings());
        assertEquals(program.getMainDescription(), newProgram.getMainDescription());
        assertEquals(program.getShortDescription(), newProgram.getShortDescription());
        assertEquals(program.getWebsites(), newProgram.getWebsites());
        assertEquals(program.getGenres(), newProgram.getGenres());
        assertEquals(program.getBroadcasters(), newProgram.getBroadcasters());
        assertEquals(program.getSource(), newProgram.getSource());
        assertEquals(1, program.getLocations().size());
        assertEquals(
            program.getLocations().iterator().next(),
            newProgram.getLocations().iterator().next()
        );
    }


    /**
     * Unmarshalling an actual example
     */
    @Test
    public void unmarshalExample1() {
        String test = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nebo_xml timestamp="2012-05-15T20:58:25" type="webonly">
              <webonly id="14305325">
                <prid>WO_KRO_000010</prid>
                <srid>boerzoektvrouw_2012_extra</srid>
                <titel>Oproep Hans</titel>
                <subtitel>Oproep Hans</subtitel>
                <omschrijving_kort>De 40-jarige Hans is op zoek naar een enthousiaste vrouw met pit.</omschrijving_kort>
                <omschrijving_lang>De 40-jarige Hans is op zoek naar een enthousiaste vrouw met pit.</omschrijving_lang>
                <media>
                  <streams publicatie_startdatumtijd="2012-05-13T19:00:00" aspect_ratio="16:9">
                    <stream formaat="wmv" kwaliteit="bb">http://cgi.omroep.nl/cgi-bin/streams?/kro/boerzoektvrouw/bzv_20120513_oproephans_video_high.wmv</stream>
                    <stream formaat="wmv" kwaliteit="sb">http://cgi.omroep.nl/cgi-bin/streams?/kro/boerzoektvrouw/bzv_20120513_oproephans_video_low.wmv</stream>
                    <stream formaat="mov" kwaliteit="bb">http://content.omroep.nl/kro/video/boerzoektvrouw/bzv_20120513_oproephans_video_high.mp4</stream>
                    <stream formaat="mov" kwaliteit="sb">http://content.omroep.nl/kro/video/boerzoektvrouw/bzv_20120513_oproephans_video_low.mp4</stream>
                  </streams>
                  <icon>
                    <file>http://u.omroep.nl/n/a/2012-05/hans.jpg</file>
                    <titel>Hans oproep</titel>
                  </icon>
                </media>
                <genres>
                  <genre>Amusement</genre>
                </genres>
                <omroepen>
                  <omroep hoofdomroep="true">KRO</omroep>
                </omroepen>
              </webonly>
            </nebo_xml>""";
        NeboXmlWebOnly xml = JAXB.unmarshal(new StringReader(test), NeboXmlWebOnly.class);
        assertNotNull(xml.getWebonly());
        assertNotNull(xml.getWebonly().getMedia());
        assertNotNull(xml.getWebonly().getMedia().getStreams());
        assertNotNull(xml.getWebonly().getMedia().getStreams().getLocations());
        assertEquals(4, xml.getWebonly().getMedia().getStreams().getLocations().size());
        assertEquals(1, xml.getWebonly().getProgram().getGenres().size());
        assertEquals(4, xml.getProgram().getLocations().size());

    }

    @Test
    public void testUnmarshalWithIllegalGenre() {
        String test = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nebo_xml timestamp="2012-05-15T20:58:25" type="webonly">
              <webonly id="14305325">
                <genres>
                  <genre>ILLEGAL</genre>
                </genres>
              </webonly>
            </nebo_xml>""";
        NeboXmlWebOnly xml = JAXB.unmarshal(new StringReader(test), NeboXmlWebOnly.class);
        assertThat(xml.getProgram().getGenres()).isEmpty();

    }

    @Test
    public void unmarshalExample2() {
        String test = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nebo_xml timestamp="2012-05-15T19:58:41" type="webonly">
              <webonly id="10602764">
                <prid>WO_NCRV_013487</prid>
                <srid>WO_S_NCRV_013488</srid>
                <titel>Yoga</titel>
                <subtitel>Aflevering 8</subtitel>
                <omschrijving_kort>Myrna van Kemenade leidt iedere ochtend op Spirit 24 een aangename yoga sessie om het lichaam en de geest in een goede balans te brengen. &#13;
            Naast fysieke oefeningen en meditatiemomenten krijgt u uitgebreide informatie over de herkomst en heilzaamheid van de oefeningen. Het programma is gericht op beginners n gevorderden, zodat iedereen mee kan doen.</omschrijving_kort>
                <omschrijving_lang>Myrna van Kemenade leidt iedere ochtend op Spirit 24 een aangename yoga sessie om het lichaam en de geest in een goede balans te brengen. &#13;
            Naast fysieke oefeningen en meditatiemomenten krijgt u uitgebreide informatie over de herkomst en heilzaamheid van de oefeningen. Het programma is gericht op beginners n gevorderden, zodat iedereen mee kan doen.</omschrijving_lang>
                <media>
                  <streams publicatie_startdatumtijd="2010-01-08T11:42:00" aspect_ratio="16:9">
                    <stream formaat="wmv" kwaliteit="bb">http://cgi.omroep.nl/cgi-bin/streams?/ncrv/geloven/16X9_20090924_YOGA_AFLEVERING8AHLHAN.wmv</stream>
                    <stream formaat="wmv" kwaliteit="sb">http://cgi.omroep.nl/cgi-bin/streams?/ncrv/geloven/16X9_20090924_YOGA_AFLEVERING8AGLHAN.wmv</stream>
                  </streams>
                </media>
                <genres>
                  <genre>Religieus</genre>
                </genres>
                <omroepen>
                  <omroep hoofdomroep="true">KRO</omroep>
                </omroepen>
              </webonly>
            </nebo_xml>
            """;

        NeboXmlWebOnly xml = JAXB.unmarshal(new StringReader(test), NeboXmlWebOnly.class);
        assertEquals(2, xml.getProgram().getLocations().size());
    }

}
