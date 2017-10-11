package nl.vpro.domain.api.media;

import java.util.Arrays;

import org.junit.Test;

import nl.vpro.domain.api.*;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class TitleFacetListTest {

    TitleFacetList list;

    {
        TitleFacet facet1 = new TitleFacet();
        {
            TitleSearch subSearch = new TitleSearch();
            subSearch.setValue(new ExtendedTextMatcher("a*", Match.MUST, ExtendedMatchType.WILDCARD, false));

            facet1.setName("titlesWithA");
            facet1.setSubSearch(subSearch);
        }
        TitleFacet facet2 = new TitleFacet();
        {
            TitleSearch subSearch = new TitleSearch();
            subSearch.setValue(new ExtendedTextMatcher("b*", Match.MUST, ExtendedMatchType.WILDCARD, false));

            facet2.setName("titlesWithB");
            facet2.setSubSearch(subSearch);
        }
        list = new TitleFacetList(Arrays.asList(facet1, facet2));
        list.setMax(11);
        list.setSort(FacetOrder.COUNT_DESC);
    }


    @Test
    public void testJsonBinding() throws Exception {

        list = Jackson2TestUtil.roundTripAndSimilar(list,
            "{\n" +
            "  \"title\" : [ {\n" +
            "    \"name\" : \"titlesWithA\",\n" +
            "    \"subSearch\" : {\n" +
            "      \"value\" : {\n" +
            "        \"value\" : \"a*\",\n" +
            "        \"matchType\" : \"WILDCARD\",\n" +
            "        \"caseSensitive\" : false\n" +
            "      }\n" +
            "    }\n" +
            "  }, {\n" +
            "    \"name\" : \"titlesWithB\",\n" +
            "    \"subSearch\" : {\n" +
            "      \"value\" : {\n" +
            "        \"value\" : \"b*\",\n" +
            "        \"matchType\" : \"WILDCARD\",\n" +
            "        \"caseSensitive\" : false\n" +
            "      }\n" +
            "    }\n" +
            "  } ],\n" +
            "  \"sort\" : \"COUNT_DESC\",\n" +
            "  \"max\" : 11\n" +
            "}");

        assertThat(list.facets).hasSize(2);
        assertTrue(list.facets.get(0).getSubSearch() != null);
    }


    @Test
    public void testXmlBinding() throws Exception {



        list = JAXBTestUtil.roundTripAndSimilar(list, "<local:titleFacetList sort=\"COUNT_DESC\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
            "    <api:max>11</api:max>\n" +
            "    <api:title name=\"titlesWithA\">\n" +
            "        <api:subSearch>\n" +
            "            <api:value matchType=\"WILDCARD\" caseSensitive=\"false\">a*</api:value>\n" +
            "        </api:subSearch>\n" +
            "    </api:title>\n" +
            "    <api:title name=\"titlesWithB\">\n" +
            "        <api:subSearch>\n" +
            "            <api:value matchType=\"WILDCARD\" caseSensitive=\"false\">b*</api:value>\n" +
            "        </api:subSearch>\n" +
            "    </api:title>\n" +
            "</local:titleFacetList>");
        assertThat(list.facets).hasSize(2);
        assertThat(list.facets.get(0).getSubSearch()).isNotNull();
        assertThat(list.facets.get(1).getSubSearch()).isNotNull();

    }


    @Test
    public void testSubSearch() throws Exception {
        String example = "{\"value\":\"a*\"}";
        TitleSearch subSearch = new TitleSearch();
        subSearch.setValue(new ExtendedTextMatcher("a*"));
        TitleSearch search = Jackson2TestUtil.roundTripAndSimilarAndEquals(subSearch, example);
    }


}
