package nl.vpro.domain.api;

import java.util.List;

import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.api.media.MediaFacetsResult;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "genericScheduleSearchResultType", propOrder = {"facets"})
public class GenericScheduleSearchResult<S extends ApiScheduleEvent> extends SearchResult<S> {

    @XmlElement
    private MediaFacetsResult facets;

    public GenericScheduleSearchResult() {
    }

    public GenericScheduleSearchResult(List<SearchResultItem<? extends S>> list, Long offset, Integer max, Total total) {
        super(list, offset, max, total);
    }

    protected GenericScheduleSearchResult(SearchResult<? extends S> sr) {
        super(sr);
    }

    public GenericScheduleSearchResult(List<SearchResultItem<? extends S>> list, MediaFacetsResult facets, Long offset, Integer max, Total total) {
        super(list, offset, max, total);
        this.facets = facets;
    }

    public MediaFacetsResult getFacets() {
        return facets;
    }

    public void setFacets(MediaFacetsResult facets) {
        this.facets = facets;
    }
}
