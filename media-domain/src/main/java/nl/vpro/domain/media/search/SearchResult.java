package nl.vpro.domain.media.search;

import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public interface SearchResult<T> {

    List<T> getResult();

    Integer getCount();

    default int size() {
        return getResult().size();
    }

}
