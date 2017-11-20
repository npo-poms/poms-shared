package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.VersionSpecific;

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
public class MediaUpdateList<T> implements Iterable<T>, VersionSpecific {

    @XmlAttribute
    @Getter
    @Setter
    protected Float version;

    protected List<T> list;

    @XmlAttribute
    protected long offset;

    @XmlAttribute
    protected long totalCount;

    @XmlAttribute
    protected Integer max;

    @XmlAttribute
    protected String order;


    public MediaUpdateList() {
        super();
    }

    public MediaUpdateList(final List<T> list, long totalCount, long offset, Integer max, String order, Float version) {
        this.list = Collections.unmodifiableList(list);
        this.offset = offset;
        this.totalCount =  totalCount;
        this.max = max;
        this.order = order;
        this.version = version;
    }


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

    @XmlAttribute
    public int getSize() {
        return size();
    }

    @Override
    public String toString() {
        return "" + list;
    }

    @Override
    public Iterator<T> iterator() {
        return list == null ? Collections.<T>emptyList().iterator() : list.iterator();
    }

    public Stream<T> stream() {
        return list == null ? Stream.empty() : list.stream();
    }


    void afterUnmarshal(Unmarshaller u, Object parent) {
        if (parent != null) {
            if (parent instanceof VersionSpecific) {
                version = ((VersionSpecific) parent).getVersion();
            }
        }
    }
}
