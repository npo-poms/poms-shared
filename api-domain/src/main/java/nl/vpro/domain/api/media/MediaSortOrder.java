package nl.vpro.domain.api.media;

import nl.vpro.domain.api.Order;

/**
 * @author Michiel Meeuwissen
 */
public class MediaSortOrder {

    private final MediaSortField sortField;
    private final Order order;

    public static MediaSortOrder asc(MediaSortField field) {
        return new MediaSortOrder(field, Order.ASC);
    }


    public static MediaSortOrder desc(MediaSortField field) {
        return new MediaSortOrder(field, Order.DESC);
    }

    public MediaSortOrder(MediaSortField sortField, Order order) {
        this.sortField = sortField;
        this.order = order;
    }

    public MediaSortOrder(MediaSortField sortField) {
        this(sortField, Order.ASC);
    }

    public MediaSortField getSortField() {
        return sortField;
    }

    public Order getOrder() {
        return order;
    }
}
