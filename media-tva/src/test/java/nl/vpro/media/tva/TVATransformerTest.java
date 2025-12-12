package nl.vpro.media.tva;

import lombok.extern.log4j.Log4j2;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.StringValue;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.xml.bind.JAXB;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.test.appender.ListAppender;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import nl.vpro.domain.TextualObjects;
import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.media.update.Validation;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.i18n.Locales;
import nl.vpro.media.tva.saxon.extension.*;
import nl.vpro.validation.ValidationLevel;

import static nl.vpro.jassert.assertions.MediaAssertions.assertThat;
import static nl.vpro.media.tva.Constants.*;
import static nl.vpro.test.util.jaxb.JAXBTestUtil.similar;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.meeuw.i18n.languages.ISO_639_1_Code.cs;

/**
 * @author Michiel Meeuwissen
 * @since 4.1
 */
@SuppressWarnings({"OptionalGetWithoutIsPresent", "DataFlowIssue"})
@Log4j2
@Isolated("Because of the logger")
@Execution(ExecutionMode.SAME_THREAD) // genreFunction is sometimes configuration for test
public class TVATransformerTest {
    static final EpgGenreFunction genreFunction = new EpgGenreFunction();


    static ListAppender appender = new ListAppender("List");

    static {
        var loggerContext = LoggerContext.getContext(false);
        appender.start();
        loggerContext.getConfiguration().addLoggerAppender(loggerContext.getRootLogger(), appender);
        ClassificationServiceLocator.setInstance(new MediaClassificationService());
    }

    @BeforeEach
    @AfterEach
    public void init() {
        appender.clear();
        genreFunction.setNotFound(NotFound.FATAL);
        genreFunction.setMatchOnValuePrefix("");
        genreFunction.setIgnore(Set.of());
    }


