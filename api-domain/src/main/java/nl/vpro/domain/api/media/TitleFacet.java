package nl.vpro.domain.api.media;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.api.NameableSearchableFacet;
import nl.vpro.domain.api.TextFacet;
import nl.vpro.domain.media.MediaObject;


/**
 * @author Lies Kombrink
 * @since 5.5
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "mediaTitleFacetType", propOrder = {"name", "subSearch"})
@JsonPropertyOrder({"name","subSearch"})
public class TitleFacet extends TextFacet<MediaSearch, MediaObject> implements NameableSearchableFacet<MediaSearch, TitleSearch>  {

    private String name;

    @Valid
    // TODO 'subSearch'?
    private TitleSearch subSearch;

    public TitleFacet() {
        // These two fields are only present for backends compatibility. Them being filled would trigger backwards compatible
        // 'title facetting', which would be, by the way, a bit silly.
        // See
        setMax(null);
        setSort(null);
    }


    @Override
    public boolean hasSubSearch() {
        return subSearch != null && subSearch.hasSearches();
    }

    @Override
    @XmlElement
    public TitleSearch getSubSearch() {
        return subSearch;
    }

    @Override
    public void setSubSearch(TitleSearch subSearch) {
        this.subSearch = subSearch;
    }

    @XmlAttribute
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name + ":" + subSearch;

    }

}
