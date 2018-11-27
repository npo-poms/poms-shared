package nl.vpro.domain.api.page;

import java.util.List;

import nl.vpro.api.util.SearchResults;
import nl.vpro.domain.api.DateFacetResultItem;
import nl.vpro.domain.api.MultipleFacetsResult;
import nl.vpro.domain.api.TermFacetResultItem;

/**
 * @author Michiel Meeuwissen
 * @since 2.3
 */
public class PageSearchResults {


    /**
     * Marks the facet result items which are also searched on as 'selected'. This convenient, otherwise the client needs to
     * perform this kind of code.
     */
    public static void setSelectedFacets(PageFacetsResult facetResults, PageFacetsResult selected, PageForm form) {
        PageSearch search = form == null ? null : form.getSearches();
        if(search == null || facetResults == null || form.getFacets() == null) {
            return;
        }
        SearchResults.setSelected(search.getBroadcasters(), facetResults.getBroadcasters(), selected.getBroadcasters(form), TermFacetResultItem::new, "page.broadcasters");

        SearchResults.setSelected(search.getGenres(), facetResults.getGenres(), selected.getGenres(form), GenreFacetResultItem::new, "page.genres");

        SearchResults.setSelected(search.getTags(), facetResults.getTags(), selected.getTags(form), TermFacetResultItem::new, "page.tags");

        SearchResults.setSelected(search.getKeywords(), facetResults.getKeywords(), selected.getKeywords(form), TermFacetResultItem::new, "page.keywords");

        SearchResults.setSelected(search.getTypes(), facetResults.getTypes(), selected.getTypes(form), TermFacetResultItem::new, "page.types");

        SearchResults.setSelected(search.getPortals(), facetResults.getPortals(), selected.getPortals(form), TermFacetResultItem::new, "page.portals");

        SearchResults.setSelected(search.getSections(), facetResults.getSections(), selected.getSections(form), TermFacetResultItem::new, "page.sections");

        SearchResults.setSelectedDateFacets(search.getSortDates(), form.getFacets().getSortDates(), facetResults.getSortDates(), selected.getSortDates(form), DateFacetResultItem::new);
        if (form.getFacets() != null) {
            setSelected(search.getRelations(), form.getFacets().getRelations(), facetResults.getRelations(), selected.getRelations(form));
        }

    }

    public static void sortFacets(PageFacetsResult facetResults, PageFacetsResult selected, PageForm form) {
        if (facetResults == null || form == null) {
            return;
        }

        PageFacets facets = form.getFacets();
        if (facets == null) {
            return;
        }

        SearchResults.sort(facets.getBroadcasters(), facetResults.getBroadcasters(), selected.getBroadcasters());
        SearchResults.sort(facets.getGenres(), facetResults.getGenres(), selected.getGenres());
        SearchResults.sort(facets.getTags(), facetResults.getTags(), selected.getTags());
        SearchResults.sort(facets.getKeywords(), facetResults.getKeywords(), selected.getKeywords());
        SearchResults.sort(facets.getTypes(), facetResults.getTypes(), selected.getTypes());
        SearchResults.sort(facets.getPortals(), facetResults.getPortals(), selected.getPortals());
        SearchResults.sort(facets.getSections(), facetResults.getSections(), selected.getSections());
    }

    public static void setSelected(RelationSearchList searches, RelationFacetList facetList, List<MultipleFacetsResult> facetResultItems, List<MultipleFacetsResult> selected) {
        if (facetResultItems != null && searches != null) {
            facetList.getSubSearch(); // TODO

            for (RelationFacet facet : facetList) {
                SearchResults.setSelectedRelations(facet, searches, facetResultItems, selected);
            }
        }
    }

}
