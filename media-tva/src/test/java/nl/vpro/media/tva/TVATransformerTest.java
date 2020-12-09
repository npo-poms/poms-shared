package nl.vpro.media.tva;

import lombok.extern.slf4j.Slf4j;
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

import javax.validation.*;
import javax.xml.bind.JAXB;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.xml.sax.SAXException;

import com.neovisionaries.i18n.LanguageCode;

import nl.vpro.domain.TextualObjects;
import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.media.tva.saxon.extension.*;

import static nl.vpro.media.tva.Constants.*;
import static nl.vpro.test.util.jaxb.JAXBTestUtil.similar;
import static org.apache.commons.io.output.NullOutputStream.NULL_OUTPUT_STREAM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Michiel Meeuwissen
 * @since 4.1
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
@Slf4j
public class TVATransformerTest {


    EpgGenreFunction genreFunction = new EpgGenreFunction();

    @BeforeEach
    public void init() {
        genreFunction.setNotFoundIsFatal(true);
    }

    @BeforeAll
    public static void initAll() {
        ClassificationServiceLocator.setInstance(new MediaClassificationService());
    }
    @Test
    public void testTransform() throws TransformerException, ParserConfigurationException, SAXException, IOException {
        String xml = transform("pd/pd/NED320150805P.xml");
        similar(xml,
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<mediaInformation xmlns=\"urn:vpro:media:2009\"\n" +
                "                  publicationTime=\"2015-08-03T17:40:17.040+02:00\"\n" +
                "                  version=\"300\">\n" +
                "   <programTable>\n" +
                "      <program mid=\"POW_00252645\"\n" +
                "               type=\"BROADCAST\"\n" +
                "               avType=\"VIDEO\"\n" +
                "               embeddable=\"true\">\n" +
                "         <crid>crid://npo/programmagegevens/840343221668</crid>\n" +
                "         <broadcaster id=\"EO\">EO</broadcaster>\n" +
                "         <title type=\"MAIN\" owner=\"MIS\">SERIE TITEL</title>\n" +
                "         <title type=\"SUB\" owner=\"MIS\">IK BEN EEN AFLEVERING</title>\n" +
                "         <!--tva:Title[@type='parentseriestitle'] 'IK BEN EEN MOEDERSERIE' goes to series-->\n" +
                "         <!--tva:Title[@type='translatedtitle'] 'ICH BIN EIN SEIZON' goes to season-->\n" +
                "         <title type=\"ORIGINAL\" owner=\"MIS\">ICH BIN EIN PROGRAM</title>\n" +
                "         <!--tva:Synopsis[@length = 'long'] 'Dit is de seizoensbeschrijving' goes to season-->\n" +
                "         <description type=\"SHORT\" owner=\"MIS\">IK BEN DE KORTE BESCHRIJVING</description>\n" +
                "         <description type=\"MAIN\" owner=\"MIS\">IK BEN DE LANGE BESCHRIJVING</description>\n" +
                "         <!--tva:Synopsis[@length = 'long' and @type = 'parentSeriesSynopsis'] 'Dit is de Seriesbeschrijving' goes to series-->\n" +
                "         <description owner=\"MIS\" type=\"KICKER\">Dit is de kicker</description>\n" +
                "         <genre id=\"3.0.1.1.11\"/>\n" +
                "         <country code=\"GB\"/>\n" +
                "         <language code=\"cs\"/>\n" +
                "         <releaseYear>2009</releaseYear>\n" +
                "         <duration>PT00H12M55S</duration>\n" +
                "         <ageRating>ALL</ageRating>\n" +
                "         <contentRating>DRUGS_EN_ALCOHOL</contentRating>\n" +
                "         <contentRating>GROF_TAALGEBRUIK</contentRating>\n" +
                "         <descendantOf type=\"SEASON\" midRef=\"POW_00252644\"/>\n" +
                "         <descendantOf type=\"SERIES\" midRef=\"POW_00818820\"/>\n" +
                "         <episodeOf type=\"SEASON\" midRef=\"POW_00252644\" index=\"1\"/>\n" +
                "      </program>\n" +
                "      <program mid=\"POW_00252645_1\"\n" +
                "               type=\"BROADCAST\"\n" +
                "               avType=\"VIDEO\"\n" +
                "               embeddable=\"true\">\n" +
                "         <crid>crid://npo/programmagegevens/840343221669</crid>\n" +
                "         <broadcaster id=\"EO\">EO</broadcaster>\n" +
                "         <title type=\"MAIN\" owner=\"MIS\">SERIE TITEL</title>\n" +
                "         <title type=\"SUB\" owner=\"MIS\">IK BEN EEN AFLEVERING</title>\n" +
                "         <!--tva:Title[@type='parentseriestitle'] 'IK BEN EEN MOEDERSERIE' goes to series-->\n" +
                "         <!--tva:Synopsis[@length = 'long'] 'Dit is de seizoensbeschrijving' goes to season-->\n" +
                "         <description type=\"SHORT\" owner=\"MIS\">IK BEN DE KORTE BESCHRIJVING</description>\n" +
                "         <description type=\"MAIN\" owner=\"MIS\">IK BEN DE LANGE BESCHRIJVING</description>\n" +
                "         <!--tva:Synopsis[@length = 'long' and @type = 'parentSeriesSynopsis'] 'Dit is de Seriesbeschrijving' goes to series-->\n" +
                "         <description owner=\"MIS\" type=\"KICKER\">Dit is de kicker</description>\n" +
                "         <genre id=\"3.0.1.1.11\"/>\n" +
                "         <country code=\"GB\"/>\n" +
                "         <language code=\"en\"/>\n" +
                "         <releaseYear>2009</releaseYear>\n" +
                "         <duration>PT00H12M55S</duration>\n" +
                "         <ageRating>9</ageRating>\n" +
                "         <contentRating>DRUGS_EN_ALCOHOL</contentRating>\n" +
                "         <contentRating>GROF_TAALGEBRUIK</contentRating>\n" +
                "      </program>\n" +
                "   </programTable>\n" +
                "   <groupTable>\n" +
                "      <group type=\"SEASON\" mid=\"POW_00252644\" avType=\"VIDEO\">\n" +
                "         <broadcaster id=\"EO\">EO</broadcaster>\n" +
                "         <title type=\"MAIN\" owner=\"MIS\">SERIE TITEL</title>\n" +
                "         <title type=\"ORIGINAL\" owner=\"MIS\">ICH BIN EIN SEIZON</title>\n" +
                "         <description type=\"MAIN\" owner=\"MIS\">Dit is de seizoensbeschrijving</description>\n" +
                "         <memberOf midRef=\"POW_00818820\" index=\"3\"/>\n" +
                "         <poSequenceInformation>3</poSequenceInformation>\n" +
                "      </group>\n" +
                "      <group type=\"SERIES\" mid=\"POW_00818820\" avType=\"VIDEO\">\n" +
                "         <broadcaster id=\"EO\">EO</broadcaster>\n" +
                "         <title type=\"MAIN\" owner=\"MIS\">IK BEN EEN MOEDERSERIE</title>\n" +
                "         <description type=\"MAIN\" owner=\"MIS\">Dit is de Seriesbeschrijving</description>\n" +
                "      </group>\n" +
                "   </groupTable>\n" +
                "   <schedule channel=\"NED3\"\n" +
                "             start=\"2015-08-15T06:30:00+02:00\"\n" +
                "             stop=\"2015-08-16T03:06:00+02:00\">\n" +
                "      <scheduleEvent urnRef=\"crid://npo/programmagegevens/840343221668\"\n" +
                "                     channel=\"NED3\"\n" +
                "                     net=\"ZAPP\">\n" +
                "         <repeat isRerun=\"true\">Bla bla</repeat>\n" +
                "         <start>2015-08-15T06:30:00+02:00</start>\n" +
                "         <duration>PT00H12M55S</duration>\n" +
                "         <poProgID>POW_00252645</poProgID>\n" +
                "         <poSeriesID>POW_00252644</poSeriesID>\n" +
                "      </scheduleEvent>\n" +
                "      <scheduleEvent urnRef=\"crid://npo/programmagegevens/840343221669\"\n" +
                "                     channel=\"NED3\"\n" +
                "                     net=\"ZAPP\">\n" +
                "         <repeat isRerun=\"false\">Bloe bloe</repeat>\n" +
                "         <start>2015-08-15T06:30:00+02:00</start>\n" +
                "         <duration>PT00H12M55S</duration>\n" +
                "         <poProgID>POW_00252645_1</poProgID>\n" +
                "      </scheduleEvent>\n" +
                "   </schedule>\n" +
                "</mediaInformation>");
    }

