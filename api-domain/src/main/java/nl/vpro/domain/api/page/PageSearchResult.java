package nl.vpro.domain.api.page;

import java.util.List;

import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.api.SearchResult;
import nl.vpro.domain.api.SearchResultItem;
import nl.vpro.domain.api.media.MediaFacetsResult;
import nl.vpro.domain.page.Page;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlRootElement(name = "pageSearchResult")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageSearchResultType", propOrder = {
    "facets",
    "selectedFacets",
    "mediaFacets",
    "mediaSelectedFacets"
})
@JsonPropertyOrder({"facets", "selectedFacets", "mediaFacets", "mediaSelectedFacets"})
public class PageSearchResult extends SearchResult<Page> {

    @XmlElement
    private PageFacetsResult facets;

    @XmlElement
    private PageFacetsResult selectedFacets;

    @XmlElement
    private MediaFacetsResult mediaFacets;

    @XmlElement
    private MediaFacetsResult mediaSelectedFacets;

    public PageSearchResult() {
    }

    public PageSearchResult(SearchResult<? extends Page> searchResultItems) {
        super(searchResultItems);
    }

    public PageSearchResult(PageSearchResult searchResultItems) {
        super(searchResultItems);
        facets = searchResultItems.getFacets();
        mediaFacets = searchResultItems.getMediaFacets();
    }

    public PageSearchResult(List<SearchResultItem<? extends Page>> searchResultItems, Long offset, Integer max, Total total) {
        super(searchResultItems, offset, max, total);
    }

    public PageSearchResult(List<SearchResultItem<? extends Page>> searchResultItems, PageFacetsResult facets, MediaFacetsResult mediaFacets, Long offset, Integer max, Total total) {
        super(searchResultItems, offset, max, total);
        this.facets = facets;
        this.mediaFacets = mediaFacets;
    }

    @Override
    public PageResult asResult() {
        return new PageResult(super.asResult());
    }

    public PageFacetsResult getFacets() {
        return facets;
    }

    public void setFacets(PageFacetsResult facets) {
        this.facets = facets;
    }

    public MediaFacetsResult getMediaFacets() {
        return mediaFacets;
    }

    public void setMediaFacets(MediaFacetsResult mediaFacets) {
        this.mediaFacets = mediaFacets;
    }

    public PageFacetsResult getSelectedFacets() {
        return selectedFacets;
    }

    public void setSelectedFacets(PageFacetsResult selectedFacets) {
        this.selectedFacets = selectedFacets;
    }

    public MediaFacetsResult getMediaSelectedFacets() {
        return mediaSelectedFacets;
    }

    public void setMediaSelectedFacets(MediaFacetsResult mediaSelectedFacets) {
        this.mediaSelectedFacets = mediaSelectedFacets;
    }

    @Override
    public String toString() {
        return super.toString() + " " + facets;
    }
}
