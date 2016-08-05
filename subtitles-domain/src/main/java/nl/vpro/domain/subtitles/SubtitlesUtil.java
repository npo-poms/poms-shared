package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import nl.vpro.util.ISO6937CharsetProvider;


/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
@Slf4j
public class SubtitlesUtil {

    public static Charset ISO6937 = ISO6937CharsetProvider.ISO6937;

    private static final DateTimeFormatter WEBVTT_FORMATTER = DateTimeFormatter.ofPattern("m:ss.SSS");
    private static final DateTimeFormatter EBU_FORMATTER = DateTimeFormatter.ofPattern("m:ss");


    public static Subtitles ebu(String parent, Duration duration, InputStream input) throws IOException {
        StringWriter w = new StringWriter();
        IOUtils.copy(new InputStreamReader(input, ISO6937), w);
        return new Subtitles(parent, duration, SubtitlesFormat.EBU,  w.toString());
    }

    public static Stream<Cue> parse(Subtitles subtitles) {
        switch (subtitles.getFormat()) {
            case EBU:
                return parseEBU(subtitles.getMid(), new StringReader(subtitles.getContent()));
            case WEBVTT:
                return parseWEBVTT(subtitles.getMid(), new StringReader(subtitles.getContent()));
            default:
                throw new IllegalStateException();
        }

    }

    public static Stream<Cue> parseWEBVTT(String parent, Reader inputStream) {
        throw new IllegalStateException();
    }

    public static Stream<Cue> parseEBU(String parent, InputStream inputStream) {
        return parseEBU(parent, new InputStreamReader(inputStream, ISO6937));
    }

    public static Stream<Cue> parseEBU(final String parent, Reader reader) {
        final Iterator<String> stream = new BufferedReader(reader)
            .lines().iterator();
        Iterator<Cue> cues = new Iterator<Cue>() {

            boolean needsFindNext = true;
            String timeLine = null;
            StringBuilder content = new StringBuilder();

            @Override
            public boolean hasNext() {
                findNext();
                return timeLine != null;
            }

            @Override
            public Cue next() {
                findNext();
                if (timeLine == null) {
                    throw new NoSuchElementException();
                }
                needsFindNext = true;
                try {
                    return Cue.parse(parent, timeLine, content.toString());
                }  catch (ParseException e) {
                    log.error(e.getMessage(), e);
                    return null;
                }

            }

            protected void findNext() {
                if (needsFindNext) {
                    timeLine = null;
                    content.setLength(0);
                    while(stream.hasNext()) {
                        String l = stream.next();
                        if (StringUtils.isNotBlank(l)) {
                            timeLine = l.trim();
                            break;
                        }
                    }
                    while (stream.hasNext()) {
                        String l = stream.next();
                        if (StringUtils.isBlank(l)) {
                            break;
                        }
                        if (content.length() > 0) {
                            content.append('\n');
                        }
                        content.append(l.trim());
                    }
                    needsFindNext = false;
                }
            }
        };
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(cues, Spliterator.ORDERED), false);

    }

    public static void toVTT(Iterator<? extends Cue> cueIterator, OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out, Charset.forName("UTF-8"));
        toVTT(cueIterator, writer);
        writer.flush();
    }

    public static void toVTT(Iterator<? extends Cue> cueIterator, Writer writer) throws IOException {
        writer.write("WEBVTT\n\n");
        StringBuilder builder = new StringBuilder();
        while (cueIterator.hasNext()) {
            formatVVT(cueIterator.next(), builder);
            writer.write(builder.toString());
            builder.setLength(0);
        }
    }

    public static void toEBU(Iterator<? extends Cue> cueIterator, OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out, ISO6937);
        toEBU(cueIterator, writer);
        writer.flush();
    }

    public static void toEBU(Iterator<? extends Cue> cueIterator, Writer writer) throws IOException {
        StringBuilder builder = new StringBuilder();
        while (cueIterator.hasNext()) {
            formatEBU(cueIterator.next(), builder);
            writer.write(builder.toString());
            builder.setLength(0);
        }
    }

    protected static StringBuilder formatVVT(Cue cue, StringBuilder builder) {
        builder.append(cue.getSequence());
        builder.append("\n");
        if (cue.getStart() != null) {
            builder.append(WEBVTT_FORMATTER.format(LocalTime.MIDNIGHT.plus(cue.getStart())));
        }
        builder.append(" --> ");
        if (cue.getEnd() != null) {
            builder.append(WEBVTT_FORMATTER.format(LocalTime.MIDNIGHT.plus(cue.getEnd())));
        }
        builder.append("\n");
        if (cue.getContent() != null) {
            builder.append(cue.getContent());
        }
        builder.append("\n\n");
        return builder;
    }

    protected static StringBuilder formatEBU(Cue cue, StringBuilder builder) {
        //001 0:01 0:02 ondertitels !

        builder.append(String.format("%04d ", cue.getSequence()));
        if (cue.getStart() != null) {
            builder.append(EBU_FORMATTER.format(LocalTime.MIDNIGHT.plus(cue.getStart())));
        }
        builder.append(" ");
        if (cue.getEnd() != null) {
            builder.append(EBU_FORMATTER.format(LocalTime.MIDNIGHT.plus(cue.getEnd())));
        }
        builder.append("\n");
        if (cue.getContent() != null) {
            builder.append(cue.getContent());
        }
        builder.append("\n\n");
        return builder;
    }
}
