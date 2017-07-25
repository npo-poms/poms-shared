package nl.vpro.domain.api.media;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import nl.vpro.domain.api.Order;

/**
 * @author Michiel Meeuwissen
 */
@XmlType(name = "mediaSortType")
@ToString
@EqualsAndHashCode
public class MediaSortOrder {

    @XmlValue
    @Getter
    private MediaSortField sortField;

    @XmlAttribute
    @Getter
    private Order order;

    public static MediaSortOrder asc(MediaSortField field) {
        return new MediaSortOrder(field, Order.ASC);
    }


    public static MediaSortOrder desc(MediaSortField field) {
        return new MediaSortOrder(field, Order.DESC);
    }

    public MediaSortOrder(MediaSortField sortField, Order order) {
        this.sortField = sortField;
        this.order = order == null ? Order.ASC : order;
    }

    public MediaSortOrder(MediaSortField sortField) {
        this(sortField, Order.ASC);
    }
    protected MediaSortOrder() {

    }

}
