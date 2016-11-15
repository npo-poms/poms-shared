package nl.vpro.domain.media.search;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeSet;

import javax.xml.bind.JAXB;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.AVType;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.ProgramType;
import nl.vpro.domain.media.support.Tag;
import nl.vpro.domain.user.TestEditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Michiel Meeuwissen
 * @since 1.7
 */
public class MediaListTest {
    @Before
    public void setup() {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
    }

    @Test
    public void marshalMediaListItem() throws IOException, SAXException {
        Program program = JAXB.unmarshal(new StringReader("<program xmlns=\"urn:vpro:media:2009\" urn='urn:vpro:media:program:123'><broadcaster>VPRO</broadcaster></program>"), Program.class);
        program.setCreationDate(new Date(1343922085885L));
        program.setLastModified(new Date(1343922085885L));
        program.setCreatedBy(TestEditors.vproEditor());
        program.setType(ProgramType.CLIP);
        program.setAVType(AVType.VIDEO);
        program.setTags(new TreeSet<>(Arrays.asList(new Tag("foo"), new Tag("bar"))));


        MediaList<MediaListItem> xmlList = new MediaList<>(new Pager(1, 10, null, Pager.Direction.ASC), 1000, new MediaListItem(program));


        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<s:list totalCount=\"1000\" offset=\"1\" max=\"10\" order=\"ASC\" size=\"1\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:s=\"urn:vpro:media:search:2012\" xmlns:update=\"urn:vpro:media:update:2009\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <s:item xsi:type=\"s:mediaListItem\" avType=\"VIDEO\" id=\"123\" mediaType=\"nl.vpro.domain.media.Program\" urn=\"urn:vpro:media:program:123\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "        <s:broadcaster>VPRO</s:broadcaster>\n" +
            "        <s:title></s:title>\n" +
            "        <s:subTitle></s:subTitle>\n" +
            "        <s:creationDate>2012-08-02T17:41:25.885+02:00</s:creationDate>\n" +
            "        <s:lastModified>2012-08-02T17:41:25.885+02:00</s:lastModified>\n" +
            "        <s:createdBy>editor@vpro.nl</s:createdBy>\n" +
            "        <s:sortDate>2012-08-02T17:41:25.885+02:00</s:sortDate>\n" +
            "        <s:type>CLIP</s:type>\n" +
            "        <s:numberOfLocations>0</s:numberOfLocations>\n" +
            "        <s:tag>bar</s:tag>\n" +
            "        <s:tag>foo</s:tag>\n" +
            "    </s:item>\n" +
            "</s:list>";
        StringWriter writer = new StringWriter();
        JAXB.marshal(xmlList, writer);
        System.out.println(writer.toString());
        Diff diff = XMLUnit.compareXML(expected, writer.toString());
        assertTrue(diff.toString() + " " + writer.toString(), diff.identical());

        MediaList<MediaListItem> list = JAXB.unmarshal(new StringReader(writer.toString()), MediaList.class);
        assertEquals("urn:vpro:media:program:123", list.getList().get(0).getUrn());
        validate(writer.toString());
    }


    protected void validate(String string) throws SAXException, IOException {
        Validator validator = Xmlns.SCHEMA.newValidator();
        validator.validate(new StreamSource(new StringReader(string)));
    }
}
