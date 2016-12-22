package nl.vpro.domain.api.page;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.api.TermFacetResultItem;
import nl.vpro.domain.page.TermWrapper;


/**
 * @author Michiel Meeuwissen
 * @since 3.4
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageGenreFacetResultItemType")
public class GenreFacetResultItem extends TermFacetResultItem {


    @XmlElement(name = "term")
    @JsonProperty("terms")
    private List<TermWrapper> terms;

    public GenreFacetResultItem() {
    }

    public GenreFacetResultItem(List<TermWrapper> terms, String value, String id, long count) {
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
