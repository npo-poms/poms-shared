package nl.vpro.domain.api;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import nl.vpro.domain.api.media.MediaFacetsResult;
import nl.vpro.domain.api.media.MediaSearchResult;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

public class MediaSearchResultTest {

    @Test
    public void toJson() {
        List<SearchResultItem<? extends MediaObject>> list = Collections.emptyList();
        MediaSearchResult result = new MediaSearchResult(list, 0L, 10, 0L, Result.TotalQualifier.EQUAL_TO);
        MediaFacetsResult facets = new MediaFacetsResult();
        facets.setAgeRatings(new ArrayList<>());
        result.setFacets(facets);
        MediaSearchResult rounded = Jackson2TestUtil.roundTripAndSimilar(result, "{\n" +
            "  \"facets\" : {\n" +
            "  },\n" +
            "  \"total\" : 0,\n" +
            "  \"offset\" : 0,\n" +
            "  \"max\" : 10,\n" +
            "  \"items\" : [ ]\n" +
            "}");
    }


    @Test
    public void toXml() {
        List<SearchResultItem<? extends MediaObject>> list = Collections.emptyList();
        MediaSearchResult result = new MediaSearchResult(list, 0L, 10, 0L, Result.TotalQualifier.EQUAL_TO);
        MediaFacetsResult facets = new MediaFacetsResult();
        facets.setAgeRatings(new ArrayList<>());
        result.setFacets(facets);
        MediaSearchResult rounded = JAXBTestUtil.roundTripAndSimilar(result, "<api:mediaSearchResult total=\"0\" offset=\"0\" max=\"10\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <api:items/>\n" +
            "    <api:facets/>\n" +
            "</api:mediaSearchResult>");

        //assertThat(rounded.getFacets().getAgeRatings()).isNotNull();

    }
}
