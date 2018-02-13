package nl.vpro.domain.media.update.collections;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.VersionSpecific;
import nl.vpro.domain.media.update.*;

/**
 *  XmlCollection does the same as JAXB would do for lists. We use this package info just to define the prefixes for the other namespaces.
 * @author Michiel Meeuwissen
 * @since 3.4
 */
@XmlRootElement(name = "collection")
@XmlType(name = "collectionType", namespace = "")
@XmlSeeAlso({
    ProgramUpdate.class,
    GroupUpdate.class,
    SegmentUpdate.class,
    MemberUpdate.class,
    MemberRefUpdate.class,
    LocationUpdate.class,
    PredictionUpdate.class,
    TranscodeStatus.class,
    String.class
})
@XmlAccessorType(XmlAccessType.NONE)
public class XmlCollection<T> implements Iterable<T> , VersionSpecific {


    @XmlAttribute
    @Getter
    @Setter
    protected Float version;

    @XmlAnyElement(lax = true)
    Collection<T> list;

    public XmlCollection() {

    }


    public XmlCollection(Collection<T> l) {
        this(l, null);
    }

    @SafeVarargs
    public XmlCollection(T... l) {
        this(Arrays.asList(l), null);
    }

    public XmlCollection(Collection<T> l, Float version) {
        this.list = l;
        this.version = version;

    }
    public int size() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Iterator<T> iterator() {
        return list == null ? Collections.emptyIterator() : list.iterator();
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
