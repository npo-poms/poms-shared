package nl.vpro.domain.api.page;

import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.api.Order;

/**
* @author Michiel Meeuwissen
* @since 3.4
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageSortType")
public class PageSortType {

    @XmlAttribute
    private Order order = Order.ASC;

    @XmlValue
    private PageSortField field;

    public PageSortType() {
    }

    public PageSortType(PageSortField field) {
        this.field = field;
    }

    public PageSortType(PageSortField field, Order order) {
        this.field = field;
        if (order != null) {
            this.order = order;
        }
    }

    public PageSortField getField() {
        return field;
    }

    public void setField(PageSortField field) {
        this.field = field;
    }

    public Order getOrder() {
        return order != null ? order : Order.ASC;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
