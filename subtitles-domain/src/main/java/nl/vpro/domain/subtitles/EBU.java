package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Slf4j
public class EBU {

    public static Charset EBU_CHARSET = SubtitlesFormat.EBU.getCharset();

    public static StringBuilder format(Cue cue, StringBuilder builder) {
        //001 0:01:00 0:02:00 ondertitels !

        builder.append(String.format("%04d ", cue.getSequence()));
        if (cue.getStart() != null) {
            builder.append(formatDuration(cue.getStart()));
        }
        builder.append(" ");
        if (cue.getEnd() != null) {
            builder.append(formatDuration(cue.getEnd()));
        }
        builder.append("\n");
        if (cue.getContent() != null) {
            builder.append(cue.getContent());
        }
        builder.append("\n\n");
        return builder;
    }

    static String formatDuration(Duration duration) {
        long centiseconds = duration.toMillis() / 10;
        /*long hours = centiseconds / 360000;
        centiseconds -= hours * 360000;
        */
        long minutes = centiseconds / 6000;
        centiseconds -= minutes * 6000;
        long seconds = centiseconds / 100;
        centiseconds -= seconds * 100;
        return String.format("%02d:%02d:%02d", minutes, seconds, centiseconds);
    }


    public static Stream<Cue> parse(String parent, InputStream inputStream) {
        return parse(parent, new InputStreamReader(inputStream, EBU_CHARSET));
    }

    static Stream<Cue> parse(final String parent, Reader reader) {
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
                    return parseCue(parent, timeLine, content.toString());
                } catch (IllegalArgumentException e) {
                    log.error(e.getMessage(), e);
                    return null;
                }

            }

            protected void findNext() {
                if (needsFindNext) {
                    timeLine = null;
                    content.setLength(0);
                    while (stream.hasNext()) {
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

    static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");

    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UT"));
    }

    static Cue parseCue(String parent, String timeLine, String content) {
        String[] split = timeLine.split("\\s+");
        try {
            return new Cue(
                parent,
                Integer.parseInt(split[0]),
                Duration.ofMillis(dateFormat.parse(split[1] + "0").getTime()),
                Duration.ofMillis(dateFormat.parse(split[2] + "0").getTime()),
                content
            );
        } catch (NumberFormatException | ParseException nfe) {
            throw new IllegalArgumentException("For " + parent + " could not parse " + timeLine + " (" + Arrays.asList(split) + "). Expected content: " + content + " Reason: " + nfe.getClass() + " " + nfe.getMessage(), nfe);
        }

    }


    static void format(Iterator<? extends Cue> cueIterator, OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out, EBU_CHARSET);
        format(cueIterator, writer);
        writer.flush();
    }

    static void format(Iterator<? extends Cue> cueIterator, Writer writer) throws IOException {
        StringBuilder builder = new StringBuilder();
        while (cueIterator.hasNext()) {
            format(cueIterator.next(), builder);
            writer.write(builder.toString());
            builder.setLength(0);
        }
    }

}
