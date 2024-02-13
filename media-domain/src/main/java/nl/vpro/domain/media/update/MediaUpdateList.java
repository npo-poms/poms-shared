package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Stream;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.util.IntegerVersion;
import nl.vpro.util.IntegerVersionSpecific;


/**
 * @author Michiel Meeuwissen
 */
@XmlRootElement(name = "list")
@XmlType(name = "mediaListResultType")
@XmlSeeAlso({
    ProgramUpdate.class,
    GroupUpdate.class,
    SegmentUpdate.class,
    MemberUpdate.class,
    MemberRefUpdate.class,
    LocationUpdate.class,
    String.class
})
@XmlAccessorType(XmlAccessType.NONE)
public class MediaUpdateList<T> implements Iterable<T>, IntegerVersionSpecific {

    @XmlAttribute
    @Getter
    @Setter
    protected IntegerVersion version;

    protected List<T> list;

    @XmlAttribute
    @Getter
    protected long offset;

    @XmlAttribute
    @Getter
    protected long totalCount;

    @XmlAttribute
    @Getter
    protected Integer max;

    @XmlAttribute
    @Getter
    protected String order;


    public MediaUpdateList() {
        super();
    }

    public MediaUpdateList(final List<T> list, long totalCount, long offset, Integer max, String order, IntegerVersion version) {
        this.list = Collections.unmodifiableList(list);
        this.offset = offset;
        this.totalCount =  totalCount;
        this.max = max;
        this.order = order;
        this.version = version;
    }


    @SafeVarargs
    public MediaUpdateList(T... list) {
        this.list = Collections.unmodifiableList(Arrays.asList(list));
        this.offset = 0;
        this.totalCount = list.length;
        this.max = null;
        this.order = null;
    }


    @XmlElement(name = "item")
    public List<T> getList() {
        return list;
    }
    public void setList(List<T> l) {
        this.list = l == null ? null : Collections.unmodifiableList(l);
    }

    //@Override
    public int size() {
        return list == null ? 0 : list.size();
    }

    public boolean isEmpty() {
        return list == null || list.isEmpty();
    }

    @XmlAttribute
    public int getSize() {
        return size();
    }
    protected void setSize(int size) {
        // for jackson
    }

    @Override
    public String toString() {
        return String.valueOf(list);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return list == null ? Collections.emptyIterator() : list.iterator();
    }

    public Stream<T> stream() {
        return list == null ? Stream.empty() : list.stream();
    }


    void afterUnmarshal(Unmarshaller u, Object parent) {
        if (parent != null) {
            if (parent instanceof IntegerVersionSpecific integerVersionSpecific) {
                version = integerVersionSpecific.getVersion();
            }
        }
    }
}
