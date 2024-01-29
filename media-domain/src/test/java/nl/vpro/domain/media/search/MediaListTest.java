package nl.vpro.domain.media.search;

import jakarta.xml.bind.JAXB;

import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.util.Arrays;
import java.util.TreeSet;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Editor;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import static nl.vpro.test.util.jaxb.JAXBTestUtil.assertThatXml;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Michiel Meeuwissen
 * @since 1.7
 */
public class MediaListTest {

    MediaList<MediaListItem> list;
    {
        Program program = JAXB.unmarshal(new StringReader("<program xmlns=\"urn:vpro:media:2009\" urn='urn:vpro:media:program:123'><broadcaster>VPRO</broadcaster></program>"), Program.class);
        program.setCreationInstant(Instant.ofEpochMilli(1343922085885L));
        program.setLastModifiedInstant(Instant.ofEpochMilli(1343922085885L));
        program.setCreatedBy( Editor.builder()
            .principalId("editor@vpro.nl")
            .displayName("Editor")
            .email("editor@vpro.nl")
            .build()
        );
        program.getLocations().add(Location.builder()
                .creationDate(Instant.ofEpochMilli(1343922085885L))
            .programUrl("http://www.vpro.nl/").build());
        program.setPublishStopInstant(Instant.ofEpochMilli(1343922085885L));
        program.setType(ProgramType.CLIP);
        program.setAVType(AVType.VIDEO);
        program.setTags(new TreeSet<>(Arrays.asList(new Tag("foo"), new Tag("bar"))));
        program.setMainTitle("");
        program.addTitle("", OwnerType.BROADCASTER, TextualType.SUB);
        list = new MediaList<>(
            MediaPager.builder()
                .offset(1)
                .max(10)
                .sort(MediaSortField.creationDate)
                .build()
            , 1000, new MediaListItem(program));
    }

    @Test
    public void xml() throws IOException, SAXException {
        String expected = """
            <?xml version="1.0" encoding="UTF-8"?><s:list xmlns:s="urn:vpro:media:search:2012" totalCount="1000" offset="1" max="10" sort="creationDate" order="ASC" size="1" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:update="urn:vpro:media:update:2009" xmlns:xs="http://www.w3.org/2001/XMLSchema">

                      <s:item xsi:type="s:mediaListItem" avType="VIDEO" mediaType="nl.vpro.domain.media.Program" id="123" urn="urn:vpro:media:program:123" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                        <s:broadcaster>VPRO</s:broadcaster>
                        <s:title/>
                        <s:subTitle/>
                        <s:creationDate>2012-08-02T17:41:25.885+02:00</s:creationDate>
                        <s:lastModified>2012-08-02T17:41:25.885+02:00</s:lastModified>
                        <s:createdBy>editor@vpro.nl</s:createdBy>
                        <s:sortDate>2012-08-02T17:41:25.885+02:00</s:sortDate>
                        <s:type>CLIP</s:type>
                        <s:publishStop>2012-08-02T17:41:25.885+02:00</s:publishStop>
                        <s:locations owner="BROADCASTER" workflow="PUBLISHED" platform="INTERNETVOD" creationDate="2012-08-02T17:41:25.885+02:00">
                          <programUrl>http://www.vpro.nl/</programUrl>
                          <avAttributes>
                            <avFileFormat>UNKNOWN</avFileFormat>
                          </avAttributes>
                        </s:locations>
                        <s:numberOfLocations>1</s:numberOfLocations>
                        <s:tag>bar</s:tag>
                        <s:tag>foo</s:tag>
                      </s:item>
                    </s:list>""";


        MediaList<MediaListItem> rounded = assertThatXml(list)
            .isSimilarTo(expected)
            .isValid(Xmlns.SCHEMA.newValidator())
            .get();

        assertEquals("urn:vpro:media:program:123", rounded.getList().get(0).getUrn());
    }



    @Test
    public void json() throws IOException, SAXException {
        String expected = """
                 {
                            "totalCount" : 1000,
                            "size" : 1,
                            "offset" : 1,
                            "max" : 10,
                            "sort" : "creationDate",
                            "order" : "ASC",
                            "items" : [ {
                              "broadcasters" : [ {
                                "value" : "VPRO"
                              } ],
                              "creationDate" : 1343922085885,
                              "lastModified" : 1343922085885,
                              "sortDate" : 1343922085885,
                              "mediaType" : "nl.vpro.domain.media.Program",
                              "locations" : [ {
                                "programUrl" : "http://www.vpro.nl/",
                                "avAttributes" : {
                                  "avFileFormat" : "UNKNOWN"
                                },
                                "owner" : "BROADCASTER",
                                "creationDate" : 1343922085885,
                                "workflow" : "PUBLISHED",
                                "platform" : "INTERNETVOD"
                              } ],
                              "numberOfLocations" : 1,
                              "tags" : [ "bar", "foo" ],
                              "id" : 123,
                              "urn" : "urn:vpro:media:program:123",
                              "createdBy" : "editor@vpro.nl",
                              "publishStop" : 1343922085885,
                              "avType" : "VIDEO",
                              "type" : "CLIP"
                            } ]
                          }
            """;


        MediaList<MediaListItem> rounded = Jackson2TestUtil.roundTripAndSimilar(list, expected);

    }



    protected void validate(String string) throws SAXException, IOException {
        Validator validator = Xmlns.SCHEMA.newValidator();
        validator.validate(new StreamSource(new StringReader(string)));
    }
}
