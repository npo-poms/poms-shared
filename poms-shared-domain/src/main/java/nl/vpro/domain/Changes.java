package nl.vpro.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Iterator;

import nl.vpro.util.CloseableIterator;

/**
 * Represents a collection os {@link }
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@AllArgsConstructor
@Getter
@Setter
public class Changes<T> implements AutoCloseable, Iterable<AbstractChange<T>> {

    private final Instant until;
    private final Long count;
    private final CloseableIterator<? extends AbstractChange<T>> iterator;

    @Override
    public void close() throws Exception {
        iterator.close();
    }
    @Override
    public Iterator<AbstractChange<T>> iterator() {
        return (Iterator<AbstractChange<T>>) iterator;

    }
}
