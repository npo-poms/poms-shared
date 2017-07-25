package nl.vpro.domain.api.media;

import lombok.Getter;

import nl.vpro.domain.api.Order;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 */
public class TitleSortOrder extends MediaSortOrder {

    @Getter
    private final TextualType textualType;
    @Getter
    private final OwnerType ownerType;

    @lombok.Builder(builderClassName = "Builder")
    public TitleSortOrder(TextualType textualType, OwnerType ownerType, Order order) {
        super(MediaSortField.title, order);
        this.textualType = textualType;
        this.ownerType = ownerType;

    }
}
