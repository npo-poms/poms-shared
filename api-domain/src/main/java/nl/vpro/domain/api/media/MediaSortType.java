package nl.vpro.domain.api.media;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.api.Order;

/**
* @author Michiel Meeuwissen
* @since 3.3
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaSortType")
public class MediaSortType {

    @XmlAttribute
    private Order order = Order.ASC;

    @XmlValue
    private MediaSortField field;

    public MediaSortType() {
    }

    public MediaSortType(MediaSortField field) {
        this.field = field;
    }

    public MediaSortType(MediaSortField field, Order order) {
        this.field = field;
        if(order != null) {
            this.order = order;
        }
    }

    public MediaSortField getField() {
        return field;
    }

    public void setField(MediaSortField field) {
        this.field = field;
    }

    public Order getOrder() {
        return order != null ? order : Order.ASC;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
