package nl.vpro.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.util.CloseableIterator;

/**
 * Represent an iterable of {@link Change}'s.
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Getter
@Setter
public class Changes<T> implements AutoCloseable, Iterable<Change<T>> {

    private final Instant until;
    private final Instant since;
    private final Long count;
    private final CloseableIterator<? extends Change<T>> iterator;


    @Getter(AccessLevel.NONE)
    private final Supplier<Instant> eta;

    @lombok.Builder
    public Changes(
        Instant since,
        Instant until,
        Long count,
        CloseableIterator<? extends Change<T>> iterator,
        Supplier<Instant> eta) {
        this.since = since;
        this.until = until;
        this.count = count;
        this.iterator = iterator;
        this.eta = eta == null ? () -> null : eta;
    }

    public Changes(
        Instant since,
        Instant until,
        Long count,
        CloseableIterator<? extends Change<T>> iterator) {
        this(since, until, count, iterator, null);
    }

    @Override
    public void close() throws Exception {
        iterator.close();
    }
    @NonNull
    @Override
    public Iterator<Change<T>> iterator() {
        return (Iterator<Change<T>>) iterator;
    }

    public Optional<Instant> getETA() {
        return Optional.ofNullable(this.eta.get()).map((i) -> i.truncatedTo(ChronoUnit.MINUTES));
    }

    @Override
    public String toString() {
        return "Changes until " + until + ", count:" + count + ", iterator: " + iterator;
    }
}
