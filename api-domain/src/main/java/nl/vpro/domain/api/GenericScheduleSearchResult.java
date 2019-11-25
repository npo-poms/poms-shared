package nl.vpro.domain.api;

import java.util.List;

import javax.xml.bind.annotation.*;

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

    public GenericScheduleSearchResult(List<SearchResultItem<? extends S>> list, Long offset, Integer max, Long total, TotalQualifier totalQualifier) {
        super(list, offset, max, total, totalQualifier);
    }

    protected GenericScheduleSearchResult(SearchResult<? extends S> sr) {
        super(sr);
    }

    public GenericScheduleSearchResult(List<SearchResultItem<? extends S>> list, MediaFacetsResult facets, Long offset, Integer max, Long listSizes, TotalQualifier totalQualifier) {
        super(list, offset, max, listSizes, totalQualifier);
        this.facets = facets;
    }

    public MediaFacetsResult getFacets() {
        return facets;
    }

    public void setFacets(MediaFacetsResult facets) {
        this.facets = facets;
    }
}
