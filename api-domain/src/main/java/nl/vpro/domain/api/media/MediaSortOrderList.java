package nl.vpro.domain.api.media;

import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.Order;
import nl.vpro.domain.api.media.bind.MediaSortOrderListJson;

/**
* @author Michiel Meeuwissen
* @since 3.3
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaSortListType")
@JsonSerialize(using = MediaSortOrderListJson.Serializer.class)
@JsonDeserialize(using = MediaSortOrderListJson.Deserializer.class)
@Data
@XmlSeeAlso({MediaSortOrder.class, TitleSortOrder.class})
public class MediaSortOrderList
   // extends AbstractCollection<MediaSortOrder> { // confuses Jaxb
    implements Iterable<MediaSortOrder> {

    @XmlElement
    private List<MediaSortOrder> sort;

    public MediaSortOrderList() {
    }

    public MediaSortOrderList(List<MediaSortOrder> sort) {
        this.sort = sort;
    }

    @Override
    public Iterator<MediaSortOrder> iterator() {
        return sort.iterator();

    }

    public int size() {
        return sort == null ? 0 : sort.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean add(MediaSortOrder sortOrder) {
        if (sort == null) {
            sort = new ArrayList<>();
        }
        return sort.add(sortOrder);
    }

    public void put(MediaSortField field, Order order) {
        add(new MediaSortOrder(field, order));
    }
}
