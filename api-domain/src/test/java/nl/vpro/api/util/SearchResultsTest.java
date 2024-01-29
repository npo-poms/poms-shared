package nl.vpro.api.util;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.*;
import nl.vpro.domain.api.media.*;
import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.MediaClassificationService;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.jackson2.Jackson2Mapper;

import static org.assertj.core.api.Assertions.assertThat;


public class SearchResultsTest {

    @BeforeAll
    public static void init() {
        ClassificationServiceLocator.setInstance(MediaClassificationService.getInstance());
    }

    @Test
    public void testSetSelectedTermFacet() {

        TextMatcherList searches = new TextMatcherList();
        searches.asList().add(new TextMatcher("ID"));
        searches.asList().add(new TextMatcher("MATCHESNOTHING"));

        List<TermFacetResultItem> facetResultItems = new ArrayList<>();
        facetResultItems.add(new TermFacetResultItem("Id", "ID", 10));
        facetResultItems.add(new TermFacetResultItem("AnotherId", "ANOTHERID", 5));

        List<TermFacetResultItem> selected = new ArrayList<>();
        SearchResults.setSelected(searches, facetResultItems, selected, TermFacetResultItem::new, "cache");

        assertThat(facetResultItems).hasSize(2);
        assertThat(facetResultItems.get(0).getId()).isEqualTo("ID");
        assertThat(facetResultItems.get(0).isSelected()).isTrue();
        assertThat(facetResultItems.get(1).getId()).isEqualTo("ANOTHERID");
        assertThat(facetResultItems.get(1).isSelected()).isFalse();

        assertThat(selected).hasSize(2);
        assertThat(selected.get(0).getId()).isEqualTo("ID");
        assertThat(selected.get(1).getId()).isEqualTo("MATCHESNOTHING");
        assertThat(selected.get(1).getCount()).isEqualTo(0);
    }

    @Test
    public void testSetSelectedDateFacet() {
        DateRangeMatcherList searches = new DateRangeMatcherList();
        searches.asList().add(new DateRangeMatcher(Instant.ofEpochMilli(0), Instant.ofEpochMilli(100000)));
        searches.asList().add(new DateRangeMatcher(null, Instant.ofEpochMilli(1000000)));

        List<DateFacetResultItem> facetResultItems = new ArrayList<>();
        facetResultItems.add(DateFacetResultItem.builder().value("range1").begin(Instant.EPOCH).end(Instant.ofEpochMilli(100000)).count(100).build());
        facetResultItems.add(DateFacetResultItem.builder().value("range2").begin(Instant.ofEpochMilli(100000)).end(Instant.ofEpochMilli(2000000)).count(50).build());
        List<DateFacetResultItem> selected = new ArrayList<>();

        SearchResults.setSelectedDateFacets(searches, new DateRangeFacets(), facetResultItems, selected, DateFacetResultItem::new);

        assertThat(facetResultItems).hasSize(2);
        assertThat(facetResultItems.get(0).isSelected()).isTrue();
        assertThat(facetResultItems.get(1).isSelected()).isFalse();

        assertThat(selected).hasSize(2);
        assertThat(selected.get(0).isSelected()).isTrue();
        assertThat(selected.get(0).getCount()).isEqualTo(100);
        assertThat(selected.get(0).getValue()).isEqualTo("range1");
        assertThat(selected.get(1).isSelected()).isTrue();
        assertThat(selected.get(1).getValue()).isNull();
        assertThat(selected.get(1).getCount()).isEqualTo(0);
    }

