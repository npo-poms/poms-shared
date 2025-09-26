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
import org.junit.jupiter.api.parallel.Isolated;
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
public class TVATransformerTest {
    final EpgGenreFunction genreFunction = new EpgGenreFunction();


    static ListAppender appender = new ListAppender("List");

    static {
        var loggerContext = LoggerContext.getContext(false);
        appender.start();
        loggerContext.getConfiguration().addLoggerAppender(loggerContext.getRootLogger(), appender);

    }

    @BeforeEach
    public void init() {
        appender.clear();
        genreFunction.setNotFound(NotFound.FATAL);
    }

    @BeforeAll
    public static void initAll() {
        ClassificationServiceLocator.setInstance(new MediaClassificationService());
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
        similar(xml, getClass().getResourceAsStream("/pd/pd/NED320200220P.mediatable.xml"));
    }

    @Test
    public void unmarshalAfterUnmarshal() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        MediaTable table = JAXB.unmarshal(new StringReader(transform("pd/pd/NED320150805P.xml")), MediaTable.class);
        validate(table);

        Program program = table.getProgramTable().get(0);


        assertThat(program.getMainTitle()).isEqualTo("SERIE TITEL");
        assertThat(program.getMainDescription()).isEqualTo("IK BEN DE LANGE BESCHRIJVING");
        assertThat(program.getLanguages()).containsExactly(UsedLanguage.of(cs));
        assertThat(TextualObjects.getDescription(program, TextualType.KICKER)).isEqualTo("Dit is de kicker");
        assertThat(program.getContentRatings()).containsExactly(ContentRating.DRUGS_EN_ALCOHOL, ContentRating.GROF_TAALGEBRUIK);
        assertThat(program.getAgeRating()).isEqualTo(AgeRating.ALL);


        Program program2 = table.getProgramTable().get(1);
        assertThat(program2.getAgeRating()).isEqualTo(AgeRating._9);


        assertThat(table.getGroupTable()).hasSize(2);

        assertThat(table.getGroupTable().get(0).getType()).isEqualTo(GroupType.SEASON);
        assertThat(table.getGroupTable().get(0)).hasAVType(AVType.VIDEO);
        assertThat(table.getGroupTable().get(0).getBroadcasters().get(0).getId()).isEqualTo("EO");
        assertThat(table.getGroupTable().get(0).getMainDescription()).isEqualTo("Dit is de seizoensbeschrijving");
        assertThat(table.getGroupTable().get(0).getMainTitle()).isEqualTo("SERIE TITEL");
        assertThat(table.getGroupTable().get(0).getMemberOf().first().getNumber()).isEqualTo(3);


        assertThat(table.getGroupTable().get(1).getType()).isEqualTo(GroupType.SERIES);
        assertThat(table.getGroupTable().get(1)).isVideo();
        assertThat(table.getGroupTable().get(1).getBroadcasters().get(0).getId()).isEqualTo("EO");
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
        table.getProgramTable().forEach(p -> {
            log.info("{}: {}", p.getMid(), p.getLanguages());
        });

        validate(table);
    }


    @Test
    public void oddLanguageSH() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/NED220231027P.xml");
        //System.out.println(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        table.getProgramTable().forEach(p -> {
            log.info("{}: {}", p.getMid(), p.getLanguages());
        });

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
        assertThat(p.getCredits().get(0).getGtaaUri()).isEqualTo("crid://bindinc/person/99992075861279");
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
        assertThat(p.getCountries().get(0).getName(Locales.NETHERLANDISH)).isEqualTo("Oost Duitsland");

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
            p.getEpisodeOf().stream().map(r -> r.getMidRef()).forEach(
                r -> assertThat(table.getGroup(r)).isNotNull()
            );

        }
    }


    private String bindinc(String resource) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        genreFunction.setNotFound(NotFound.IGNORE);// TODO API-460
        genreFunction.setMatchOnValuePrefix(BINDINC_GENRE_PREFIX);

        genreFunction.setIgnore(new HashSet<>(Arrays.asList(BINDINC_GENRE_PREFIX + "Overige")));

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


    TransformerFactoryImpl FACTORY = new net.sf.saxon.TransformerFactoryImpl();

    private static final Set<Net> KNOWN_NETS = new HashSet<>(Arrays.asList(new Net("ZAPP", "Z@PP"), new Net("ZAPPELIN", "Zappelin")));
    {
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

    private String transform(String resource) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        return transform(resource, (t) -> {});
    }

    private String transform(InputStream resource, Consumer<Transformer> configure) throws TransformerException, IOException, SAXException, ParserConfigurationException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Result result = new StreamResult(out);
        getTransformer(configure).transform(new StreamSource(resource), result);
        return out.toString(StandardCharsets.UTF_8);
    }

    private String transform(String resource, Consumer<Transformer> configure) throws TransformerException, IOException, SAXException, ParserConfigurationException {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (input == null) {
            throw new IllegalArgumentException("Could not find " + resource);
        }
        return transform(input, configure);
    }


    private Transformer getTransformer(Consumer<Transformer> configure) throws TransformerConfigurationException, ParserConfigurationException, SAXException, IOException {
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

     private Transformer getTransformer(String resource) throws TransformerConfigurationException {
        StreamSource stylesource = new StreamSource(getClass().getResourceAsStream(resource));
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