    @Test
    public void testTransform_MSE_4907() throws TransformerException, ParserConfigurationException, SAXException, IOException {
        String xml = transform("pd/pd/NED320200220P.xml");
        similar(xml, getClass().getResourceAsStream("/pd/pd/NED320200220P.mediatable.xml"));
    }

    @Test
    public void testUnmarshalAfterUnmarshal() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        MediaTable table = JAXB.unmarshal(new StringReader(transform("pd/pd/NED320150805P.xml")), MediaTable.class);
        validate(table);

        Program program = table.getProgramTable().get(0);


        assertThat(program.getMainTitle()).isEqualTo("SERIE TITEL");
        assertThat(program.getMainDescription()).isEqualTo("IK BEN DE LANGE BESCHRIJVING");
        assertThat(program.getLanguages()).containsExactly(LanguageCode.cs.toLocale());
        assertThat(TextualObjects.getDescription(program, TextualType.KICKER)).isEqualTo("Dit is de kicker");
        assertThat(program.getContentRatings()).containsExactly(ContentRating.DRUGS_EN_ALCOHOL, ContentRating.GROF_TAALGEBRUIK);
        assertThat(program.getAgeRating()).isEqualTo(AgeRating.ALL);


        Program program2 = table.getProgramTable().get(1);
        assertThat(program2.getAgeRating()).isEqualTo(AgeRating._9);


