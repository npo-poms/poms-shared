package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.Duration;
import java.util.Base64;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;

import nl.vpro.util.BasicWrappedIterator;
import nl.vpro.util.CountedIterator;

import static nl.vpro.util.ISO6937CharsetProvider.ISO6937;


/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
@Slf4j
public class SubtitlesUtil {


    public static Subtitles tt888(String parent, Duration offset, Locale locale, InputStream input) throws IOException {
        StringWriter w = new StringWriter();
        IOUtils.copy(new InputStreamReader(input, ISO6937), w);
        return Subtitles.builder()
            .mid(parent)
            .offset(offset)
            .language(locale)
            .format(SubtitlesFormat.TT888)
            .content(w.toString())
            .build();
    }



    public static Stream<Cue> parse(Subtitles subtitles, boolean guessOffset) {

        SubtitlesContent content = subtitles.getContent();
        String mid = subtitles.getMid();
        Duration offset = subtitles.getOffset();


        Function<TimeLine, Duration> offsetGuesser = guessOffset ? new DefaultOffsetGuesser(subtitles.getCreationDate()) : timeLine -> Duration.ZERO;
        switch (content.getFormat()) {
            case TT888:
                return TT888.parse(mid, offset, offsetGuesser, new StringReader(content.getValue()));
            case WEBVTT:
                return WEBVTTandSRT.parse(mid, offset, new StringReader(content.getValue()), ".");
            case SRT:
                return WEBVTTandSRT.parse(mid, offset, new StringReader(content.getValue()), ",");
            case EBU:
                return EBU.parse(mid, offset, offsetGuesser, new ByteArrayInputStream(Base64.getDecoder().decode(content.getValue())));
            default:
                throw new IllegalArgumentException("Not supported format " + content.getFormat());
        }

    }

    public static Stream<StandaloneCue> standaloneStream(Subtitles subtitles, boolean guessOffset) {
        if (subtitles == null) {
            return Stream.empty();
        }
        return parse(subtitles, guessOffset)
            .map(c -> new StandaloneCue(c, subtitles.getLanguage(), subtitles.getType())
            );
    }



    public static CountedIterator<Cue> iterator(Subtitles subtitles, boolean guessOffset){
        return new BasicWrappedIterator<>(
            (long) subtitles.getCueCount(),
            parse(subtitles, guessOffset)
                .iterator());
    }

    public static CountedIterator<Cue> iterator(Subtitles subtitles) {
        return new BasicWrappedIterator<>(
            (long) subtitles.getCueCount(),
            parse(subtitles, false)
                .iterator());
    }

    public static CountedIterator<StandaloneCue> standaloneIterator(Subtitles subtitles, boolean guessOffset) {
        return new BasicWrappedIterator<>(
            (long) subtitles.getCueCount(),
            parse(subtitles, guessOffset)
                .map(c -> new StandaloneCue(c, subtitles.getLanguage(), subtitles.getType()))
                .iterator());
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



}
