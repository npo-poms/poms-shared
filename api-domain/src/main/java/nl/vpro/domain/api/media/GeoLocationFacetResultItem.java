package nl.vpro.domain.api.media;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.api.TermFacetResultItem;
import nl.vpro.domain.classification.bind.TermWrapper;


/**
 * @author Michiel Meeuwissen
 * @since 3.4
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaGeoLocationFacetResultItemType")
public class GeoLocationFacetResultItem extends TermFacetResultItem {


    @XmlElement(name = "term")
    @JsonProperty("terms")
    private List<TermWrapper> terms;

    public GeoLocationFacetResultItem() {
    }

    public GeoLocationFacetResultItem(List<TermWrapper> terms, String value, String id, long count) {
        super(value, id, count);
        this.terms = terms;
    }

    public List<TermWrapper> getTerms() {
        return terms;
    }

    public void setTerms(List<TermWrapper> terms) {
        this.terms = terms;
    }
}