        assertThat(table.getGroupTable()).hasSize(2);

        assertThat(table.getGroupTable().get(0).getType()).isEqualTo(GroupType.SEASON);
        assertThat(table.getGroupTable().get(0).getAVType()).isEqualTo(AVType.VIDEO);
        assertThat(table.getGroupTable().get(0).getBroadcasters().get(0).getId()).isEqualTo("EO");
        assertThat(table.getGroupTable().get(0).getMainDescription()).isEqualTo("Dit is de seizoensbeschrijving");
        assertThat(table.getGroupTable().get(0).getMainTitle()).isEqualTo("SERIE TITEL");
        assertThat(table.getGroupTable().get(0).getMemberOf().first().getNumber()).isEqualTo(3);


        assertThat(table.getGroupTable().get(1).getType()).isEqualTo(GroupType.SERIES);
        assertThat(table.getGroupTable().get(1).getAVType()).isEqualTo(AVType.VIDEO);
        assertThat(table.getGroupTable().get(1).getBroadcasters().get(0).getId()).isEqualTo("EO");
        assertThat(table.getGroupTable().get(1).getMainDescription()).isEqualTo("Dit is de Seriesbeschrijving");
        assertThat(table.getGroupTable().get(1).getMainTitle()).isEqualTo("IK BEN EEN MOEDERSERIE");
    }

    @Test
    public void testRegional() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/OZEE20150914P.xml");
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        validate(table);
    }

    @Test
    public void testOddDate () throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/NED220150915P.xml");

        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        validate(table);
    }

    @Test
    public void testMoreSeasons() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/NED220150919P.xml");
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        validate(table);
        assertThat(table.getGroupTable()).hasSize(30);
        assertThat(table.getGroupTable().stream().filter(g -> g.getType() == GroupType.SEASON).collect(Collectors.toList())).hasSize(27);
        assertThat(table.getGroupTable().stream().filter(g -> g.getType() == GroupType.SERIES).collect(Collectors.toList())).hasSize(3);

        // MSE-4572
        Optional<MediaObject> byCrid = table.findByCrid("crid://npo/programmagegevens/1000741891668");
        assertThat(byCrid.get().getEmail()).isEmpty();
    }

    @Test
    public void testWithHtml() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/OFRY20150921P.xml"); // This acually came in on dev and didn't work.
        //System.out.println(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        validate(table);
    }

    @Test
    public void testOddLanguage() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/HOLL20151005P.xml"); // This acually came in on dev and didn't work.
        //System.out.println(xml);
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

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
    public void testSchedule() throws IOException, ParserConfigurationException, SAXException, TransformerException {
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
        log.info(xml);
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
                log.info("" + mediaObject + " has no broadcasters");
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
                assertThat(titles.get(1).get()).isEqualTo("H\u00f6rdur - zwischen den Welten");
            }
            if ("POW_00163247".equals(program.getMid())) {
                assertThat(program.getLanguages()).containsExactly(new Locale("zxx"));
            }
            if ("POW_00995211".equals((program.getMid()))) {
                assertThat(program.getLanguages()).containsExactly(new Locale("und"));
            }
        }
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
    public void MSE_4593() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String xml = transform("pd/pd/NED320190902P.xml");
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);
        log.info("{}", xml);
    }

    @Test
    public void bindincZDF() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        genreFunction.setNotFoundIsFatal(false); // TODO API-460

        String xml = transform("bindinc/20201124021653000dayZDF_20201123.xml", (transformer) -> {
                transformer.setParameter(XSL_PARAM_PERSON_URI_PREFIX, "crid://bindinc/person/");
                transformer.setParameter(XSL_PARAM_WORKFLOW, Workflow.PUBLISHED.getXmlValue());
            }
        );
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        JAXB.marshal(table, System.out);

        Program p = table.getProgramTable().stream().filter(pr -> pr.getCrids().contains("crid://media-press.tv/191255709")).findFirst().orElse(null);
        log.info(Jackson2Mapper.getPrettyInstance().writeValueAsString(p));
        assertThat(p.getMainTitle()).isEqualTo("#heuldoch - Therapie wie noch nie");
        assertThat(p.getCredits().get(0).getGtaaUri()).isEqualTo("crid://bindinc/person/99992075861279");
        assertThat(p.getWorkflow()).isEqualTo(Workflow.PUBLISHED);
    }

    @Test
    public void bindincTV01() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        genreFunction.setNotFoundIsFatal(false); // TODO API-460
        String xml = transform("bindinc/20201208185718000dayTV0120201209.xml", (transformer) -> {
                transformer.setParameter(XSL_PARAM_PERSON_URI_PREFIX, "crid://bindinc/person/");
                transformer.setParameter(XSL_PARAM_WORKFLOW, Workflow.PUBLISHED.getXmlValue());
            }
        );
        MediaTable table = JAXB.unmarshal(new StringReader(xml), MediaTable.class);

        JAXB.marshal(table, System.out);

        Program p = (Program) table.find("POW_04866660").get();
        log.info(Jackson2Mapper.getPrettyInstance().writeValueAsString(p));
        assertThat(p.getMainTitle()).isEqualTo("NOS Journaal: Briefing door het RIVM");
        assertThat(p.getCrids()).containsExactly("crid://media-press.tv/203053643", "crid://npo/programmagegevens/1902975399668");
        assertThat(p.getWorkflow()).isEqualTo(Workflow.PUBLISHED);

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
                        @Override
                        public Sequence<?> call(XPathContext context, Sequence[] arguments) throws XPathException {
                            String value = arguments[0].iterate().next().getStringValueCS().toString().trim().toUpperCase().replaceAll("\\s+", "_");
                            switch(value) {
                                case "AVROTROS": value = "AVTR"; break;
                                case "KRO-NCRV": value = "KRNC"; break;
                                case "SOCUTERA": value = "SOCU"; break;
                                case "OMROP_FRYSLAN": value = "ROFR"; break;
                                case "": log.warn("empty value"); break;
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
            new ValidListValueFunction()
            )
        );
        FACTORY.setConfiguration(configuration);
    }

    private String transform(String resource) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        return transform(resource, (t) -> {});
    }

    private String transform(String resource, Consumer<Transformer> configure) throws TransformerException, IOException, SAXException, ParserConfigurationException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Result result = new StreamResult(out);
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (input == null) {
            throw new IllegalArgumentException("Could not find " + resource);
        }
        getTransformer(configure).transform(new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)), result);
        return out.toString(StandardCharsets.UTF_8.name());
    }

    private Transformer getTransformer(Consumer<Transformer> configure) throws TransformerConfigurationException, ParserConfigurationException, SAXException, IOException {
        StreamSource stylesource = new StreamSource(getClass().getResourceAsStream("/nl/vpro/media/tva/tvaTransformer.xsl"));
        Transformer transformer = FACTORY.newTransformer(stylesource);

        transformer.setParameter(XSL_PARAM_NEWGENRES, "true");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setParameter(
            XSL_PARAM_CHANNELMAPPING,
            Constants.createChannelMapping(Constants.ChannelIdType.PD));
        configure.accept(transformer);
        return transformer;
    }

    private void validate(MediaTable o) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        JAXB.marshal(o, NULL_OUTPUT_STREAM); // just marshall it once to ensure getTitles is called....
       //JAXB.marshal(o, System.out);
        Set<ConstraintViolation<MediaTable>> validate = validator.validate(o);
        for (ConstraintViolation<MediaTable> cv : validate) {
            log.warn(cv.getMessage());
        }
        assertThat(validator.validate(o)).isEmpty();

        for (Program program : o.getProgramTable()) {
            //program.getTitles();
            //System.out.println("" + program.getBroadcasters());
            Set<ConstraintViolation<Program>> constraintViolations = validator.validate(program);
            for (ConstraintViolation<Program> cv : constraintViolations) {
                log.warn(cv.getMessage());
            }
            assertThat(constraintViolations).isEmpty();
        }
    }
}
