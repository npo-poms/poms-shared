package nl.vpro.domain.api.media;

import java.util.Collections;

import org.junit.Test;

import nl.vpro.domain.api.ExtendedTextMatcher;
import nl.vpro.domain.api.Match;
import nl.vpro.domain.api.TextMatcher;
import nl.vpro.domain.api.TextMatcherList;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class TitleFacetListTest {


    @Test
    public void testJsonBinding() throws Exception {
        TitleSearch subSearch = new TitleSearch();
        subSearch.setValue(new ExtendedTextMatcher("a*"));
        TitleFacet facet = new TitleFacet();
        facet.setName("titlesWithA");
        facet.setSubSearch(subSearch);

        TitleFacetList list = new TitleFacetList(Collections.singletonList(facet));

        list = Jackson2TestUtil.roundTripAndSimilar(list, "{\"sort\":\"VALUE_ASC\",\"max\":24,\"name\":\"titlesWithA\",\"subSearch\":{\"value\":\"a\"}}");

        assertThat(list.facets).hasSize(1);
        assertTrue(list.facets.get(0).getSubSearch() != null);
    }


    @Test
    public void testSubSearch() throws Exception {
        String example = "{\"value\":\"a*\"}";
        TitleSearch subSearch = new TitleSearch();
        subSearch.setValue(new ExtendedTextMatcher("a*"));
        TitleSearch search = Jackson2TestUtil.roundTripAndSimilarAndEquals(subSearch, example);
    }


    @Test
    public void testTitleFacet() throws Exception {
        String example = "{\"threshold\":0,\"sort\":\"VALUE_ASC\",\"max\":24,\"name\":\"titles\",\"subSearch\":{\"broadcasters\":[\"VPRO\",{\"value\":\"WHATSON\",\"match\":\"not\"}]}}";
        RelationFacet facet  = new RelationFacet();
        facet.setThreshold(0);
        facet.setName("myrelation");
        RelationSearch subSearch = new RelationSearch();
        subSearch.setBroadcasters(new TextMatcherList(new TextMatcher("VPRO"), new TextMatcher("EO", Match.NOT)));
        facet.setSubSearch(subSearch);
        RelationFacet rounded = Jackson2TestUtil.roundTripAndSimilarAndEquals(facet, example);
    }

}
