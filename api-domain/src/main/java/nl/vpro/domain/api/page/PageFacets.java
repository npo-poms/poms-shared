package nl.vpro.domain.api.page;

import lombok.Data;
import nl.vpro.domain.api.DateRangeFacets;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pagesFacetsType")
@Data
public class PageFacets {

    @XmlElement
    private DateRangeFacets sortDates;

    @XmlElement
    private PageFacet broadcasters;

    @XmlElement
    private PageFacet types;

    @XmlElement
    private ExtendedPageFacet tags;

    @XmlElement
    private ExtendedPageFacet keywords;

    @XmlElement
    private PageSearchableTermFacet genres;

    @XmlElement
    private PageFacet portals;

    @XmlElement
    private PageFacet sections;

    @XmlElement
    private RelationFacetList relations;

    @XmlElement
    private PageSearch filter;

    public boolean isFaceted() {
        return sortDates != null
            || broadcasters != null
            || types != null
            || tags != null
            || keywords != null
            || genres != null
            || portals != null
            || sections != null
            || relations != null;
    }
}
