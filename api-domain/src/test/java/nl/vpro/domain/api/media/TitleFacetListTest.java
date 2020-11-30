package nl.vpro.domain.api.media;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.*;
import nl.vpro.domain.api.page.PageForm;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class TitleFacetListTest {
    TitleFacetList list;

    {
        TitleFacet facet1 = new TitleFacet();
        {
            TitleSearch subSearch = TitleSearch.builder()
                .value("a*")
                .match(Match.SHOULD)
                .matchType(StandardMatchType.WILDCARD)
                .caseSensitive(false)
                .build();

            facet1.setName("titlesWithA");
            facet1.setSubSearch(subSearch);
        }
        TitleFacet facet2 = new TitleFacet();
        {
            TitleSearch subSearch = TitleSearch.builder()
                .value("b*")
                .match(Match.MUST)
                .matchType(StandardMatchType.WILDCARD)
                .caseSensitive(false)
                .build();

            facet2.setName("titlesWithB");
            facet2.setSubSearch(subSearch);
        }
        list = new TitleFacetList(Arrays.asList(facet1, facet2));
        list.setMax(11);
        list.setSort(FacetOrder.COUNT_DESC);
    }

    @Test
    public void testJsonBinding() {


        TitleFacetList rounded = Jackson2TestUtil.roundTripAndSimilar(list,
            "[ {\n" +
                "  \"max\" : 11,\n" +
                "  \"sort\" : \"COUNT_DESC\"\n" +
                "}, {\n" +
                "  \"name\" : \"titlesWithA\",\n" +
                "  \"subSearch\" : {\n" +
                "    \"value\" : \"a*\",\n" +
                "    \"matchType\" : \"WILDCARD\",\n" +
                "    \"match\" : \"SHOULD\"\n" +
                "  }\n" +
                "}, {\n" +
                "  \"name\" : \"titlesWithB\",\n" +
                "  \"subSearch\" : {\n" +
                "    \"value\" : \"b*\",\n" +
                "    \"matchType\" : \"WILDCARD\"\n" +
                "  }\n" +
                "} ]");


        assertThat(rounded.facets).hasSize(2);
        assertThat(rounded.getMax()).isEqualTo(11);
        assertNotNull(rounded.facets.get(0).getSubSearch());
    }


    @Test
    public void testJsonBindingInForm() throws IOException {
        PageForm form = Jackson2Mapper.getLenientInstance().readValue("{\n" +
            "  \"highlight\" : true,\n" +
            "  \"searches\" : {\n" +
            "    \"text\" : \"woord\",\n" +
            "    \"sortDates\" : [ ]\n" +
            "  },\n" +
            "  \"mediaForm\" : {\n" +
            "    \"highlight\" : false,\n" +
            "    \"facets\" : {\n" +
            "      \"avTypes\" : {\n" +
            "        \"sort\" : \"COUNT\"\n" +
            "      },\n" +
            "      \"durations\" : [ {\n" +
            "        \"name\" : \"0-5m\",\n" +
            "        \"begin\" : 1,\n" +
            "        \"end\" : 300000,\n" +
            "        \"inclusiveEnd\" : true\n" +
            "      }, {\n" +
            "        \"name\" : \"5-10m\",\n" +
            "        \"begin\" : 300001,\n" +
            "        \"end\" : 600000,\n" +
            "        \"inclusiveEnd\" : true\n" +
            "      }, {\n" +
            "        \"name\" : \"10m-30m\",\n" +
            "        \"begin\" : 600001,\n" +
            "        \"end\" : 1800000,\n" +
            "        \"inclusiveEnd\" : true\n" +
            "      }, {\n" +
            "        \"name\" : \"30m-60m\",\n" +
            "        \"begin\" : 1800001,\n" +
            "        \"end\" : 3600000,\n" +
            "        \"inclusiveEnd\" : true\n" +
            "      }, {\n" +
            "        \"name\" : \"60m-∞\",\n" +
            "        \"begin\" : 3600001,\n" +
            "        \"end\" : 14400000,\n" +
            "        \"inclusiveEnd\" : true\n" +
            "      } ],\n" +
            "      \"descendantOf\" : {\n" +
            "        \"sort\" : \"COUNT\",\n" +
            "        \"subSearch\" : {\n" +
            "          \"types\" : [ \"SERIES\" ],\n" +
            "          \"match\" : \"MUST\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"tags\" : {\n" +
            "        \"sort\" : \"COUNT\",\n" +
            "        \"dynamic\" : true\n" +
            "      },\n" +
            "      \"titles\" : {\n" +
            "        \"sort\" : \"COUNT\"\n" +
            "      },\n" +
            "      \"types\" : {\n" +
            "        \"sort\" : \"COUNT\"\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"facets\" : {\n" +
            "    \"sortDates\" : [ \"YEAR\" ],\n" +
            "    \"broadcasters\" : {\n" +
            "      \"sort\" : \"COUNT\"\n" +
            "    },\n" +
            "    \"genres\" : {\n" +
            "      \"sort\" : \"COUNT\"\n" +
            "    }\n" +
            "  }\n" +
            "}\n", PageForm.class);
        TitleFacetList list = form.getMediaForm().getFacets().getTitles();
        assertThat(list.getFacets()).isNull();
        }


    @Test
    public void testJsonBindingBackwards() {
        TitleFacetList backwards = new TitleFacetList();
        backwards.setMax(11);
        backwards.setSort(FacetOrder.COUNT_DESC);
        backwards = Jackson2TestUtil.roundTripAndSimilar(backwards,
            "{\n" +
                "  \"max\" : 11,\n" +
                "  \"sort\" : \"COUNT_DESC\"\n" +
                "}");


        assertThat(backwards.facets).isNull();
        assertThat(backwards.getMax()).isEqualTo(11);

        TitleFacetList rounded = JAXBTestUtil.roundTripAndSimilar(backwards, "<local:titleFacetList sort=\"COUNT_DESC\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
            "    <api:max>11</api:max>\n" +
            "</local:titleFacetList>");

        assertThat(rounded.getFacets()).isNull();

    }

    @Test
    public void testXmlBinding() {

        TitleFacetList rounded = JAXBTestUtil.roundTripAndSimilar(list,
            "<local:titleFacetList sort=\"COUNT_DESC\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:max>11</api:max>\n" +
                "    <api:title name=\"titlesWithA\">\n" +
                "        <api:subSearch matchType=\"WILDCARD\" match=\"SHOULD\">a*</api:subSearch>\n" +
                "    </api:title>\n" +
                "    <api:title name=\"titlesWithB\">\n" +
                "        <api:subSearch matchType=\"WILDCARD\">b*</api:subSearch>\n" +
                "    </api:title>\n" +
                "</local:titleFacetList>\n");
        assertThat(rounded.facets).hasSize(2);
        assertThat(rounded.facets.get(0).getSubSearch()).isNotNull();
        assertThat(rounded.facets.get(1).getSubSearch()).isNotNull();

    }

    @Test
    public void testSubSearch() {
        String example = "{\n" +
            "  \"value\" : \"a*\"\n" +
            "}";
        TitleSearch subSearch = TitleSearch.builder().value("a*").build();
        TitleSearch search = Jackson2TestUtil.roundTripAndSimilarAndEquals(subSearch, example);
    }

    @Test
    public void testDeserializeJson() throws IOException {
        String example = " [\n" +
            "            {\n" +
            "                \"name\": \"test\",\n" +
            "                \"subSearch\": {\n" +
            "                    \"type\": \"MAIN\",\n" +
            "                    \"value\": \"x*\",\n" +
            "                    \"matchType\": \"WILDCARD\"\n" +
            "                }\n" +
            "            }\n" +
            "        ]";

        TitleFacetList actual = Jackson2Mapper.getInstance().readValue(example, TitleFacetList.class);

        assertThat(actual.getFacets().get(0).getSubSearch()).isNotNull();
        assertThat(actual.getSubSearch()).isNull();

    }


}
