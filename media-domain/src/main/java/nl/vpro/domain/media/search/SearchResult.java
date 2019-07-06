package nl.vpro.domain.media.search;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public interface SearchResult<T> extends Iterable<T> {

    List<T> getResult();

    Long getCount();

    default int size() {
        return getResult().size();
    }
    @Nonnull
    @Override
    default Iterator<T> iterator() {
        return getResult().iterator();
    }

}
