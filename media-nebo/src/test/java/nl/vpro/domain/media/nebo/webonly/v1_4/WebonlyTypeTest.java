package nl.vpro.domain.media.nebo.webonly.v1_4;

import java.io.*;
import java.time.Duration;
import java.util.Arrays;

import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

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
public class WebonlyTypeTest {

    public static final Validator schemaValidator;

    static {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = null;
        try {
            schema = factory.newSchema(new Source[]{new StreamSource(factory.getClass().getResourceAsStream("/nl/vpro/domain/media/nebo/webonly_import_1.2.xsd"))});
        } catch(SAXException e) {
            e.printStackTrace(System.err);
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
            .locations(new Location("http://location.nl/abcde1234.mp4", OwnerType.BROADCASTER, new AVAttributes(1000000, AVFileFormat.MP4), Duration.ofMillis(36000L)))
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
        String test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<nebo_xml timestamp=\"2012-05-15T20:58:25\" type=\"webonly\">\n" +
            "  <webonly id=\"14305325\">\n" +
            "    <prid>WO_KRO_000010</prid>\n" +
            "    <srid>boerzoektvrouw_2012_extra</srid>\n" +
            "    <titel>Oproep Hans</titel>\n" +
            "    <subtitel>Oproep Hans</subtitel>\n" +
            "    <omschrijving_kort>De 40-jarige Hans is op zoek naar een enthousiaste vrouw met pit.</omschrijving_kort>\n" +
            "    <omschrijving_lang>De 40-jarige Hans is op zoek naar een enthousiaste vrouw met pit.</omschrijving_lang>\n" +

            "    <media>\n" +
            "      <streams publicatie_startdatumtijd=\"2012-05-13T19:00:00\" aspect_ratio=\"16:9\">\n" +
            "        <stream formaat=\"wmv\" kwaliteit=\"bb\">http://cgi.omroep.nl/cgi-bin/streams?/kro/boerzoektvrouw/bzv_20120513_oproephans_video_high.wmv</stream>\n" +
            "        <stream formaat=\"wmv\" kwaliteit=\"sb\">http://cgi.omroep.nl/cgi-bin/streams?/kro/boerzoektvrouw/bzv_20120513_oproephans_video_low.wmv</stream>\n" +
            "        <stream formaat=\"mov\" kwaliteit=\"bb\">http://content.omroep.nl/kro/video/boerzoektvrouw/bzv_20120513_oproephans_video_high.mp4</stream>\n" +
            "        <stream formaat=\"mov\" kwaliteit=\"sb\">http://content.omroep.nl/kro/video/boerzoektvrouw/bzv_20120513_oproephans_video_low.mp4</stream>\n" +
            "      </streams>\n" +
            "      <icon>\n" +
            "        <file>http://u.omroep.nl/n/a/2012-05/hans.jpg</file>\n" +
            "        <titel>Hans oproep</titel>\n" +
            "      </icon>\n" +
            "    </media>\n" +

            "    <genres>\n" +
            "      <genre>Amusement</genre>\n" +
            "    </genres>\n" +
            "    <omroepen>\n" +
            "      <omroep hoofdomroep=\"true\">KRO</omroep>\n" +
            "    </omroepen>\n" +
            "  </webonly>\n" +
            "</nebo_xml>";
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
        String test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<nebo_xml timestamp=\"2012-05-15T20:58:25\" type=\"webonly\">\n" +
            "  <webonly id=\"14305325\">\n" +
            "    <genres>\n" +
            "      <genre>ILLEGAL</genre>\n" +
            "    </genres>\n" +
            "  </webonly>\n" +
            "</nebo_xml>";
        NeboXmlWebOnly xml = JAXB.unmarshal(new StringReader(test), NeboXmlWebOnly.class);
        assertThat(xml.getProgram().getGenres()).isEmpty();

    }

    @Test
    public void unmarshalExample2() {
        String test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<nebo_xml timestamp=\"2012-05-15T19:58:41\" type=\"webonly\">\n" +
            "  <webonly id=\"10602764\">\n" +
            "    <prid>WO_NCRV_013487</prid>\n" +
            "    <srid>WO_S_NCRV_013488</srid>\n" +
            "    <titel>Yoga</titel>\n" +
            "    <subtitel>Aflevering 8</subtitel>\n" +
            "    <omschrijving_kort>Myrna van Kemenade leidt iedere ochtend op Spirit 24 een aangename yoga sessie om het lichaam en de geest in een goede balans te brengen. &#13;\n" +
            "Naast fysieke oefeningen en meditatiemomenten krijgt u uitgebreide informatie over de herkomst en heilzaamheid van de oefeningen. Het programma is gericht op beginners n gevorderden, zodat iedereen mee kan doen.</omschrijving_kort>\n" +
            "    <omschrijving_lang>Myrna van Kemenade leidt iedere ochtend op Spirit 24 een aangename yoga sessie om het lichaam en de geest in een goede balans te brengen. &#13;\n" +
            "Naast fysieke oefeningen en meditatiemomenten krijgt u uitgebreide informatie over de herkomst en heilzaamheid van de oefeningen. Het programma is gericht op beginners n gevorderden, zodat iedereen mee kan doen.</omschrijving_lang>\n" +
            "    <media>\n" +
            "      <streams publicatie_startdatumtijd=\"2010-01-08T11:42:00\" aspect_ratio=\"16:9\">\n" +
            "        <stream formaat=\"wmv\" kwaliteit=\"bb\">http://cgi.omroep.nl/cgi-bin/streams?/ncrv/geloven/16X9_20090924_YOGA_AFLEVERING8AHLHAN.wmv</stream>\n" +
            "        <stream formaat=\"wmv\" kwaliteit=\"sb\">http://cgi.omroep.nl/cgi-bin/streams?/ncrv/geloven/16X9_20090924_YOGA_AFLEVERING8AGLHAN.wmv</stream>\n" +
            "      </streams>\n" +
            "    </media>\n" +
            "    <genres>\n" +
            "      <genre>Religieus</genre>\n" +
            "    </genres>\n" +
            "    <omroepen>\n" +
            "      <omroep hoofdomroep=\"true\">KRO</omroep>\n" +
            "    </omroepen>\n" +
            "  </webonly>\n" +
            "</nebo_xml>\n";

        NeboXmlWebOnly xml = JAXB.unmarshal(new StringReader(test), NeboXmlWebOnly.class);
        assertEquals(2, xml.getProgram().getLocations().size());
    }

}
