package nl.vpro.domain.media.update.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.update.*;

/**
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
    String.class
})
public class XmlCollection<T> implements Iterable<T>  {

    @XmlAnyElement(lax = true)
    Collection<T> list;

    public XmlCollection() {

    }


    public XmlCollection(Collection<T> l) {
        this.list = l;

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
}