    @Test
    public void testSetSelectedRelationFacet() {

        RelationSearchList searches = new RelationSearchList();
        RelationSearch search1 = new RelationSearch();
        search1.setBroadcasters(new TextMatcherList(new TextMatcher("VPRO")));
        search1.setTypes(new TextMatcherList(new TextMatcher("LABEL")));
        search1.setValues(new ExtendedTextMatcherList(new ExtendedTextMatcher("Een label")));
        searches.asList().add(search1);

        RelationSearch search2 = new RelationSearch();
        search2.setBroadcasters(new TextMatcherList(new TextMatcher("VPRO")));
        search2.setTypes(new TextMatcherList(new TextMatcher("ARTIST")));
        search2.setValues(new ExtendedTextMatcherList(new ExtendedTextMatcher("Een artiest")));
        searches.asList().add(search2);

        RelationSearch search3 = new RelationSearch();
        search3.setBroadcasters(new TextMatcherList(new TextMatcher("VPRO")));
        search3.setTypes(new TextMatcherList(new TextMatcher("LABEL")));
        search3.setValues(new ExtendedTextMatcherList(new ExtendedTextMatcher("Nog een label")));
        searches.asList().add(search3);


        List<MultipleFacetsResult> facetResultItems = new ArrayList<>();
        MultipleFacetsResult result = new MultipleFacetsResult();
        result.setName("labels");
        List<TermFacetResultItem> labels = new ArrayList<>();
        labels.add(new TermFacetResultItem("VALUE", "LABEL1", 10));
        labels.add(new TermFacetResultItem("VALUE", "Een label", 5));
        result.setFacets(labels);
        facetResultItems.add(result);


        RelationFacetList relationFacetList  = new RelationFacetList();
        RelationFacet facet = new RelationFacet();
        facet.setName("labels");
        RelationSearch facetSearch = new RelationSearch();
        facet.setSubSearch(facetSearch);
        facetSearch.setBroadcasters(new TextMatcherList(new TextMatcher("VPRO")));
        facetSearch.setTypes(new TextMatcherList(new TextMatcher("LABEL")));
        relationFacetList.setFacets(new ArrayList<>());
        relationFacetList.getFacets().add(facet);


        List<MultipleFacetsResult> selected = new ArrayList<>();
        MediaSearchResults.setSelectedRelationFacets(searches, relationFacetList, facetResultItems, selected);

        assertThat(facetResultItems.size()).isEqualTo(1);
        assertThat(facetResultItems.get(0).getName()).isEqualTo("labels");
        assertThat(facetResultItems.get(0).getFacets().size()).isEqualTo(2);
        assertThat(facetResultItems.get(0).getFacets().get(0).getId()).isEqualTo("LABEL1");
        assertThat(facetResultItems.get(0).getFacets().get(0).getCount()).isEqualTo(10);
        assertThat(facetResultItems.get(0).getFacets().get(0).isSelected()).isFalse();
        assertThat(facetResultItems.get(0).getFacets().get(1).getId()).isEqualTo("Een label");
        assertThat(facetResultItems.get(0).getFacets().get(1).getCount()).isEqualTo(5);
        assertThat(facetResultItems.get(0).getFacets().get(1).isSelected()).isTrue();


        assertThat(selected.get(0).getName()).isEqualTo("labels");
        assertThat(selected.get(0).getFacets().size()).isEqualTo(2);

        assertThat(selected.get(0).getFacets().get(0).getId()).isEqualTo("Een label");
        assertThat(selected.get(0).getFacets().get(0).getCount()).isEqualTo(5);
        assertThat(selected.get(0).getFacets().get(1).getId()).isEqualTo("Nog een label");
        assertThat(selected.get(0).getFacets().get(1).getCount()).isEqualTo(0);
    }

    @Test
    public void unmarshalJson() throws IOException {
        MediaSearchResult result = Jackson2Mapper.getInstance().readValue(getClass().getResource("/related.json"), MediaSearchResult.class);
        MediaObject o = result.asList().get(0);
        assertThat(o.getDescendantOf().iterator().next().getMidRef()).isEqualTo("VPRO_1154287");
    }

    @Test
    public void unmarshalXml() {
        MediaSearchResult result = JAXB.unmarshal(getClass().getResource("/related.xml"), MediaSearchResult.class);
        MediaObject o = result.asList().get(0);
        assertThat(o.getDescendantOf().iterator().next().getMidRef()).isEqualTo("VPRO_1154287");
    }
}
