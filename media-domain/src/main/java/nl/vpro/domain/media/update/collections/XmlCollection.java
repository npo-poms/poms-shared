package nl.vpro.domain.media.update.collections;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Stream;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.media.update.*;
import nl.vpro.util.IntegerVersion;
import nl.vpro.util.IntegerVersionSpecific;

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
public class XmlCollection<T> implements Iterable<T> , IntegerVersionSpecific {


    @XmlAttribute
    @Getter
    @Setter
    protected IntegerVersion version;

    @XmlAnyElement(lax = true)
    @JsonProperty("list")
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

    public XmlCollection(Collection<T> l, IntegerVersion version) {
        this.list = l;
        this.version = version;

    }
    public int size() {
        return list == null ? 0 : list.size();
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
            if (parent instanceof IntegerVersionSpecific) {
                version = ((IntegerVersionSpecific) parent).getVersion();
            }
        }
    }
    @Override
    public String toString() {
        return "xmlcollection:" + list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XmlCollection<?> that = (XmlCollection<?>) o;

        if (!Objects.equals(version, that.version)) return false;
        return Objects.equals(list, that.list);
    }

    @Override
    public int hashCode() {
        int result = version != null ? version.hashCode() : 0;
        result = 31 * result + (list != null ? list.hashCode() : 0);
        return result;
    }
}
