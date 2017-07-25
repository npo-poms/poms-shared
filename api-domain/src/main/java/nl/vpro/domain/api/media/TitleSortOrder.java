package nl.vpro.domain.api.media;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.Order;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 */
@XmlType(name = "titleSortOrderType")
public class TitleSortOrder extends MediaSortOrder {

    @Getter
    @XmlAttribute
    private TextualType textualType;
    @Getter
    @XmlAttribute
    private OwnerType ownerType;

    @lombok.Builder(builderClassName = "Builder")
    public TitleSortOrder(TextualType textualType, OwnerType ownerType, Order order) {
        super(MediaSortField.title, order);
        this.textualType = textualType;
        this.ownerType = ownerType;
    }

    protected TitleSortOrder() {
        // jaxb
    }
}
