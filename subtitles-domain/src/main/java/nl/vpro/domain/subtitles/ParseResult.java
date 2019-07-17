package nl.vpro.domain.subtitles;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Getter
public class ParseResult implements Iterable<Cue> {

    private final List<Header> headers;

    @NonNull
    private final Stream<Cue> cues;

    private ParseResult(Stream<Cue> cues, List<Header> headers) {
        this.cues = cues;
        this.headers = headers;
    }
    private ParseResult(Stream<Cue> cues) {
        this(cues, new ArrayList<>());
    }

    public static ParseResult of(Stream<Cue> cues) {
        return new ParseResult(cues);
    }

    public static ParseResult of(Stream<Cue> cues, List<Header> headers) {
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
}
