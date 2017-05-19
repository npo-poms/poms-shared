package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.Duration;
import java.util.Base64;
import java.util.Iterator;
import java.util.Locale;
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



    public static Stream<Cue> parse(Subtitles subtitles) {
        return parse(subtitles.getContent(), subtitles.getMid(), subtitles.getOffset());
    }

    public static Stream<Cue> parse(SubtitlesContent content, String mid, Duration offset) {
        switch (content.getFormat()) {
            case TT888:
                return TT888.parse(mid, offset, new StringReader(content.getValue()));
            case WEBVTT:
                return WEBVTTandSRT.parse(mid, offset, new StringReader(content.getValue()), ".");
            case SRT:
                return WEBVTTandSRT.parse(mid, offset, new StringReader(content.getValue()), ",");
            case EBU:
                return EBU.parse(mid, offset, new ByteArrayInputStream(Base64.getDecoder().decode(content.getValue())));
            default:
                throw new IllegalArgumentException("Not supported format " + content.getFormat());
        }

    }

    public static Stream<StandaloneCue> standaloneStream(Subtitles subtitles) {
        if (subtitles == null) {
            return Stream.empty();
        }
        return parse(subtitles)
            .map(c -> new StandaloneCue(c, subtitles.getLanguage(), subtitles.getType())
            );
    }



    public static CountedIterator<Cue> iterator(Subtitles subtitles){
        return new BasicWrappedIterator<>(
            (long) subtitles.getCueCount(),
            parse(subtitles)
                .iterator());
    }

    public static CountedIterator<StandaloneCue> standaloneIterator(Subtitles subtitles) {
        return new BasicWrappedIterator<>(
            (long) subtitles.getCueCount(),
            parse(subtitles)
                .map(c -> new StandaloneCue(c, subtitles.getLanguage(), subtitles.getType()))
                .iterator());
    }

    public static void toEBU(Iterator<? extends Cue> cueIterator, OutputStream entityStream) throws IOException {
        TT888.format(cueIterator, entityStream);

    }

    public static void toVTT(Iterator<? extends Cue> cueIterator, OutputStream entityStream) throws IOException {
        WEBVTTandSRT.formatWEBVTT(cueIterator, entityStream);
    }

    public static void toSRT(Iterator<? extends Cue> cueIterator, OutputStream entityStream) throws IOException {
        WEBVTTandSRT.formatSRT(cueIterator, entityStream);

    }


}
