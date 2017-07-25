package nl.vpro.domain.api.media;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.api.Order;
import nl.vpro.domain.api.media.bind.MediaSortOrderAdapter;

/**
* @author Michiel Meeuwissen
* @since 3.3
*/
@EqualsAndHashCode(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaSortListType")
@XmlJavaTypeAdapter(MediaSortOrderAdapter.class)
@Data
public class MediaSortOrderList extends AbstractCollection<MediaSortOrder> {

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

    @Override
    public int size() {
        return sort.size();
    }

    @Override
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
