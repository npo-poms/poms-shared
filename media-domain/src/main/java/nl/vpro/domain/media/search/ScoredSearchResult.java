package nl.vpro.domain.media.search;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public interface ScoredSearchResult<T> extends SearchResult<T>  {

    List<ScoredResult<T>> getScoredResult();
    
    @Override
    default List<T> getResult() {
        return getScoredResult()
            .stream().map(ScoredResult::getResult)
            .collect(Collectors.toList());
    }
    
    default Stream<ScoredResult<T>> stream() {
        return getScoredResult().stream();
    }

}
