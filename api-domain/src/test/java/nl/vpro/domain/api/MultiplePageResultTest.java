package nl.vpro.domain.api;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.page.Page;
import nl.vpro.domain.page.PageType;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class MultiplePageResultTest {

    @Test
    public void testJson() throws Exception {
        MultiplePageResult list = new MultiplePageResult(
            Arrays.asList("http://vpro.nl/bla", "http://vpro.nl/foo"),
            Arrays.asList(new Page(PageType.PLAYER), null), null
        );
        assertThat(Jackson2Mapper.getInstance().writeValueAsString(list)).isEqualTo("{\"total\":2,\"items\":[{\"id\":\"http://vpro.nl/bla\",\"result\":{\"objectType\":\"page\",\"type\":\"PLAYER\"}},{\"id\":\"http://vpro.nl/foo\",\"error\":\"Not found http://vpro.nl/foo\"}]}");

    }
    @Test
    public void testXml() {
        MultiplePageResult list = new MultiplePageResult(Arrays.asList("http://vpro.nl/bla", "http://vpro.nl/foo"), Arrays.asList(new Page(PageType.PLAYER), null), null);
        JAXBTestUtil.roundTripAndSimilar(list, "<api:multiplePageResult total=\"2\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <api:items>\n" +
            "        <api:item xsi:type=\"api:multiplePageEntryType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "            <api:id>http://vpro.nl/bla</api:id>\n" +
            "            <api:result xsi:type=\"pages:pageType\" type=\"PLAYER\"/>\n" +
            "        </api:item>\n" +
            "        <api:item xsi:type=\"api:multiplePageEntryType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "            <api:id>http://vpro.nl/foo</api:id>\n" +
            "            <api:error>Not found http://vpro.nl/foo</api:error>\n" +
            "        </api:item>\n" +
            "    </api:items>\n" +
            "</api:multiplePageResult>");

    }

}
