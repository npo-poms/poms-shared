package nl.vpro.domain.api.media;

import java.util.Collections;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.GenericMediaSearchResult;
import nl.vpro.domain.api.SearchResult;
import nl.vpro.domain.api.SearchResultItem;
import nl.vpro.domain.media.MediaObject;


/**
 * Exists only because of <a href="https://jira.vpro.nl/browse/API-118">that generics dont' work  otherwise?</a>
 *
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "mediaSearchResult")
@XmlType(name = "mediaSearchResultType")
public class MediaSearchResult extends GenericMediaSearchResult<MediaObject> {

    public MediaSearchResult() {
    }

    public static MediaSearchResult emptyResult(Long offset, Integer max) {
        return new MediaSearchResult(Collections.emptyList(), offset, max, Total.EMPTY);
    }

    public MediaSearchResult(List<SearchResultItem<? extends MediaObject>> list, Long offset, Integer max, Total total) {
        super(list, offset, max, total);
    }

    public MediaSearchResult(List<SearchResultItem<? extends MediaObject>> list, MediaFacetsResult facetsResult, Long offset, Integer max, Total total) {
        super(list, facetsResult, offset, max, total);
    }

    public MediaSearchResult(SearchResult<? extends MediaObject> sr) {
        super(sr);
    }

    public MediaSearchResult(GenericMediaSearchResult<? extends MediaObject> sr, TotalQualifier totalQualifier) {
        super(sr);
        setFacets(sr.getFacets());
        setSelectedFacets(sr.getSelectedFacets());
        this.totalQualifier = totalQualifier;
    }
    public MediaSearchResult(GenericMediaSearchResult<? extends MediaObject> sr) {
        this(sr, sr.getTotalQualifier());
    }


    @Override
    public MediaResult asResult() {
        return new MediaResult(super.asResult());
    }
}
