package nl.vpro.domain.subtitles;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Getter
public class ParseResult implements Iterable<Cue> {

    private final List<Meta> headers;

    @NonNull
    private final Stream<@Nullable Cue> cues;

    private ParseResult(@NonNull Stream<@NonNull Cue> cues, List<Meta> headers) {
        this.cues = cues;
        this.headers = headers;
    }
    private ParseResult(@NonNull Stream<@NonNull Cue> cues) {
        this(cues, new ArrayList<>());
    }

    public static ParseResult of(Stream<@NonNull Cue> cues) {
        return new ParseResult(cues);
    }

    public static ParseResult of(Stream<@NonNull Cue> cues, List<Meta> headers) {
        return new ParseResult(cues, headers);
    }

    @Override
    @NonNull
    public Iterator<Cue> iterator() {
        return cues.iterator();
    }

    public <R, A> R collect(Collector<? super Cue, A, R> collector) {
        return cues.collect(collector);
    }

    @Override
    public String toString() {
        return "headers: " + headers;
    }
}
