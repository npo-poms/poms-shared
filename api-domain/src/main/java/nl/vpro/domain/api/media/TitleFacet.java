package nl.vpro.domain.api.media;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.api.NameableSearchableFacet;


/**
 * @author lies
 * @since 5.5
 */


@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "mediaRelationFacetType", propOrder = {"name", "subSearch"})
@JsonPropertyOrder({"threshold", "sort", "offset", "max", "name", "subSearch"})
public class TitleFacet extends ExtendedMediaFacet implements NameableSearchableFacet<TitleSearch> {

    private String name;

    @Valid
    private TitleSearch subSearch;

    public TitleFacet() {
    }

    public TitleFacet(Integer threshold, FacetOrder sort, Integer max) {
        super(threshold, sort, max);
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
}
