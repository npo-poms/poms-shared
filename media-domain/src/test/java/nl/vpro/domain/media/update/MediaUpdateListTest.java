package nl.vpro.domain.media.update;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.Duration;

import javax.xml.bind.JAXB;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Michiel Meeuwissen
 */
@Slf4j
public class MediaUpdateListTest {


    @SuppressWarnings("unchecked")
    @Test
    public void marshalStrings() {
        MediaUpdateList<String> xmlList = new MediaUpdateList<>("a", "b");
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<list  offset='0' size='2' totalCount='2' xmlns:media=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"  xmlns=\"urn:vpro:media:update:2009\">\n" +
            "   <item  xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xs:string\">a</item>\n" +
            "   <item  xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xs:string\">b</item>" +
            "</list>";

        StringWriter writer = new StringWriter();
        JAXB.marshal(xmlList, writer);
        Diff diff = DiffBuilder.compare(expected).withTest(writer.toString()).checkForSimilar().ignoreWhitespace().build();
        assertFalse(diff.hasDifferences(), diff.toString() + " " + writer.toString());

        MediaUpdateList<String> list = JAXB.unmarshal(new StringReader(writer.toString()), MediaUpdateList.class);
        assertEquals("a", list.getList().get(0));
        assertEquals("b", list.getList().get(1));
        // validate(writer.toString()); TODO I can't get it working

    }


    @Test
    public void memberUpdateType() throws IOException, SAXException {
        MemberUpdate memberUpdate = new MemberUpdate(1,
            ProgramUpdate.create(MediaBuilder.program().avType(AVType.VIDEO).broadcasters(new Broadcaster("VPRO"))
                .titles(new Title("De titel", OwnerType.BROADCASTER, TextualType.MAIN))));

        MediaUpdateList<MemberUpdate> xmlList = new MediaUpdateList<>(memberUpdate);
        StringWriter writer = new StringWriter();
        JAXB.marshal(xmlList, writer);
        validate(writer.toString());
    }


    @SuppressWarnings("unchecked")
    @Test
    public void mediaUpdateList() throws IOException, SAXException {
        Program program = MediaBuilder
            .program()
            .  urn("urn:vpro:media:program:123")
            .  avType(AVType.VIDEO)
            .  mainTitle("hoi")
            .  broadcasters("VPRO")
            .  mid("POMS_1234")
            .segments(
                MediaBuilder.segment()
                    .broadcasters("VPRO")
                    .avType(AVType.VIDEO)
                    .type(SegmentType.VISUALRADIOSEGMENT)
                    .titles(new Title("segmenttitel", OwnerType.BROADCASTER, TextualType.MAIN))
                    .start(Duration.ZERO)
                    .build()
            ).build();

        assertThat(program.getMid()).isEqualTo("POMS_1234");
        MediaUpdateList<ProgramUpdate> list = new MediaUpdateList<>(
            ProgramUpdate.create(program));
        StringWriter writer = new StringWriter();
        JAXB.marshal(list, writer);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<list offset=\"0\" totalCount=\"1\" size=\"1\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <item xsi:type=\"programUpdateType\" avType=\"VIDEO\" embeddable=\"true\" mid=\"POMS_1234\" urn=\"urn:vpro:media:program:123\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "        <broadcaster>VPRO</broadcaster>\n" +
            "        <title type=\"MAIN\">hoi</title>\n" +
            "        <intentions/>\n" +
            "        <targetGroups/>\n" +
            "        <geoLocations/>\n" +
            "        <topics/>\n" +
            "        <credits/>\n" +
            "        <locations/>\n" +
            "        <scheduleEvents/>\n" +
            "        <images/>\n" +
            "        <segments>\n" +
            "            <segment avType=\"VIDEO\" embeddable=\"true\" midRef=\"POMS_1234\" type=\"VISUALRADIOSEGMENT\">\n" +
            "                <broadcaster>VPRO</broadcaster>\n" +
            "                <title type=\"MAIN\">segmenttitel</title>\n" +
            "                <intentions/>\n" +
            "                <targetGroups/>\n" +
            "                <geoLocations/>\n" +
            "                <topics/>\n" +
            "                <credits/>\n" +
            "                <locations/>\n" +
            "                <images/>\n" +
            "                <start>P0DT0H0M0.000S</start>\n" +
            "            </segment>\n" +
            "        </segments>\n" +
            "    </item>\n" +
            "</list>";
        log.info(writer.toString());
        Diff diff = DiffBuilder.compare(expected).withTest(writer.toString()).build();
        MediaUpdateList<ProgramUpdate> list2 = JAXB.unmarshal(new StringReader(writer.toString()), MediaUpdateList.class);
        if (diff.hasDifferences()) {
            assertThat(writer.toString()).isEqualTo(expected);
        }

        //JAXB.marshal(list2, System.out);

        assertEquals(1, list2.getList().get(0).getSegments().size());
        validate(writer.toString());
    }


    protected void validate(String string) throws SAXException, IOException {
        try {
            Validator validator = Xmlns.SCHEMA.newValidator();
            validator.validate(new StreamSource(new StringReader(string)));
        } catch (SAXParseException se) {
            log.error("For {}", string);
            throw se;
        }
    }

}
