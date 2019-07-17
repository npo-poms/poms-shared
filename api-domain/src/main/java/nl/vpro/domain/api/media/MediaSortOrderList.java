package nl.vpro.domain.api.media;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.Order;
import nl.vpro.domain.api.media.bind.MediaSortOrderListJson;

/**
* @author Michiel Meeuwissen
* @since 3.3
*/
@JsonSerialize(using = MediaSortOrderListJson.Serializer.class)
@JsonDeserialize(using = MediaSortOrderListJson.Deserializer.class)
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class MediaSortOrderList extends AbstractList<MediaSortOrder> implements Iterable<MediaSortOrder> {

    private List<MediaSortOrder> sort;

    public MediaSortOrderList() {
    }

    public MediaSortOrderList(List<MediaSortOrder> sort) {
        this.sort = sort;
    }

    @NonNull
    @Override
    public Iterator<MediaSortOrder> iterator() {
        return sort == null ? Collections.<MediaSortOrder>emptyList().iterator() : sort.iterator();

    }

    @Override
    public int size() {
        return sort == null ? 0 : sort.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean add(MediaSortOrder sortOrder) {
        if (sort == null) {
            sort = new ArrayList<>();
        }
        return sort.add(sortOrder);
    }

    @Override
    public MediaSortOrder get(int index) {
        return sort.get(index);

    }

    public void put(MediaSortField field, Order order) {
        add(new MediaSortOrder(field, order));
    }
}
