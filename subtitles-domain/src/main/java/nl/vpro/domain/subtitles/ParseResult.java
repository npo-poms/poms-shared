package nl.vpro.domain.subtitles;

import lombok.Getter;

import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Getter
public class ParseResult implements Iterable<Cue> {
    @Nonnull
    private final Stream<Cue> cues;

    private ParseResult(Stream<Cue> cues) {
        this.cues = cues;
    }

    public static ParseResult of(Stream<Cue> cues) {
        return new ParseResult(cues);
    }

    @Override
    @Nonnull
    public Iterator<Cue> iterator() {
        return cues.iterator();
    }

    public <R, A> R collect(Collector<? super Cue, A, R> collector) {
        return cues.collect(collector);
    }
}
