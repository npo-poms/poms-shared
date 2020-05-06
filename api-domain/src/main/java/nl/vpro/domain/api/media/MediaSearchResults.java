package nl.vpro.domain.api.media;

import java.util.List;

import nl.vpro.api.util.SearchResults;
import nl.vpro.domain.api.DateFacetResultItem;
import nl.vpro.domain.api.DurationFacetResultItem;
import nl.vpro.domain.api.MultipleFacetsResult;
import nl.vpro.domain.api.TermFacetResultItem;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class MediaSearchResults {

    private MediaSearchResults() {
    }

    public static void setSelectedFacets(MediaFacetsResult facetResults, MediaFacetsResult selected, MediaForm form) {

        MediaSearch search = form == null ? null : form.getSearches();


        if (facetResults == null || search == null || form.getFacets() == null) {
            return;
        }
        SearchResults.setSelected(search.getBroadcasters(), facetResults.getBroadcasters(), selected.getBroadcasters(search), TermFacetResultItem::new, "media.broadcaster");
        SearchResults.setSelected(search.getGenres(), facetResults.getGenres(), selected.getGenres(search), GenreFacetResultItem::new, "media.genres"/*, SearchResults::getGenreValue*/);
        SearchResults.setSelected(search.getDescendantOf(), facetResults.getDescendantOf(), selected.getDescendantOf(search), MemberRefFacetResultItem::new, "media.descendantOf");
        SearchResults.setSelected(search.getEpisodeOf(), facetResults.getEpisodeOf(), selected.getEpisodeOf(search), MemberRefFacetResultItem::new, "media.episodeOf");
        SearchResults.setSelected(search.getMemberOf(), facetResults.getMemberOf(), selected.getMemberOf(search), MemberRefFacetResultItem::new, "media.memberOf");
        SearchResults.setSelected(search.getTags(), facetResults.getTags(), selected.getTags(search), TermFacetResultItem::new, "media.tags");
        SearchResults.setSelected(search.getTypes(), facetResults.getTypes(), selected.getTypes(search), TermFacetResultItem::new, "media.types");
        SearchResults.setSelected(search.getAvTypes(), facetResults.getAvTypes(), selected.getAvTypes(search), TermFacetResultItem::new, "media.avtypes");
        SearchResults.setSelected(search.getAgeRatings(), facetResults.getAgeRatings(), selected.getAgeRating(search), TermFacetResultItem::new, "media.agerating");
        SearchResults.setSelected(search.getContentRatings(), facetResults.getContentRatings(), selected.getContentRatings(search), TermFacetResultItem::new, "media.contentrating");

        setSelectedRelationFacets(search.getRelations(), form.getFacets().getRelations(), facetResults.getRelations(), selected.getRelations(search));

        SearchResults.setSelectedDateFacets(search.getSortDates(), form.getFacets().getSortDates(), facetResults.getSortDates(), selected.getSortDates(search), DateFacetResultItem::new);

        SearchResults.setSelectedDurationsFacets(search.getDurations(), form.getFacets().getDurations(), facetResults.getDurations(), selected.getDurations(search), DurationFacetResultItem::new);

        setSelectedTitlesFacets(search.getTitles(), form.getFacets().getTitles(), facetResults.getTitles(), selected.getTitles(search));


    }

    public static void sortFacets(MediaFacetsResult facetResults, MediaFacetsResult selected, MediaForm form) {
        if (facetResults == null || form == null || form.getFacets() == null) {
            return;
        }
        SearchResults.sort(form.getFacets().getBroadcasters(), facetResults.getBroadcasters(), selected.getBroadcasters());
        SearchResults.sort(form.getFacets().getGenres(), facetResults.getGenres(), selected.getGenres());
        SearchResults.sort(form.getFacets().getDescendantOf(), facetResults.getDescendantOf(), selected.getDescendantOf());
        SearchResults.sort(form.getFacets().getEpisodeOf(), facetResults.getEpisodeOf(), selected.getEpisodeOf());
        SearchResults.sort(form.getFacets().getMemberOf(), facetResults.getMemberOf(), selected.getMemberOf());
        SearchResults.sort(form.getFacets().getTags(), facetResults.getTags(), selected.getTags());
        SearchResults.sort(form.getFacets().getTypes(), facetResults.getTypes(), selected.getTypes());
        SearchResults.sort(form.getFacets().getAvTypes(), facetResults.getAvTypes(), selected.getAvTypes());

        SearchResults.sortWithCustomComparator(form.getFacets().getAgeRatings(), facetResults.getAgeRatings(), selected.getAgeRatings(),
            (o1, o2) -> {
                try {
                    Integer id1 = Integer.parseInt(o1.getValue());
                    Integer id2 = Integer.parseInt(o2.getValue());
                    return id1.compareTo(id2);
                } catch (NumberFormatException ex) {
                    /* Ignore */
                }

                return o1.getValue().compareTo(o2.getValue());
            });

        SearchResults.sort(form.getFacets().getContentRatings(), facetResults.getContentRatings(), selected.getContentRatings());
    }


    public static void setSelectedRelationFacets(
        RelationSearchList searches,
        RelationFacetList facetList,
        List<MultipleFacetsResult> facetResultItems,
        List<MultipleFacetsResult> selected) {
        if (facetResultItems != null && searches != null) {
            facetList.getSubSearch(); // TODO

            for (RelationFacet facet : facetList) {
                SearchResults.setSelectedRelations(facet, searches, facetResultItems, selected);
            }
        }
    }


    public static void setSelectedTitlesFacets(
        List<TitleSearch> searches,
        TitleFacetList facetList,
        List<TermFacetResultItem> facetResultItems,
        List<TermFacetResultItem> selected) {
        if (facetResultItems != null && searches != null) {
            facetList.getSubSearch(); // TODO

            for (TitleFacet facet : facetList) {
                for (TermFacetResultItem facetResultItem : facetResultItems) {
                    if (facetResultItem.getId().equals(facet.getName())) {
                        for (TitleSearch search : searches) {
                            if (search.searchEquals(facet.getSubSearch())) {
                                facetResultItem.setSelected(true);
                                selected.add(facetResultItem);
                            }
                        }
                    }
                }
            }
        }
    }
}
