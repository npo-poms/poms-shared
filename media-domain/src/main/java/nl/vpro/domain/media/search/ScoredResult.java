package nl.vpro.domain.media.search;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Getter
@ToString()
public class ScoredResult<T> {
    final T result;
    final float score;

    public ScoredResult(T result, float score) {
        this.result = result;
        this.score = score;
    }
}
