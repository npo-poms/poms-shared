package nl.vpro.domain.media.update;

import java.util.Collection;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public interface TransformingCollection<T, S> extends Collection<T> {

    Collection<S> unwrap();
}
