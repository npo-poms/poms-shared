package nl.vpro.domain.api.media;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.api.Order;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 */
@XmlType(name = "titleSortOrderType")
@EqualsAndHashCode(callSuper = true)
@ToString
public class TitleSortOrder extends MediaSortOrder {

    @Getter
    @XmlAttribute
    private TextualType type;
    @Getter
    @XmlAttribute
    private OwnerType owner;

    @lombok.Builder(builderClassName = "Builder")
    public TitleSortOrder(TextualType textualType, OwnerType ownerType, Order order) {
        super(null, order);
        this.type = textualType;
        this.owner = ownerType;
    }

    protected TitleSortOrder() {
        super(null);
        // jaxb
    }

    @Override
    @JsonProperty("field")
    public MediaSortField getField() {
        return MediaSortField.title;
    }
}
