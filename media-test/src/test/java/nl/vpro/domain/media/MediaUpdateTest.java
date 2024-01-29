package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.LocalDateTime;

import jakarta.validation.ConstraintViolation;
import javax.xml.XMLConstants;
import jakarta.xml.bind.JAXB;
import jakarta.xml.transform.stream.StreamSource;
import jakarta.xml.validation.*;

import org.junit.jupiter.api.*;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.domain.media.update.MediaUpdate;
import nl.vpro.domain.media.update.ProgramUpdate;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.util.Version;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * Created a program 'with everything' then makes an 'update' object of it. Checks the XML, and marshall it back to an update object.
 * <p>
 * Then take that, make a simple {@link MediaObject} of it and check _its_ XML.
 *
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@SuppressWarnings("DataFlowIssue")
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class MediaUpdateTest {

    static ProgramUpdate rounded;

    static {
        ClassificationServiceLocator.setInstance(MediaClassificationService.getInstance());
    }

    @Test
    @Tag("withEverything")
    public void withEverything1() throws Exception {

        Program withEverything = MediaTestDataBuilder
            .program()
            .withEverything()
            .withFixedDates()
            .build();


        ProgramUpdate update = ProgramUpdate.create(withEverything, OwnerType.BROADCASTER);
        update.setVersion(Version.of(5, 12));
        log.info("{}", update.getVersion());

        rounded = JAXBTestUtil.roundTripAndValidateAndSimilar(update,
            getClass().getResource("/nl/vpro/domain/media/update/vproMediaUpdate.xsd"),
            getClass().getResourceAsStream("/programupdate-with-everything.xml")
        );

        assertThat(update.violations()).isEmpty();
    }

    @Test
    public void withEverythingJson() throws Exception {

        Program withEverything = MediaTestDataBuilder
            .program()
            .withEverything()
            .withFixedDates()
            .build();


        ProgramUpdate update = ProgramUpdate.create(withEverything, OwnerType.BROADCASTER);
        update.setVersion(Version.of(5, 12));
        log.info("{}", update.getVersion());
        rounded = Jackson2TestUtil.roundTripAndSimilar(
            Jackson2Mapper.getInstance(),
            update,
            getClass().getResourceAsStream("/program-from-update-with-everything.json")

        );

        assertThat(update.violations()).isEmpty();
    }


     @Test
     public void withConstrainedJson() {

         ProgramUpdate rounded = Jackson2TestUtil.roundTripAndSimilar(
             Jackson2Mapper.getStrictInstance(), ProgramUpdate.create(MediaTestDataBuilder.broadcast().constrained()
                 .id(1L)
                 .mid("mid_1")
                 .ageRating(AgeRating.ALL)
                 .withScheduleEvent(Channel.RAD1, LocalDateTime.of(2023, 6, 5, 10, 30))
                 .build()), """
                 {
                        "objectType" : "programUpdate",
                        "mid" : "mid_1",
                        "type" : "BROADCAST",
                        "avType" : "VIDEO",
                        "urn" : "urn:vpro:media:program:1",
                        "embeddable" : true,
                        "broadcaster" : [ "BNN", "AVRO" ],
                        "title" : [ {
                          "value" : "Main title",
                          "type" : "MAIN"
                        }, {
                          "value" : "Short title",
                          "type" : "SHORT"
                        }, {
                          "value" : "Episode title MIS",
                          "type" : "SUB"
                        } ],
                        "duration" : "P0DT2H0M0.000S",
                        "ageRating" : "ALL",
                        "scheduleEvents" : [ {
                          "channel" : "RAD1",
                          "start" : 1685953800000,
                          "duration" : 1800000,
                          "guideDay" : "2023-06-05"
                        } ]
                      }
                 """);

         assertThat(rounded.getAgeRating()).isEqualTo(AgeRating.ALL);


     }


    @Test
    public void polyMorphJson() throws IOException {
        assertThat(Jackson2Mapper.getInstance()
            .readValue(getClass().getResourceAsStream("/program-from-update-with-everything.json"), MediaUpdate.class)).isInstanceOf(ProgramUpdate.class);
    }

    @Test
    @Tag("withEverything")
    public void withEverything2() throws Exception {
        Assumptions.assumeTrue(rounded != null);
        MediaObject fetched = rounded.fetch(OwnerType.BROADCASTER);


        JAXBTestUtil.roundTripAndSimilar(fetched,
            getClass().getResourceAsStream("/program-from-update-with-everything.xml")
        );
    }

    @Test
    public void deleted() {
        Program deleted = MediaTestDataBuilder.program().workflow(Workflow.DELETED).build();
        ProgramUpdate update = ProgramUpdate.create(deleted, OwnerType.BROADCASTER);

        ProgramUpdate programUpdate = JAXBTestUtil.roundTripAndSimilar(update, """
            <program xmlns="urn:vpro:media:update:2009" deleted="true" embeddable="true" xmlns:shared="urn:vpro:shared:2009" xmlns:media="urn:vpro:media:2009">
              <intentions/>
              <targetGroups/>
              <geoLocations/>
              <topics/>
              <credits/>
              <locations/>
              <scheduleEvents/>
              <images/>
              <segments/>
            </program>""");
        assertThat(programUpdate.isDeleted()).isTrue();

    }



    @Test
    public void generateCompleteProgramUpdateXMLandVerifyAgainstXSD() throws Exception {
        StringWriter writer = new StringWriter();

        Program program = MediaTestDataBuilder.program().withEverything().build();
        ProgramUpdate programUpdate = ProgramUpdate.create(program);
        //JAXB.marshal(programUpdate, System.out); //log
        JAXB.marshal(programUpdate, writer);

        StreamSource xml = new StreamSource(new StringReader(writer.toString()));

        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        log.info("{}", getClass().getResource("/nl/vpro/domain/media/update/vproMediaUpdate.xsd"));
        Schema schema = schemaFactory.newSchema(
                getClass().getResource("/nl/vpro/domain/media/update/vproMediaUpdate.xsd")
        );
        Validator validator = schema.newValidator();
        validator.validate(xml);
    }

    @Test
    public void testWithoutIntentions() {

        String withoutIntentions = """
            <program xmlns="urn:vpro:media:update:2009" xmlns:media="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" embeddable="true">
                <locations/>
                <scheduleEvents/>
                <images/>
                <segments/>
            </program>""";

        ProgramUpdate update = JAXB.unmarshal(new StringReader(withoutIntentions), ProgramUpdate.class);
        assertThat(update.getIntentions()).isNull();

    }

    @Test
    public void testWithIntentions() {

        String withoutIntentions = """
            <program xmlns="urn:vpro:media:update:2009" xmlns:media="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" embeddable="true">
                <locations/>
                <scheduleEvents/>
                <images/>
                <segments/>
                <intentions />
            </program>""";

        ProgramUpdate update = JAXB.unmarshal(new StringReader(withoutIntentions), ProgramUpdate.class);

        assertThat(update.getIntentions()).isEmpty();


    }


    @Test
    public void testWithIntentionsOfOtherOwner() {
        ProgramUpdate update  = ProgramUpdate.create(
            MediaBuilder.program().intentions(Intentions.builder().owner(OwnerType.MIS).value(IntentionType.INFORM_INDEPTH).build()).build(),
            OwnerType.BROADCASTER
        );

        assertThat(update.getIntentions()).containsExactly(IntentionType.INFORM_INDEPTH);
    }

    @Test
    public void testWithIntentionsOfOwner() {
        ProgramUpdate update  = ProgramUpdate.create(
            MediaBuilder.program().intentions(
                Intentions.builder()
                    .owner(OwnerType.MIS).value(IntentionType.INFORM_INDEPTH).build(),
                 Intentions.builder()
                    .owner(OwnerType.BROADCASTER).value(IntentionType.ENTERTAINMENT_INFORMATIVE).build()

            ).build(),
            OwnerType.MIS
        );

        assertThat(update.getIntentions()).containsExactly(IntentionType.INFORM_INDEPTH);

    }

    @Test
    public void testWithWarning() {
        ProgramUpdate update = ProgramUpdate.create(
            MediaBuilder.program()
                .subTitle("bla")
                .build()
        );
        for (ConstraintViolation<?> v : update.warningViolations()) {
            log.info("{}: {}", v.getPropertyPath(), v.getMessage());
        }

        for (ConstraintViolation<?> v : update.violations()) {
            log.info("{}: {}", v.getPropertyPath(), v.getMessage());
        }
    }
}
