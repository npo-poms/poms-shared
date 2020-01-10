package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXB;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.junit.jupiter.api.*;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.update.ProgramUpdate;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.util.Version;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * Created a program 'with everything' then makes an 'update' object of it. Checks the XML, and marshall it back to an update object.
 *
 * Then take that, make a simple mediaobject of it and check _its_ XML.
 *
 *
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@Slf4j
public class MediaUpdateTest {

    static ProgramUpdate rounded;

    static {
        ClassificationServiceLocator.setInstance(MediaClassificationService.getInstance());
    }

    @Test
    public void withEverything1() throws Exception {

        Program withEverything = MediaTestDataBuilder
            .program()
            .withEverything()
            .withFixedDates()
            .build();

        withEverything.getVersion();
        ProgramUpdate update = ProgramUpdate.create(withEverything, OwnerType.BROADCASTER);
        update.setVersion(Version.of(5, 11));
        log.info("{}", update.getVersion());

        rounded = JAXBTestUtil.roundTripAndValidateAndSimilar(update,
            getClass().getResource("/nl/vpro/domain/media/update/vproMediaUpdate.xsd"),
            getClass().getResourceAsStream("/programupdate-with-everything.xml")
        );
    }

    @Test
    public void withEverything2() throws Exception {
        Assumptions.assumeTrue(rounded != null);
        MediaObject fetched = rounded.fetch(OwnerType.BROADCASTER);
        JAXBTestUtil.roundTripAndSimilar(fetched,
            getClass().getResourceAsStream("/program-from-update-with-everything.xml")
        );
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

        String withoutIntentions = "<program xmlns=\"urn:vpro:media:update:2009\" xmlns:media=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" embeddable=\"true\">\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>";

        ProgramUpdate update = JAXB.unmarshal(new StringReader(withoutIntentions), ProgramUpdate.class);
        assertThat(update.getIntentions()).isNull();

    }

    @Test
    public void testWithIntentions() {

        String withoutIntentions = "<program xmlns=\"urn:vpro:media:update:2009\" xmlns:media=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" embeddable=\"true\">\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "    <intentions />\n" +
            "</program>";

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
}
