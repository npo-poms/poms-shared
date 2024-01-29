package nl.vpro.domain.api.media;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.api.Order;

/**
 * @author Michiel Meeuwissen
 */
@XmlType(name = "mediaSortType")
@XmlAccessorType(XmlAccessType.NONE)
@ToString
@EqualsAndHashCode
public class MediaSortOrder {

    @XmlValue
    @JsonProperty("field")
    @Getter
    private MediaSortField field;

    @XmlAttribute
    @Getter
    private Order order;

    public static MediaSortOrder asc(MediaSortField field) {
        return new MediaSortOrder(field, Order.ASC);
    }


    public static MediaSortOrder desc(MediaSortField field) {
        return new MediaSortOrder(field, Order.DESC);
    }

    public MediaSortOrder(MediaSortField field, Order order) {
        this.field = field;
        this.order = order == null ? Order.ASC : order;
    }

    public MediaSortOrder(MediaSortField field) {
        this(field, Order.ASC);
    }
    protected MediaSortOrder() {

    }

}
