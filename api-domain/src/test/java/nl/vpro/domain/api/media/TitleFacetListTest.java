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
            """
                [ {
                  "max" : 11,
                  "sort" : "COUNT_DESC"
                }, {
                  "name" : "titlesWithA",
                  "subSearch" : {
                    "value" : "a*",
                    "matchType" : "WILDCARD",
                    "match" : "SHOULD"
                  }
                }, {
                  "name" : "titlesWithB",
                  "subSearch" : {
                    "value" : "b*",
                    "matchType" : "WILDCARD"
                  }
                } ]""");


        assertThat(rounded.facets).hasSize(2);
        assertThat(rounded.getMax()).isEqualTo(11);
        assertNotNull(rounded.facets.get(0).getSubSearch());
    }


    @Test
    public void testJsonBindingInForm() throws IOException {
        PageForm form = Jackson2Mapper.getLenientInstance().readValue("""
            {
              "highlight" : true,
              "searches" : {
                "text" : "woord",
                "sortDates" : [ ]
              },
              "mediaForm" : {
                "highlight" : false,
                "facets" : {
                  "avTypes" : {
                    "sort" : "COUNT"
                  },
                  "durations" : [ {
                    "name" : "0-5m",
                    "begin" : 1,
                    "end" : 300000,
                    "inclusiveEnd" : true
                  }, {
                    "name" : "5-10m",
                    "begin" : 300001,
                    "end" : 600000,
                    "inclusiveEnd" : true
                  }, {
                    "name" : "10m-30m",
                    "begin" : 600001,
                    "end" : 1800000,
                    "inclusiveEnd" : true
                  }, {
                    "name" : "30m-60m",
                    "begin" : 1800001,
                    "end" : 3600000,
                    "inclusiveEnd" : true
                  }, {
                    "name" : "60m-∞",
                    "begin" : 3600001,
                    "end" : 14400000,
                    "inclusiveEnd" : true
                  } ],
                  "descendantOf" : {
                    "sort" : "COUNT",
                    "subSearch" : {
                      "types" : [ "SERIES" ],
                      "match" : "MUST"
                    }
                  },
                  "tags" : {
                    "sort" : "COUNT",
                    "dynamic" : true
                  },
                  "titles" : {
                    "sort" : "COUNT"
                  },
                  "types" : {
                    "sort" : "COUNT"
                  }
                }
              },
              "facets" : {
                "sortDates" : [ "YEAR" ],
                "broadcasters" : {
                  "sort" : "COUNT"
                },
                "genres" : {
                  "sort" : "COUNT"
                }
              }
            }
            """, PageForm.class);
        TitleFacetList list = form.getMediaForm().getFacets().getTitles();
        assertThat(list.getFacets()).isNull();
        }


    @Test
    public void testJsonBindingBackwards() {
        TitleFacetList backwards = new TitleFacetList();
        backwards.setMax(11);
        backwards.setSort(FacetOrder.COUNT_DESC);
        backwards = Jackson2TestUtil.roundTripAndSimilar(backwards,
            """
                {
                  "max" : 11,
                  "sort" : "COUNT_DESC"
                }""");


        assertThat(backwards.facets).isNull();
        assertThat(backwards.getMax()).isEqualTo(11);

        TitleFacetList rounded = JAXBTestUtil.roundTripAndSimilar(backwards, """
            <local:titleFacetList sort="COUNT_DESC" xmlns:shared="urn:vpro:shared:2009" xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009" xmlns:local="uri:local">
                <api:max>11</api:max>
            </local:titleFacetList>""");

        assertThat(rounded.getFacets()).isNull();

    }

    @Test
    public void testXmlBinding() {

        TitleFacetList rounded = JAXBTestUtil.roundTripAndSimilar(list,
            """
                <local:titleFacetList sort="COUNT_DESC" xmlns:shared="urn:vpro:shared:2009" xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009" xmlns:local="uri:local">
                    <api:max>11</api:max>
                    <api:title name="titlesWithA">
                        <api:subSearch matchType="WILDCARD" match="SHOULD">a*</api:subSearch>
                    </api:title>
                    <api:title name="titlesWithB">
                        <api:subSearch matchType="WILDCARD">b*</api:subSearch>
                    </api:title>
                </local:titleFacetList>
                """);
        assertThat(rounded.facets).hasSize(2);
        assertThat(rounded.facets.get(0).getSubSearch()).isNotNull();
        assertThat(rounded.facets.get(1).getSubSearch()).isNotNull();

    }

    @Test
    public void testSubSearch() {
        String example = """
            {
              "value" : "a*"
            }""";
        TitleSearch subSearch = TitleSearch.builder().value("a*").build();
        TitleSearch search = Jackson2TestUtil.roundTripAndSimilarAndEquals(subSearch, example);
    }

    @Test
    public void testDeserializeJson() throws IOException {
        String example = """
             [
                        {
                            "name": "test",
                            "subSearch": {
                                "type": "MAIN",
                                "value": "x*",
                                "matchType": "WILDCARD"
                            }
                        }
                    ]\
            """;

        TitleFacetList actual = Jackson2Mapper.getInstance().readValue(example, TitleFacetList.class);

        assertThat(actual.getFacets().get(0).getSubSearch()).isNotNull();
        assertThat(actual.getSubSearch()).isNull();

    }


}
