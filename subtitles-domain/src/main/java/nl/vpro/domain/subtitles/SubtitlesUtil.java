package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.util.BasicWrappedIterator;
import nl.vpro.util.CountedIterator;

import static nl.vpro.util.ISO6937CharsetProvider.ISO6937;


/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
@Slf4j
public class SubtitlesUtil {

    private SubtitlesUtil() {
    }


    public static Subtitles tt888(String parent, Duration offset, Locale locale, InputStream input) throws IOException {
        StringWriter w = new StringWriter();
        if (input == null) {
            throw new IllegalArgumentException("InputStream is null");
        }
        IOUtils.copy(new InputStreamReader(input, ISO6937), w);
        return Subtitles.builder()
            .mid(parent)
            .offset(offset)
            .language(locale)
            .format(SubtitlesFormat.TT888)
            .content(w.toString())
            .build();
    }



    public static Subtitles vtt(String parent, Duration offset, Locale locale, InputStream input) throws IOException {
        StringWriter w = new StringWriter();
        if (input == null) {
            throw new IllegalArgumentException("InputStream is null");
        }
        IOUtils.copy(new InputStreamReader(input, WEBVTTandSRT.VTT_CHARSET), w);
        return Subtitles.builder()
            .mid(parent)
            .offset(offset)
            .language(locale)
            .format(SubtitlesFormat.WEBVTT)
            .content(w.toString())
            .build();
    }




    @NonNull
    public static ParseResult parse(@NonNull Subtitles subtitles, boolean guessOffset) {

        SubtitlesContent content = subtitles.getContent();
        String mid = subtitles.getMid();
        Duration offset = subtitles.getOffset();

        Function<TimeLine, Duration> offsetGuesser = guessOffset ? new DefaultOffsetGuesser(subtitles.getCreationInstant()) : timeLine -> Duration.ZERO;
        return switch (content.getFormat()) {
            case TT888 ->
                ParseResult.of(TT888.parse(mid, offset, offsetGuesser, content.asStream(), getCharset(content.getCharset(), TT888.CHARSET)));
            case WEBVTT ->
                WEBVTTandSRT.parseWEBVTT(mid, offset, content.asStream(), getCharset(content.getCharset(), WEBVTTandSRT.VTT_CHARSET));
            case SRT ->
                ParseResult.of(WEBVTTandSRT.parseSRT(mid, offset, content.asStream(), getCharset(content.getCharset(), WEBVTTandSRT.SRT_CHARSET)));
            case EBU -> ParseResult.of(EBU.parse(mid, offset, offsetGuesser, content.asStream()));
            default -> throw new IllegalArgumentException("Not supported format " + content.getFormat());
        };
    }




    private static Charset getCharset(String c, Charset defaultValue) {
        if (c == null) {
            log.debug("Using default");
            return defaultValue;
        }
        try {
            return Charset.forName(c);
        } catch (UnsupportedCharsetException usce) {
            if (!defaultValue.name().equals(c)) {
                log.warn(usce.getMessage());
            }
            return defaultValue;

        }
    }

    public static Stream<@NonNull StandaloneCue> standaloneStream(Subtitles subtitles, boolean guessOffset, boolean fillCueNumbers) {
        if (subtitles == null) {
            return Stream.empty();
        }
        Stream<Cue> stream = parse(subtitles, guessOffset).getCues();
        if (fillCueNumbers) {
            stream = fillCueNumber(stream);
        }
        return stream.map(c -> StandaloneCue.of(c, subtitles.getLanguage(), subtitles.getType()))
            .filter(Objects::nonNull);
    }



    public static CountedIterator<Cue> iterator(Subtitles subtitles, boolean guessOffset){
        return new BasicWrappedIterator<>(
            (long) subtitles.getCueCount(),
            parse(subtitles, guessOffset)
                .iterator());
    }

    public static CountedIterator<Cue> iterator(Subtitles subtitles) {
        return iterator(subtitles, false);
    }

    public static CountedIterator<StandaloneCue> standaloneIterator(Subtitles subtitles, boolean guessOffset, boolean fillCueNumbers) {
        return new BasicWrappedIterator<>((long) subtitles.getCueCount(), standaloneStream(subtitles, guessOffset, fillCueNumbers).iterator());

    }
    public static void stream(Iterator<? extends Cue> cueIterator, SubtitlesFormat format, OutputStream output) throws IOException {
        switch(format) {
            case TT888:
                toTT888(cueIterator, output);
                return;
            case WEBVTT:
                toVTT(cueIterator, output);
                return;
            case SRT:
                toSRT(cueIterator, output);
                return;
            default:
                throw new UnsupportedOperationException();
        }
    }


    public static void toTT888(Iterator<? extends Cue> cueIterator, OutputStream outputStream) throws IOException {
        TT888.format(cueIterator, outputStream);

    }

    public static void toVTT(Iterator<? extends Cue> cueIterator, OutputStream outputStream) throws IOException {
        WEBVTTandSRT.formatWEBVTT(cueIterator, outputStream);
    }

    public static void toSRT(Iterator<? extends Cue> cueIterator, OutputStream outputStream) throws IOException {
        WEBVTTandSRT.formatSRT(cueIterator, outputStream);

    }

    public static Stream<Cue> fillCueNumber(Stream<Cue> cues) {
        final AtomicInteger cueNumber = new AtomicInteger(0);
        return cues
            .peek((cue) -> {
                if (cue != null) {
                    if (cue.getSequence() == null) {
                        cue.sequence = cueNumber.incrementAndGet();
                    } else {
                        cueNumber.set(cue.getSequence());
                    }
                }
                }
            );

    }


}
