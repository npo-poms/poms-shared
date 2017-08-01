package nl.vpro.domain.media.search;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public class MediaFormTest {


    @Test
    public void xml() throws IOException, SAXException {
        MediaForm form = MediaForm.builder()
            .sortOrder(MediaSortField.creationDate)
            .build();
        JAXBTestUtil.roundTripAndSimilar(form, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<s:mediaForm xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:s=\"urn:vpro:media:search:2012\" xmlns:update=\"urn:vpro:media:update:2009\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
                "    <s:pager>\n" +
                "        <s:offset>0</s:offset>\n" +
                "        <s:sort>creationDate</s:sort>\n" +
                "        <s:order>ASC</s:order>\n" +
                "    </s:pager>\n" +
                "</s:mediaForm>\n"
            );
    }

    @Test
    @Ignore
    public void builder() throws IOException, SAXException {
        MediaForm form = MediaForm.builder().broadcasters(null)
            .locationsCount(IntegerRange.builder().start(0L).stop(0L, true).build()).build();
        JAXBTestUtil.roundTripAndSimilar(form, "<a />");
    }
}
