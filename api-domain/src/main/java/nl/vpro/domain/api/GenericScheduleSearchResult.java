package nl.vpro.domain.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

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

    public GenericScheduleSearchResult(List<SearchResultItem<? extends S>> list, Long offset, Integer max, long total) {
        super(list, offset, max, total);
    }

    protected GenericScheduleSearchResult(SearchResult<? extends S> sr) {
        super(sr);
    }

    public GenericScheduleSearchResult(List<SearchResultItem<? extends S>> list, MediaFacetsResult facets, Long offset, Integer max, long listSizes) {
        super(list, offset, max, listSizes);
        this.facets = facets;
    }

    public MediaFacetsResult getFacets() {
        return facets;
    }

    public void setFacets(MediaFacetsResult facets) {
        this.facets = facets;
    }
}
