package nl.vpro.domain.api;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.page.Page;
import nl.vpro.domain.page.PageType;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

public class MultiplePageResultTest {

    @Test
    public void testJson() throws Exception {
        MultiplePageResult list = new MultiplePageResult(
            Arrays.asList("http://vpro.nl/bla", "http://vpro.nl/foo"),
            Arrays.asList(new Page(PageType.PLAYER), null), null
        );
        Jackson2TestUtil.assertThatJson(Jackson2Mapper.getInstance().writeValueAsString(list)).isSimilarTo("{\"total\":2, \"totalQualifier\":\"EQUAL_TO\", \"items\":[{\"id\":\"http://vpro.nl/bla\",\"result\":{\"objectType\":\"page\",\"type\":\"PLAYER\"}},{\"id\":\"http://vpro.nl/foo\",\"error\":\"Not found http://vpro.nl/foo\"}]}");

    }
    @Test
    public void testXml() {
        MultiplePageResult list = new MultiplePageResult(Arrays.asList("http://vpro.nl/bla", "http://vpro.nl/foo"), Arrays.asList(new Page(PageType.PLAYER), null), null);
        JAXBTestUtil.roundTripAndSimilar(list, """
            <api:multiplePageResult total="2" totalQualifier="EQUAL_TO" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                <api:items>
                    <api:item xsi:type="api:multiplePageEntryType" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                        <api:id>http://vpro.nl/bla</api:id>
                        <api:result xsi:type="pages:pageType" type="PLAYER"/>
                    </api:item>
                    <api:item xsi:type="api:multiplePageEntryType" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                        <api:id>http://vpro.nl/foo</api:id>
                        <api:error>Not found http://vpro.nl/foo</api:error>
                    </api:item>
                </api:items>
            </api:multiplePageResult>""");

    }

}
