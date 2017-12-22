package nl.vpro.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Iterator;

import nl.vpro.util.CloseableIterator;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@AllArgsConstructor
@Getter
@Setter
public class Changes<T> implements AutoCloseable, Iterable<Change<T>> {

    private final Instant until;
    private final Long count;
    private final CloseableIterator<? extends Change<T>> iterator;

    @Override
    public void close() throws Exception {
        iterator.close();
    }
    @Override
    public Iterator<Change<T>> iterator() {
        return (Iterator<Change<T>>) iterator;
    }

    @Override
    public String toString() {
        return "Changes until " + until + ", count:" + count + ", iterator: " + iterator;

    }
}
