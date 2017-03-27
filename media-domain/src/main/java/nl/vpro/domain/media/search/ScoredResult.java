package nl.vpro.domain.media.search;

import lombok.Getter;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Getter
public class ScoredResult<T> {

    T result;
    float score;
}
