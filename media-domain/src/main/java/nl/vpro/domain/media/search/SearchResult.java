package nl.vpro.domain.media.search;

import java.util.Iterator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public interface SearchResult<T> extends Iterable<T> {

    List<T> getResult();

    Long getCount();

    String getQuery();

    default int size() {
        return getResult().size();
    }
    @NonNull
    @Override
    default Iterator<T> iterator() {
        return getResult().iterator();
    }

}
