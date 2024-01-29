package nl.vpro.domain.api;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.api.media.MediaFacetsResult;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "genericMediaSearchResultType", propOrder = {"facets", "selectedFacets"})
@JsonPropertyOrder({"facets", "selectedFacets"})
public class GenericMediaSearchResult<S extends MediaObject> extends SearchResult<S> {

    @XmlElement
    private MediaFacetsResult facets;

    @XmlElement
    private MediaFacetsResult selectedFacets;

    public GenericMediaSearchResult() {
    }

    public GenericMediaSearchResult(List<SearchResultItem<? extends S>> list, Long offset, Integer max, Total total) {
        super(list, offset, max, total);
    }

    protected GenericMediaSearchResult(SearchResult<? extends S> sr) {
        super(sr);
    }

    public GenericMediaSearchResult(List<SearchResultItem<? extends S>> list, MediaFacetsResult facets, Long offset, Integer max,  Total total) {
        super(list, offset, max, total);
        this.facets = facets;
    }

    public MediaFacetsResult getFacets() {
        return facets;
    }

    public void setFacets(MediaFacetsResult facets) {
        this.facets = facets;
    }

    public MediaFacetsResult getSelectedFacets() {
        return selectedFacets;
    }

    protected MediaFacetsResult getSelectedFacetsNotNull() {
        if (selectedFacets == null) {
            selectedFacets = new MediaFacetsResult();
        }
        return selectedFacets;
    }

    public void setSelectedFacets(MediaFacetsResult selected) {
        this.selectedFacets = selected;
    }



}
