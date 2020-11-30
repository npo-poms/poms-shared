package nl.vpro.domain.api.media;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.*;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RelationFacetListTest {

    @Test
    public void testJaxbBinding() {
        RelationSearch subSearch = new RelationSearch();
        subSearch.setBroadcasters(new TextMatcherList(new TextMatcher("VPRO")));
        RelationFacet facet = new RelationFacet();
        facet.setSubSearch(subSearch);

        RelationFacetList list = new RelationFacetList(Collections.singletonList(facet));

        list = JAXBTestUtil.roundTripAndSimilar(list, "<local:relationFacetList xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
            "    <api:facet sort=\"VALUE_ASC\">\n" +
            "        <api:max>24</api:max>\n" +
            "        <api:subSearch>\n" +
            "            <api:broadcasters match=\"MUST\">\n" +
            "                <api:matcher>VPRO</api:matcher>\n" +
            "            </api:broadcasters>\n" +
            "        </api:subSearch>\n" +
            "    </api:facet>\n" +
            "</local:relationFacetList>");

        assertThat(list.facets).isNotEmpty();
        assertNotNull(list.facets.get(0).getSubSearch());
    }

    @Test
    public void testJsonBinding() {
        RelationSearch subSearch = new RelationSearch();
        subSearch.setBroadcasters(new TextMatcherList(new TextMatcher("VPRO")));
        RelationFacet facet = new RelationFacet();
        facet.setName("myrelation");
        facet.setSubSearch(subSearch);

        RelationFacetList list = new RelationFacetList(Collections.singletonList(facet));

        list = Jackson2TestUtil.roundTripAndSimilar(list, "{\"sort\":\"VALUE_ASC\",\"max\":24,\"name\":\"myrelation\",\"subSearch\":{\"broadcasters\":\"VPRO\"}}");

        assertThat(list.facets).hasSize(1);
        assertNotNull(list.facets.get(0).getSubSearch());
    }

    @Test
    public void testJsonBindingList() throws Exception {
        RelationSearch subSearch = new RelationSearch();
        subSearch.setBroadcasters(new TextMatcherList(new TextMatcher("VPRO")));

        RelationFacet facet = new RelationFacet();
        facet.setName("myrelation");
        facet.setSubSearch(subSearch);
        facet.setThreshold(2);

        RelationFacet facet2 = new RelationFacet();
        facet2.setName("myrelation2");
        facet2.setSubSearch(subSearch);

        RelationFacetList list = new RelationFacetList(Arrays.asList(facet, facet2));

        String expected = "[{\"threshold\":2,\"sort\":\"VALUE_ASC\",\"max\":24,\"name\":\"myrelation\",\"subSearch\":{\"broadcasters\":\"VPRO\"}},{\"sort\":\"VALUE_ASC\",\"max\":24,\"name\":\"myrelation2\",\"subSearch\":{\"broadcasters\":\"VPRO\"}}]";
        String actual  = Jackson2Mapper.getInstance().writeValueAsString(list);
        Jackson2TestUtil.assertThatJson(actual).isSimilarTo(expected);

        list = Jackson2Mapper.getInstance().readValue(new StringReader(actual), RelationFacetList.class);

        assertThat(list.facets).hasSize(2);
        assertNotNull(list.facets.get(0).getSubSearch());
    }

    @Test
    public void testJsonBindingListWithFilter() throws Exception {
        RelationSearch subSearch = new RelationSearch();
        subSearch.setBroadcasters(new TextMatcherList(new TextMatcher("VPRO")));

        RelationFacet facet = new RelationFacet();
        facet.setName("myrelation");
        facet.setSubSearch(subSearch);
        facet.setThreshold(0);

        RelationFacet facet2 = new RelationFacet();
        facet2.setName("myrelation2");
        facet2.setSubSearch(subSearch);
        facet2.setThreshold(3);

        RelationFacetList list = new RelationFacetList(Arrays.asList(facet, facet2));

        list.setFilter(new MediaSearch());

        String expected = "{\"value\":[{\"threshold\":0,\"sort\":\"VALUE_ASC\",\"max\":24,\"name\":\"myrelation\",\"subSearch\":{\"broadcasters\":\"VPRO\"}},{\"threshold\":3,\"sort\":\"VALUE_ASC\",\"max\":24,\"name\":\"myrelation2\",\"subSearch\":{\"broadcasters\":\"VPRO\"}}],\"filter\":{}}";
        String actual = Jackson2Mapper.getInstance().writeValueAsString(list);
        Jackson2TestUtil.assertThatJson(actual).isSimilarTo(expected);

        list = Jackson2Mapper.getInstance().readValue(new StringReader(actual), RelationFacetList.class);

        assertThat(list.facets).hasSize(2);
        assertThat(list.getFilter()).isNotNull();
        assertNotNull(list.facets.get(0).getSubSearch());
    }


    @Test
    public void testSubSearch() {
        String example = "{\"broadcasters\":[\"VPRO\",{\"value\":\"EO\",\"match\":\"NOT\"}]}";
        RelationSearch subSearch = new RelationSearch();
        subSearch.setBroadcasters(new TextMatcherList(new TextMatcher("VPRO"), new TextMatcher("EO", Match.NOT)));
        RelationSearch search = Jackson2TestUtil.roundTripAndSimilarAndEquals(subSearch, example);
    }


    @Test
    public void testRelationFacet() {
        String example = "{\"threshold\":0,\"sort\":\"VALUE_ASC\",\"max\":24,\"name\":\"myrelation\",\"subSearch\":{\"broadcasters\":[\"VPRO\",{\"value\":\"EO\",\"match\":\"NOT\"}]}}";
        RelationFacet facet  = new RelationFacet();
        facet.setThreshold(0);
        facet.setName("myrelation");
        RelationSearch subSearch = new RelationSearch();
        subSearch.setBroadcasters(new TextMatcherList(new TextMatcher("VPRO"), new TextMatcher("EO", Match.NOT)));
        facet.setSubSearch(subSearch);
        RelationFacet rounded = Jackson2TestUtil.roundTripAndSimilarAndEquals(facet, example);
    }

    @Test
    public void testJsonBindingListWithSubSearch() throws Exception {
        RelationSearch subSearch = new RelationSearch();
        subSearch.setBroadcasters(new TextMatcherList(new TextMatcher("VPRO"), new TextMatcher("EO", Match.NOT)));

        RelationFacet facet = new RelationFacet();
        facet.setName("myrelation");
        facet.setSubSearch(subSearch);
        facet.setThreshold(0);

        RelationFacetList list = new RelationFacetList(Collections.singletonList(facet));
        RelationSearch listSubSearch  = new RelationSearch();
        listSubSearch.setBroadcasters(new TextMatcherList(new TextMatcher("VPRO",Match.SHOULD), new TextMatcher("EO", Match.NOT)));
        list.setSubSearch(listSubSearch);

        String expected = "{\n" +
            "  \"value\" : {\n" +
            "    \"threshold\" : 0,\n" +
            "    \"sort\" : \"VALUE_ASC\",\n" +
            "    \"max\" : 24,\n" +
            "    \"name\" : \"myrelation\",\n" +
            "    \"subSearch\" : {\n" +
            "      \"broadcasters\" : [ \"VPRO\", {\n" +
            "        \"value\" : \"EO\",\n" +
            "        \"match\" : \"NOT\"\n" +
            "      } ]\n" +
            "    }\n" +
            "  },\n" +
            "  \"subSearch\" : {\n" +
            "    \"broadcasters\" : [ {\n" +
            "      \"value\" : \"VPRO\",\n" +
            "      \"match\" : \"SHOULD\"\n" +
            "    }, {\n" +
            "      \"value\" : \"EO\",\n" +
            "      \"match\" : \"NOT\"\n" +
            "    } ]\n" +
            "  }\n" +
            "}";
        String actual = Jackson2Mapper.getInstance().writeValueAsString(list);
        Jackson2TestUtil.assertThatJson(actual).isSimilarTo(expected);

        list = Jackson2Mapper.getInstance().readValue(new StringReader(actual), RelationFacetList.class);

        assertThat(list.facets).hasSize(1);
        assertThat(list.getFilter()).isNull();
        assertNotNull(list.getSubSearch());
        assertThat(list.getSubSearch().getBroadcasters().get(0).getValue()).isEqualTo("VPRO");
        assertThat(list.getSubSearch().getBroadcasters().get(0).getMatch()).isEqualTo(Match.SHOULD);
        assertThat(list.getSubSearch().getBroadcasters().get(1).getValue()).isEqualTo("EO");
        assertThat(list.getSubSearch().getBroadcasters().get(1).getMatch()).isEqualTo(Match.NOT);
        assertNotNull(list.getFacets().get(0).getSubSearch());
        assertThat(list.getFacets().get(0).getSubSearch().getBroadcasters().get(0).getValue()).isEqualTo("VPRO");
        assertThat(list.getFacets().get(0).getSubSearch().getBroadcasters().get(0).getMatch()).isEqualTo(Match.MUST);
        assertThat(list.getFacets().get(0).getSubSearch().getBroadcasters().get(1).getValue()).isEqualTo("EO");
        assertThat(list.getFacets().get(0).getSubSearch().getBroadcasters().get(1).getMatch()).isEqualTo(Match.NOT);
    }
}