    @Test
    public void transform() throws TransformerException, ParserConfigurationException, SAXException, IOException {
        genreFunction.setNotFound(NotFound.ASIS);
        String xml = transform("pd/pd/NED320150805P.xml");
        similar(xml,
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <mediaInformation xmlns="urn:vpro:media:2009"
                                  publicationTime="2015-08-03T17:40:17.040+02:00"
                                  version="300">
                   <programTable>
                      <program workflow="FOR REPUBLICATION"
                               mid="POW_00252645"
                               type="BROADCAST"
                               avType="VIDEO"
                               embeddable="true">
                         <crid>crid://npo/programmagegevens/840343221668</crid>
                         <broadcaster id="EO">EO</broadcaster>
                         <title type="MAIN" owner="MIS">SERIE TITEL</title>
                         <title type="SUB" owner="MIS">IK BEN EEN AFLEVERING</title>
                         <!--tva:Title[@type='parentseriestitle'] 'IK BEN EEN MOEDERSERIE' goes to series-->
                         <!--tva:Title[@type='translatedtitle'] 'ICH BIN EIN SEIZON' goes to season-->
                         <title type="ORIGINAL" owner="MIS">ICH BIN EIN PROGRAM</title>
                         <!--tva:Synopsis[@length = 'long'] 'Dit is de seizoensbeschrijving' goes to season-->
                         <description type="SHORT" owner="MIS">IK BEN DE KORTE BESCHRIJVING</description>
                         <description type="MAIN" owner="MIS">IK BEN DE LANGE BESCHRIJVING</description>
                         <!--tva:Synopsis[@length = 'long' and @type = 'parentSeriesSynopsis'] 'Dit is de Seriesbeschrijving' goes to series-->
                         <description owner="MIS" type="KICKER">Dit is de kicker</description>
                         <genre id="3.0.1.1.11"/>
                         <country code="GB"/>
                         <language code="cs"/>
                         <releaseYear>2009</releaseYear>
                         <duration>PT00H12M55S</duration>
                         <ageRating>ALL</ageRating>
                         <contentRating>DRUGS_EN_ALCOHOL</contentRating>
                         <contentRating>GROF_TAALGEBRUIK</contentRating>
                         <descendantOf type="SEASON" midRef="POW_00252644"/>
                         <descendantOf type="SERIES" midRef="POW_00818820"/>
                         <episodeOf type="SEASON" midRef="POW_00252644" index="1"/>
                      </program>
                      <program workflow="FOR REPUBLICATION"
                               mid="POW_00252645_1"
                               type="BROADCAST"
                               avType="VIDEO"
                               embeddable="true">
                         <crid>crid://npo/programmagegevens/840343221669</crid>
                         <broadcaster id="EO">EO</broadcaster>
                         <title type="MAIN" owner="MIS">SERIE TITEL</title>
                         <title type="SUB" owner="MIS">IK BEN EEN AFLEVERING</title>
                         <!--tva:Title[@type='parentseriestitle'] 'IK BEN EEN MOEDERSERIE' goes to series-->
                         <!--tva:Synopsis[@length = 'long'] 'Dit is de seizoensbeschrijving' goes to season-->
                         <description type="SHORT" owner="MIS">IK BEN DE KORTE BESCHRIJVING</description>
                         <description type="MAIN" owner="MIS">IK BEN DE LANGE BESCHRIJVING</description>
                         <!--tva:Synopsis[@length = 'long' and @type = 'parentSeriesSynopsis'] 'Dit is de Seriesbeschrijving' goes to series-->
                         <description owner="MIS" type="KICKER">Dit is de kicker</description>
                         <genre id="3.0.1.1.11"/>
                         <country code="GB"/>
                         <language code="en"/>
                         <releaseYear>2009</releaseYear>
                         <duration>PT00H12M55S</duration>
                         <ageRating>9</ageRating>
                         <contentRating>DRUGS_EN_ALCOHOL</contentRating>
                         <contentRating>GROF_TAALGEBRUIK</contentRating>
                      </program>
                   </programTable>
                   <groupTable>
                      <group type="SEASON"
                             avType="VIDEO"
                             mid="POW_00252644"
                             workflow="FOR REPUBLICATION">
                         <broadcaster id="EO">EO</broadcaster>
                         <title type="MAIN" owner="MIS">SERIE TITEL</title>
                         <title type="ORIGINAL" owner="MIS">ICH BIN EIN SEIZON</title>
                         <description type="MAIN" owner="MIS">Dit is de seizoensbeschrijving</description>
                         <memberOf midRef="POW_00818820" index="3"/>
                         <poSequenceInformation>3</poSequenceInformation>
                      </group>
                      <group type="SERIES"
                             avType="VIDEO"
                             mid="POW_00818820"
                             workflow="FOR REPUBLICATION">
                         <broadcaster id="EO">EO</broadcaster>
                         <title type="MAIN" owner="MIS">IK BEN EEN MOEDERSERIE</title>
                         <description type="MAIN" owner="MIS">Dit is de Seriesbeschrijving</description>
                      </group>
                   </groupTable>
                   <schedule channel="NED3"
                             start="2015-08-15T06:30:00+02:00"
                             stop="2015-08-16T03:06:00+02:00">
                      <scheduleEvent urnRef="crid://npo/programmagegevens/840343221668"
                                     channel="NED3"
                                     net="ZAPP">
                         <repeat isRerun="true">Bla bla</repeat>
                         <start>2015-08-15T06:30:00+02:00</start>
                         <duration>PT00H12M55S</duration>
                         <poProgID>POW_00252645</poProgID>
                         <poSeriesID>POW_00252644</poSeriesID>
                      </scheduleEvent>
                      <scheduleEvent urnRef="crid://npo/programmagegevens/840343221669"
                                     channel="NED3"
                                     net="ZAPP">
                         <repeat isRerun="false">Bloe bloe</repeat>
                         <start>2015-08-15T06:30:00+02:00</start>
                         <duration>PT00H12M55S</duration>
                         <poProgID>POW_00252645_1</poProgID>
                      </scheduleEvent>
                   </schedule>
                </mediaInformation>""");
    }

    @Test
    public void transform_MSE_4907() throws TransformerException, ParserConfigurationException, SAXException, IOException {
        String xml = transform("pd/pd/NED320200220P.xml");
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        Set<String> mids = table.getGroupTable().stream().map(g -> g.getType() + ":" + g.getMid() + ":" + g.getMainTitle())
            .collect(Collectors.toCollection(TreeSet::new));
        assertThat(mids).containsExactly(
            "SEASON:20Jeugd1900:NOS Jeugdjournaal",
            "SEASON:AT_2027949:Mega Mindy",
            "SEASON:AT_2063296:Rintje",
            "SEASON:AT_2120977:Brugklas",
            "SEASON:AT_2125605:Zin in Zappelin",
            "SEASON:AT_2133014:Jill",
            "SEASON:BV_101396041:De Wereld Draait Door",
            "SEASON:BV_101396684:Rambam",
            "SEASON:BV_101396774:First Dates",
            "SEASON:KN_1709499:SpangaS",
            "SEASON:KN_1712040:Keuringsdienst van Waarde",
            "SEASON:KN_1712142:Kindertijd",
            "SEASON:KN_1712577:Vos en Haas",
            "SEASON:KN_1712769:Woezel & Pip",
            "SEASON:POW_00089119:Barbapapa",
            "SEASON:POW_00163215:Pingu",
            "SEASON:POW_00240526:Olivia",
            "SEASON:POW_00252687:Angelina Ballerina",
            "SEASON:POW_00384899:Heuvelland Ziekenhuis",
            "SEASON:POW_00435067:Raad eens hoeveel ik van je hou",
            "SEASON:POW_00435138:Joe & Jack",
            "SEASON:POW_00441125:Peter Pan",
            "SEASON:POW_00474205:De dodelijkste 60",
            "SEASON:POW_00474545:Babar en de belevenissen van Badou",
            "SEASON:POW_00751302:Tip de muis",
            "SEASON:POW_00791054:Mike de ridder",
            "SEASON:POW_00804016:Calimero",
            "SEASON:POW_00815880:De Smurfen",
            "SEASON:POW_00929913:Olly, het kleine witte busje",
            "SEASON:POW_01003140:Heidi",
            "SEASON:POW_01003234:Maya de Bij",
            "SEASON:POW_01046861:Masha's sprookjes",
            "SEASON:POW_01050841:Pieter Konijn",
            "SEASON:POW_01098927:Peperbollen",
            "SEASON:POW_02521078:Shaun het schaap",
            "SEASON:POW_03040667:Bollie & Billie",
            "SEASON:POW_03309781:Bumba bravo Babilu",
            "SEASON:POW_03317942:TwiniPop",
            "SEASON:POW_03427467:Masha en de beer",
            "SEASON:POW_03429987:Nelli en Nora",
            "SEASON:POW_03430057:Grizzy en de lemmingen",
            "SEASON:POW_03470984:Inui",
            "SEASON:POW_03498568:Nijntjes avonturen, groot en klein",
            "SEASON:POW_03600599:Ziggy en de Zootram",
            "SEASON:POW_03745328:Rita & Krokodil",
            "SEASON:POW_03806511:De tafel van K3",
            "SEASON:POW_03876980:No-No",
            "SEASON:POW_03877093:Buurman en Buurman hebben een nieuw huis",
            "SEASON:POW_04096138:Maan en ik",
            "SEASON:POW_04112692:Tekst-TV",
            "SEASON:POW_04133217:Machtige Mike",
            "SEASON:POW_04185566:Bing",
            "SEASON:POW_04429318:Lucas etc.",
            "SEASON:POW_04513082:Casper en Emma",
            "SEASON:POW_04525267:Bernard",
            "SEASON:VPWON_1283337:Het Klokhuis",
            "SEASON:VPWON_1300156:Sesamstraat 10 voor...",
            "SEASON:VPWON_1305823:Checkpoint",
            "SEASON:VPWON_1307071:Van Vader naar Moeder",
            "SEASON:VPWON_1307693:Beste Vrienden Quiz",
            "SEASON:VPWON_1308513:Gefileerd",
            "SEASON:VPWON_1312927:Sesamstraat",
            "SEASON:VPWON_1312953:Sesamstraat",
            "SERIES:AT_2035122:Brugklas",
            "SERIES:AT_2037172:Jill",
            "SERIES:AT_2048907:Mega Mindy",
            "SERIES:AT_2063294:Rintje",
            "SERIES:AT_2121097:Zin in Zappelin",
            "SERIES:BNN_101378960:First Dates",
            "SERIES:KN_1674156:Kindertijd",
            "SERIES:KN_1676932:SpangaS",
            "SERIES:KN_1678993:Keuringsdienst van Waarde",
            "SERIES:KN_1703632:Vos en Haas",
            "SERIES:KN_1712770:Woezel & Pip",
            "SERIES:NOSJeugdjournaal:NOS Jeugdjournaal",
            "SERIES:POW_00816641:Olivia",
            "SERIES:POW_00816642:Barbapapa",
            "SERIES:POW_00818800:Pingu",
            "SERIES:POW_00818820:Angelina Ballerina",
            "SERIES:POW_00822800:De Smurfen",
            "SERIES:POW_00823480:Heuvelland Ziekenhuis",
            "SERIES:POW_00824308:Buurman en Buurman",
            "SERIES:POW_00872680:Tip de muis",
            "SERIES:POW_00890542:Shaun het schaap",
            "SERIES:POW_00890543:Babar en de belevenissen van Badou",
            "SERIES:POW_00890545:Pieter Konijn",
            "SERIES:POW_00890546:Maya de bij",
            "SERIES:POW_00890560:Peter Pan",
            "SERIES:POW_00890582:Mike de ridder",
            "SERIES:POW_00890602:Joe & Jack",
            "SERIES:POW_00890603:Raad eens hoeveel ik van je hou",
            "SERIES:POW_00890621:Bumba",
            "SERIES:POW_00890623:De dodelijkste 60",
            "SERIES:POW_00892940:Calimero",
            "SERIES:POW_00980920:Olly, het kleine witte busje",
            "SERIES:POW_01003366:Heidi",
            "SERIES:POW_01047543:Masha",
            "SERIES:POW_01099374:Peperbollen",
            "SERIES:POW_03068578:Bing",
            "SERIES:POW_03164061:Bollie & Billie",
            "SERIES:POW_03382279:TwiniPop",
            "SERIES:POW_03430041:Nelli en Nora",
            "SERIES:POW_03430143:Grizzy en de lemmingen",
            "SERIES:POW_03430705:Inui",
            "SERIES:POW_03438910:Nijntje",
            "SERIES:POW_03600759:Ziggy en de Zootram",
            "SERIES:POW_03745355:Rita & Krokodil",
            "SERIES:POW_03806522:De tafel van K3",
            "SERIES:POW_03877047:NoNo",
            "SERIES:POW_04133296:Mighty Mike",
            "SERIES:POW_04425970:Lucas",
            "SERIES:POW_04439800:Maan en ik",
            "SERIES:POW_04574076:Bernard",
            "SERIES:POW_04582617:Casper en Emma",
            "SERIES:VARA_101377717:De Wereld Draait Door",
            "SERIES:VARA_101378105:Rambam",
            "SERIES:VPWON_1247335:Sesamstraat",
            "SERIES:VPWON_1247336:Sesamstraat 10 voor...",
            "SERIES:VPWON_1249361:Beste Vrienden Quiz",
            "SERIES:VPWON_1256937:Het Klokhuis",
            "SERIES:VPWON_1257901:Checkpoint",
            "SERIES:VPWON_1298270:Van Vader naar Moeder",
            "SERIES:VPWON_1308512:Gefileerd"
        );
        similar(xml, getClass().getResourceAsStream("/pd/pd/NED320200220P.mediatable.xml"));
    }

    @Test
    public void unmarshalAfterUnmarshal() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        MediaTable table = JAXB.unmarshal(new StringReader(transform("pd/pd/NED320150805P.xml")), MediaTable.class);
        validate(table);

        Program program = table.getProgramTable().getFirst();


        assertThat(program.getMainTitle()).isEqualTo("SERIE TITEL");
        assertThat(program.getMainDescription()).isEqualTo("IK BEN DE LANGE BESCHRIJVING");
        assertThat(program.getLanguages()).containsExactly(UsedLanguage.of(cs));
        assertThat(TextualObjects.getDescription(program, TextualType.KICKER)).isEqualTo("Dit is de kicker");
        assertThat(program.getContentRatings()).containsExactly(ContentRating.DRUGS_EN_ALCOHOL, ContentRating.GROF_TAALGEBRUIK);
        assertThat(program.getAgeRating()).isEqualTo(AgeRating.ALL);


        Program program2 = table.getProgramTable().get(1);
        assertThat(program2.getAgeRating()).isEqualTo(AgeRating._9);


        assertThat(table.getGroupTable()).hasSize(2);

        assertThat(table.getGroupTable().getFirst().getType()).isEqualTo(GroupType.SEASON);
        assertThat(table.getGroupTable().getFirst()).hasAVType(AVType.VIDEO);
        assertThat(table.getGroupTable().getFirst().getBroadcasters().getFirst().getId()).isEqualTo("EO");
        assertThat(table.getGroupTable().getFirst().getMainDescription()).isEqualTo("Dit is de seizoensbeschrijving");
        assertThat(table.getGroupTable().get(0).getMainTitle()).isEqualTo("SERIE TITEL");
        assertThat(table.getGroupTable().get(0).getMemberOf().first().getNumber()).isEqualTo(3);


        assertThat(table.getGroupTable().get(1).getType()).isEqualTo(GroupType.SERIES);
        assertThat(table.getGroupTable().get(1)).isVideo();
        assertThat(table.getGroupTable().get(1).getBroadcasters().getFirst().getId()).isEqualTo("EO");
        assertThat(table.getGroupTable().get(1).getMainDescription()).isEqualTo("Dit is de Seriesbeschrijving");
        assertThat(table.getGroupTable().get(1).getMainTitle()).isEqualTo("IK BEN EEN MOEDERSERIE");
    }

    @Test
    public void regional() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/OZEE20150914P.xml");
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        validate(table);
    }

    @Test
    public void oddDate () throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/NED220150915P.xml");

        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        validate(table);
    }

    @Test
    public void moreSeasons() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/NED220150919P.xml");
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        validate(table);

        Set<String> mids = table.getGroupTable().stream().map(g -> g.getType() + ":" + g.getMid() + ":" + g.getMainTitle())
            .collect(Collectors.toCollection(TreeSet::new));

        assertThat(mids).containsExactly(
            "SEASON:15Jnl1300n2za1:NOS Journaal",
            "SEASON:15Jnl1700:NOS Journaal",
            "SEASON:15ROFza:Fryslân DOK:",
            "SEASON:AT_2033152:Krabbé zoekt Van Gogh",
            "SEASON:KN_1665953:Schepper & co",
            "SEASON:POW_00974481:Tekst-TV",
            "SEASON:POW_00992530:OHM Magazine",
            "SEASON:POW_01006071:Detectives: The Fall",
            "SEASON:POW_01120182:Natuur op 2: Galapagos",
            "SEASON:POW_02942555:Tijd voor Meldpunt! & Hallo Nederland",
            "SEASON:RKK_1673029:Bodar op zoek naar Benedictus",
            "SEASON:VARA_101372855:Vroege Vogels",
            "SEASON:VARA_101372976:2 voor 12",
            "SEASON:VPWON_1233644:Grensland",
            "SEASON:VPWON_1235891:Dit is de dag",
            "SEASON:VPWON_1236065:Blauw Bloed",
            "SEASON:VPWON_1236541:De Nachtzoen",
            "SEASON:VPWON_1236775:MO Actueel",
            "SEASON:VPWON_1241384:Nieuwsuur",
            "SEASON:VPWON_1241874:Landinwaarts",
            "SEASON:VPWON_1241915:Andere Tijden",
            "SEASON:VPWON_1242702:De Verandering",
            "SEASON:VPWON_1242892:Nederland Zingt",
            "SEASON:VPWON_1246587:Metterdaad",
            "SEASON:VPWON_1248820:Moslimpredikant online",
            "SERIES:AT_2033151:Krabbé zoekt Van Gogh",
            "SERIES:POW_01006080:The Fall",
            "SERIES:VPWON_1246433:Grensland"
        );
        assertThat(table.getGroupTable()).hasSize(30);

        assertThat(table.getGroupTable().stream().filter(g -> g.getType() == GroupType.SEASON).collect(Collectors.toList())).hasSize(27);
        assertThat(table.getGroupTable().stream().filter(g -> g.getType() == GroupType.SERIES).collect(Collectors.toList())).hasSize(3);

    }
    @Test
    public void email() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/NED220150919P.xml");
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        {
            // MSE-4572
            Optional<MediaObject> byCrid = table.findByCrid("crid://npo/programmagegevens/1000741891668");
            assertThat(byCrid.get().getEmail()).isEmpty();
        }
        {
            //MSE-5137
            Optional<MediaObject> byCrid = table.findByCrid("crid://npo/programmagegevens/1000741915668");
            assertThat(byCrid.get().getEmail()).isEmpty();
        }
        {
            Optional<MediaObject> byCrid = table.findByCrid("crid://npo/programmagegevens/1000741903668");
            assertThat(byCrid.get().getEmail().stream().map(Email::get).toList()).contains("bla@rkk.nl");
        }

    }

    @Test
    public void withHtml() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/OFRY20150921P.xml"); // This acually came in on dev and didn't work.
        //System.out.println(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        validate(table);
    }

    @Test
    public void oddLanguageJw() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/HOLL20151005P.xml"); // This actually came in on dev and didn't work.
        // the odd language
        //System.out.println(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        table.getProgramTable().forEach(p ->
            log.info("{}: {}", p.getMid(), p.getLanguages())
        );

        validate(table);
    }


    @Test
    public void oddLanguageSH() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/NED220231027P.xml");
        //System.out.println(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        table.getProgramTable().forEach(p -> log.info("{}: {}", p.getMid(), p.getLanguages()));

        validate(table);
    }

    @Test
    @Disabled("Some xml's simply do not validate")
    public void MSE_3144() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/MSE-3144/NED220160223P.xml");
        //System.out.println(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        JAXB.marshal(table, System.out);
        validate(table);
    }

    @Test
    public void MSE_3144_1() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/MSE-3144/NED320160221P.xml");
        //System.out.println(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        validate(table);
        //JAXB.marshal(table, System.out);
    }

    @Test
    public void MSE_3153() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        // Het koepelprogramma KN_1676556 krijgt geen Seizoen
        String xml = transform("pd/pd/MSE-3153/NED320160104P.xml");
        //System.out.println(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        //validate(table);
        Program p = table.<Program>find("KN_1676556").get();
        Group season = table.<Group>find("KN_1676501").get();

        assertThat(p.getEpisodeOf().first().getNumber()).isEqualTo(1);
        //JAXB.marshal(table, System.out);
    }

    @Test
    public void schedule() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/OZEE20150914P.xml");
        //System.out.println(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        Schedule schedule = table.getSchedule();

        assertNotNull(schedule);
        assertThat(schedule.getStart()).isEqualTo(LocalDateTime.parse("2015-09-14T06:00:00").atZone(Schedule.ZONE_ID).toInstant());
        assertThat(schedule.getStop()).isEqualTo(LocalDateTime.parse("2015-09-15T06:00:00").atZone(Schedule.ZONE_ID).toInstant());
        schedule.getScheduleEvents().clear(); // the stop time is influence by the end time of the last scheduleevent in it.
        assertThat(schedule.getStop()).isEqualTo(LocalDateTime.parse("2015-09-15T05:58:30").atZone(Schedule.ZONE_ID).toInstant());
    }

    @Test
    public void MSE_3202() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/BRAB20160317P.xml");
        //log.info(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        Map<String, Group> mids = new HashMap<>();
        for(Group group : table.getGroupTable()) {
            Group existing = mids.get(group.getMid());
            if (existing != null) {
                assertThat(existing.getMainTitle()).isEqualTo(group.getMainTitle());
                assertThat(existing.getMainDescription()).isEqualTo(group.getMainDescription());
                assertThat(existing.getBroadcasters()).isEqualTo(group.getBroadcasters());
            } else {
                mids.put(group.getMid(), group);
            }
        }
    }


    @Test
    public void MSE_3273() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/MSE-3273/NED320160704P.xml");
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        for (MediaObject mediaObject : table) {
            if (StringUtils.isEmpty(mediaObject.getMid())) {
                continue;
            }
            if (mediaObject.getBroadcasters().isEmpty()) {
                log.info(mediaObject + " has no broadcasters");
            }
            for (Broadcaster b : mediaObject.getBroadcasters()) {
                assertThat(b.getId()).isNotEmpty();
            }
        }
    }

    @Test
    public void MSE_3454_And_LanguageCode_XX_ZZ() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/MSE-3454/NED320160920P.xml");
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        for (Program program: table.getProgramTable()) {
            if ("POW_03195338".equals(program.getMid())) {
                List<Title> titles = new ArrayList<>(program.getTitles());
                assertThat(titles.get(0).get()).isEqualTo("Zappbios: Tussen twee werelden");
                assertThat(titles.get(1).get()).isEqualTo("Hördur - zwischen den Welten");
            }
            if ("POW_00163247".equals(program.getMid())) {
                assertThat(program.getLanguages()).containsExactly(UsedLanguage.of("zxx"));
            }
            if ("POW_00995211".equals((program.getMid()))) {
                assertThat(program.getLanguages()).containsExactly(UsedLanguage.of("und"));
            }
        }
        validate(table);

    }

    @Test
    public void missingCrids() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/NED320160711P.xml");
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        for (MediaObject mediaObject : table.getProgramTable()) {
            assertThat(mediaObject.getCrids()).isNotEmpty();
        }
    }

    @Test
    public void MSE_4581() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/NED320190715P.xml");
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        MediaObject example = table.find("VPWON_1307584").orElseThrow(IllegalArgumentException::new);
        log.info("{}", example.getContentRatings());
        //assertThat(example.getContentRatings()).containsExactly(ContentRating.DISCRIMINATIE, ContentRating.DRUGS_EN_ALCOHOL, ContentRating.GROF_TAALGEBRUIK);
        assertThat(example.getContentRatings()).containsExactly(ContentRating.GEWELD, ContentRating.DRUGS_EN_ALCOHOL, ContentRating.GROF_TAALGEBRUIK);
        assertThat(example.getAgeRating()).isEqualTo(AgeRating._12);


    }


    @Test
    public void bindincZDF() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = bindinc("bindinc/20201124021653000dayZDF_20201123.xml");

        //log.info(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        //JAXB.marshal(table, System.out);

        Program p = table.getProgramTable().stream().filter(pr -> pr.getCrids().contains("crid://media-press.tv/191255709")).findFirst().orElseThrow(NoSuchElementException::new);
        //log.info(Jackson2Mapper.getPrettyInstance().writeValueAsString(p));
        assertThat(p.getMainTitle()).isEqualTo("#heuldoch - Therapie wie noch nie");
        assertThat(TextualObjects.getDescription(p, TextualType.LONG)).isEqualTo("Om hun ontsnapping te financieren, doen de twee ontsnapte gevangenen, Gloria en Lin, zich voor als therapeuten voor vier mannen die veroordeeld zijn voor aanranding. Filmproducent Ralf, app-ontwikkelaar Julian, voetbalster Kobe en gynaecoloog Ferdinand willen zich min of meer vrijwillig rehabiliteren in een afgelegen landhuis in Brandenburg, met therapie als gevolg van de MeToo-beweging. (LONG)");
        assertThat(p.getCredits().getFirst().getGtaaUri()).isEqualTo("crid://bindinc/person/99992075861279");
        assertThat(p.getWorkflow()).isEqualTo(Workflow.PUBLISHED);
        assertThat(p.getScheduleEvents()).isNotEmpty();

        assertThat(p.getScheduleEvents().first().getChannel()).isEqualTo(Channel.ZDF_);

        assertThat(p.getGenres()).hasSize(1);
        assertThat(p.getGenres().first().getTermId()).isEqualTo("3.0.1.3");

        Program movie = table.getProgramTable().stream().filter(pr -> pr.getCrids().contains("crid://media-press.tv/1444377")).findFirst().orElseThrow(NoSuchElementException::new);
        assertThat(movie.getGenres().first().getTermId()).isEqualTo("3.0.1.2");

        Program unfoundgenre = table.getProgramTable().stream().filter(pr -> pr.getCrids().contains("crid://media-press.tv/198808847")).findFirst().orElseThrow(NoSuchElementException::new);
        assertThat(unfoundgenre.getGenres()).isEmpty();


        Program blackandWhite = table.getProgramTable().stream().filter(pr -> pr.getCrids().contains("crid://media-press.tv/191172999")).findFirst().orElseThrow(NoSuchElementException::new);

        assertThat(blackandWhite.getAvAttributes().getVideoAttributes().getColor()).isEqualTo(ColorType.BLACK_AND_WHITE);
        assertThat(blackandWhite.getScheduleEvents().first().getAvAttributes().getVideoAttributes().getAspectRatio()).isEqualTo(AspectRatio._16x9);
        assertThat(blackandWhite.getScheduleEvents().first().getAvAttributes().getVideoAttributes().getColor()).isEqualTo(ColorType.BLACK_AND_WHITE);


        assertThat(blackandWhite.getScheduleEvents().first().getAvAttributes().getAudioAttributes().getNumberOfChannels()).isEqualTo(2);

        for (Program program : table.getProgramTable()) {
            for (Credits c : program.getCredits()) {
                assertThat(c.getRole()).isNotNull();
            }
        }

        // No such term with reference urn:bindinc:genre:AmusementCHANGED
        assertThat(appender.getEvents().stream().filter(e ->  e.getLevel().compareTo(Level.WARN) <= 0)).hasSize(1);
    }

    @Test
    public void bindincTV01() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = bindinc("bindinc/20201208185718000dayTV0120201209.xml");

        //log.info(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        assertThat(table.getProgramTable()).hasSize(34);
        //JAXB.marshal(table, System.out);

        Program p = (Program) table.find("POW_04866660").get();
        //log.info(Jackson2Mapper.getPrettyInstance().writeValueAsString(p));
        assertThat(p.getMainTitle()).isEqualTo("NOS Journaal: Briefing door het RIVM");
        assertThat(p.getCrids()).containsExactly("crid://media-press.tv/203053643", "crid://npo/programmagegevens/1902975399668");
        assertThat(p.getWorkflow()).isEqualTo(Workflow.PUBLISHED);
        assertThat(p.getScheduleEvents()).isNotEmpty();
        assertThat(p.getCountries().getFirst().getName(Locales.NETHERLANDISH)).isEqualTo("Oost Duitsland");

        Program p2 = (Program) table.find("POW_04508476").get();
        assertThat(p2.getScheduleEvents()).hasSize(2);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "bindinc/20201208185718000dayTV0120201209.xml",
        "bindinc/20230605120229000dayLAUN20230623.xml"
    })
    public void bindincGenres(String resource) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        String xml = bindinc(resource);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        assertThat(appender.getEvents().stream().filter(e ->  e.getLevel().compareTo(Level.WARN) <= 0)).isEmpty();
    }

    @Test
    public void bindincARTT() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = bindinc("bindinc/20210210220659000dayARTT20210220.xml");

        //log.info("{}", xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        //JAXB.marshal(table, System.out);

        // this seems to be kind of a 'strand. This program contains multiple short movies
        Program kurzSchluss = table.<Program>findByCrid("crid://media-press.tv/206782916").orElseThrow(IllegalStateException::new);
        // like:
        Program heimweh = table.<Program>findByCrid("crid://media-press.tv/133868451").orElseThrow(IllegalStateException::new);
        Program silence = table.<Program>findByCrid("crid://media-press.tv/206796806").orElseThrow(IllegalStateException::new);
        Program maestro = table.<Program>findByCrid("crid://media-press.tv/206782916").orElseThrow(IllegalStateException::new);
        log.info("strand kurzSchluss: {}", kurzSchluss.getScheduleEvents());
        log.info("heimweh: {}", heimweh.getScheduleEvents());
        log.info("silence: {}", silence.getScheduleEvents());
        log.info("maestro: {}", maestro.getScheduleEvents());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        // MSE_5051
        "NED320201208P.xml",
        "NED320210109P.xml",
        "BVNT20210321P.xml",
        "NED220210310P.xml",
        "NED120210310P.xml",
        "NED320190902P.xml" //  MSE_4593
         })
    public void MSE_5051_newgenres(String source) throws ParserConfigurationException, TransformerException, SAXException, IOException {
        String xml = transform("pd/pd/" + source);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        for (Program p : table.getProgramTable()) {
            // lets check whether all that looks believable
            log.info("{} {}: {}", p.getMid(), p.getMainTitle(), p.getGenres().stream().map(g -> g.getTermId() + ":" + g.getDisplayName()).collect(Collectors.joining(", ")));
        }
        validate(table);
    }


    @Test
    public void API_535_missingMainTitle() throws IOException, ParserConfigurationException, TransformerException, SAXException {
        String xml = bindinc("bindinc/20210914010321000dayTV5M20210914.xml");
        //log.info(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        Program program = (Program) table.findByCrid("crid://media-press.tv/153553613").orElse(null);
        assertThat(program.getMainTitle()).isEqualTo("C'est la vie");
    }

    @Test
    public void MSE_5159_htmlInTitle() throws IOException, ParserConfigurationException, TransformerException, SAXException {
        String xml = transform("pd/pd/CULT20211016P.xml");
        //log.info(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        validate(table);

    }

    @Test
    public void missingTitleType() throws IOException, ParserConfigurationException, TransformerException, SAXException {
        String xml = bindinc("bindinc/20240313075603000dayARD_20240402.xml");
//        log.info(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        validate(table);

    }


    @Test
    public void MSE_5213() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/NORH20220204P.xml");
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);


        assertThat(table.findByCrid("crid://npo/programmagegevens/2078210744668").get().getSocialRefs()).containsExactly(new SocialRef("#cultureclub"));
        assertThat(table.findByCrid("crid://npo/programmagegevens/2078210758668").get().getSocialRefs()).isEmpty();

    }

    @Test
    public void MSE_5290_series_genres() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/NED220211017P.xml");
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);


        Program program = (Program) table.findByCrid("crid://npo/programmagegevens/2028047243668").get();
        log.info("{}", program);
        assertThat(program.getDescendantOf()).hasSize(2);
        String seasonMid = program.getEpisodeOf().first().getParentMid();
        Group season = table.getGroup(seasonMid).orElse(null);
        assertThat(season.getGenres()).hasSize(1);
        assertThat(season.getGenres().first().getTermId()).isEqualTo("3.0.1.8");

        String seriesMid = season.getMemberOf().first().getMidRef();
        Group series = table.getGroup(seriesMid).orElse(null);

        assertThat(series.getGenres()).hasSize(1);
        assertThat(series.getGenres().first().getTermId()).isEqualTo("3.0.1.8");
    }

    @Test
    public void MSE_5303_translatedTitle() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/NED320220729P.xml");
        log.info("{}", xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        validate(table);

        Group withWhiteSpaceOriginalTitle = table.getGroup("BV_101408213").orElseThrow(AssertionError::new);
        assertThat(withWhiteSpaceOriginalTitle.getOriginalTitle()).isNull();

        Group jeugdJournaal = table.getGroup("22Jeugd1900geb").orElseThrow(AssertionError::new);

        assertThat(jeugdJournaal.getOriginalTitle()).isEqualTo("NOS Jeugdjournaal met gebarentaal");

    }

    @Test
    public void _101schedule() throws IOException, ParserConfigurationException, TransformerException, SAXException {
        String xml = transform("pd/pd/101_20240714P.xml");
        log.info("{}", xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        validate(table);

        for (Program p : table.getProgramTable()) {
            p.getEpisodeOf().stream().map(MemberRef::getMidRef).forEach(
                r -> assertThat(table.getGroup(r)).isNotNull()
            );

        }
    }


    private String bindinc(String resource) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        genreFunction.setNotFound(NotFound.IGNORE);// TODO API-460
        genreFunction.setMatchOnValuePrefix(BINDINC_GENRE_PREFIX);
        genreFunction.setIgnore(Set.of(BINDINC_GENRE_PREFIX + "Overige"));

        Document channelMapping = createChannelMapping(ChannelIdType.BINDINC);

        String xml = transform(resource, (transformer) -> {
            transformer.setParameter(XSL_PARAM_PERSON_URI_PREFIX, "crid://bindinc/person/");
            transformer.setParameter(XSL_PARAM_WORKFLOW, Workflow.PUBLISHED.getXmlValue());
            transformer.setParameter(XSL_PARAM_LONGDESCRIPTIONS, "true");
            transformer.setParameter(XSL_PARAM_CHANNELMAPPING, channelMapping);
            }
        );
        return xml;

    }


    static TransformerFactoryImpl FACTORY = new TransformerFactoryImpl();

    private static final Set<Net> KNOWN_NETS = new HashSet<>(Arrays.asList(new Net("ZAPP", "Z@PP"), new Net("ZAPPELIN", "Zappelin")));
    static {
        SaxonConfiguration configuration = new SaxonConfiguration();
        configuration.setExtensions(Arrays.asList(
            new FindBroadcasterFunction(null) {
                @Override
                public ExtensionFunctionCall makeCallExpression() {
                    return new ExtensionFunctionCall() {
                        @SuppressWarnings("resource")
                        @Override
                        public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                            String value = arguments[0].iterate().next().getStringValue().trim().toUpperCase().replaceAll("\\s+", "_");
                            switch (value) {
                                case "AVROTROS" -> value = "AVTR";
                                case "KRO-NCRV" -> value = "KRNC";
                                case "SOCUTERA" -> value = "SOCU";
                                case "OMROP_FRYSLAN" -> value = "ROFR";
                                case "" -> log.warn("empty value");
                            }
                            return new StringValue(value.substring(0, Math.min(4, value.length())));
                        }
                    };
                }
            },

            new FindNetFunction(() -> KNOWN_NETS),
            new MisGenreFunction(),
            genreFunction,
            new HtmlStripperFunction(),
            new ValidListValueFunction(),
            new ValidValueFunction()
            )
        );
        FACTORY.setConfiguration(configuration);
    }

    private static String transform(String resource) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        return transform(resource, (t) -> {});
    }

    static String transform(InputStream resource, Consumer<Transformer> configure) throws TransformerException, IOException, SAXException, ParserConfigurationException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Result result = new StreamResult(out);
        getTransformer(configure).transform(new StreamSource(resource), result);
        return out.toString(StandardCharsets.UTF_8);
    }

    private static String transform(String resource, Consumer<Transformer> configure) throws TransformerException, IOException, SAXException, ParserConfigurationException {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (input == null) {
            throw new IllegalArgumentException("Could not find " + resource);
        }
        return transform(input, configure);
    }



    private static synchronized Transformer getTransformer(Consumer<Transformer> configure) throws TransformerConfigurationException, ParserConfigurationException, SAXException, IOException {

        Transformer transformer = getTransformer("/nl/vpro/media/tva/tvaTransformer.xsl");
        transformer.setParameter(
            XSL_PARAM_CHANNELMAPPING,
            createChannelMapping(Constants.ChannelIdType.PD));
        transformer.setParameter(
            XSL_PARAM_WORKFLOW,
            Workflow.FOR_REPUBLICATION.getXmlValue()
        );
        configure.accept(transformer);
        return transformer;
    }

    static Transformer getTransformer(String resource) throws TransformerConfigurationException {
        StreamSource stylesource = new StreamSource(TVATransformerTest.class.getResourceAsStream(resource));
        Transformer transformer = FACTORY.newTransformer(stylesource);

        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        return transformer;
    }

    private void validate(MediaTable o) {
        Validator validator = Validation.getValidator();
        JAXB.marshal(o, OutputStream.nullOutputStream()); // just marshall it once to ensure getTitles is called....
        //JAXB.marshal(o, System.out);
        Set<ConstraintViolation<MediaTable>> validate = validator.validate(o, ValidationLevel.WEAK_WARNING.getClasses());
        for (ConstraintViolation<MediaTable> cv : validate) {
            log.info("{} : {}: {}", cv.getInvalidValue(), cv.getPropertyPath(), cv.getMessage());
        }
        assertThat(validator.validate(o, ValidationLevel.POMS.getClasses())).isEmpty();

        for (MediaObject program : o) {
            //program.getTitles();
            //System.out.println("" + program.getBroadcasters());
            Set<ConstraintViolation<MediaObject>> constraintViolations = validator.validate(program, ValidationLevel.POMS.getClasses());
            for (ConstraintViolation<MediaObject> cv : constraintViolations) {
                log.warn(cv.getMessage());
            }
            assertThat(constraintViolations).isEmpty();
        }

    }
}
