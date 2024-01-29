package nl.vpro.domain.api;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This is an array of Strings in json. But it can also be marshalled/unmarshalled to XML.
 * @author Michiel Meeuwissen
 * @since 3.2.4
 */
@XmlRootElement(name = "idList")
@XmlType(name = "idListType")
@XmlAccessorType(XmlAccessType.FIELD)
public class IdList extends AbstractList<String> implements Serializable {

    @Serial
    private static final long serialVersionUID = 0L;


    public static IdList of(String... ids) {
        return new IdList(ids);
    }

    public static IdList of(Collection<String> ids) {
        return new IdList(ids);
    }

    @XmlElement(name = "id")
    List<String> ids = new ArrayList<>();

    public IdList() {

    }

    public IdList(String... ids) {
        this.ids = Arrays.asList(ids);

    }
    public IdList(Collection<String> list) {
        ids.addAll(list);
    }

    @Override
    public String get(int index) {
        return ids.get(index);

    }

    @Override
    public int size() {
        return ids.size();
    }

    @Override
    public String set(int index, String id) {
        return ids.set(index, id);
    }

    @Override
    public void add(int index, String id) {
        ids.add(index, id);
    }

    @Override
    public boolean add(String id) {
        return ids.add(id);
    }

    @Override
    public String remove(int index) {
        return ids.remove(index);
    }

    @Override
    @NonNull
    public IdList subList(int offset, int max) {
        return new IdList(ids.subList(offset, max));
    }
}
