package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Slf4j
public class TT888 {

    public static final Charset CHARSET = SubtitlesFormat.TT888.getCharset();

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
        long millis = duration.toMillis();
        boolean negative = millis < 0;
        if (negative) {
            millis *= -1;
        }
        long centiseconds = millis / 10;
        /*long hours = centiseconds / 360000;
        centiseconds -= hours * 360000;
        */
        long minutes = centiseconds / 6000;
        centiseconds -= minutes * 6000;
        long seconds = centiseconds / 100;
        centiseconds -= seconds * 100;
        return String.format("%s%02d:%02d:%02d", negative ? "-": "", minutes, seconds, centiseconds);
    }



    public static Stream<Cue> parse(String parent, Duration offset, Function<TimeLine, Duration> offsetGuesser, InputStream inputStream) {
        return parse(parent, offset, offsetGuesser, inputStream, CHARSET);
    }

    public static Stream<Cue> parseUTF8(String parent, Duration offset, Function<TimeLine, Duration> offsetGuesser, InputStream inputStream) {
        return parse(parent, offset, offsetGuesser, inputStream, StandardCharsets.UTF_8);
    }


    public static Stream<Cue> parse(String parent, Duration offset, Function<TimeLine, Duration> offsetGuesser, InputStream inputStream, Charset charset) {
        return parse(parent, offset, offsetGuesser, new InputStreamReader(inputStream, charset));
    }

    static Stream<Cue> parse(final String parent, final Duration offsetParameter, Function<TimeLine, Duration> offsetGuesser, Reader reader) {
        final Iterator<String> stream = new BufferedReader(reader)
            .lines().iterator();

        Iterator<Cue> cues = new Iterator<Cue>() {

            boolean needsFindNext = true;
            String timeLine = null;
            StringBuilder content = new StringBuilder();
            Duration offset = offsetParameter;

            long count = 0;

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
                    String contentString = content.toString();
                    TimeLine parsedTimeLine = parseTimeline(timeLine);
                    if (offset == null) {
                        offset = offsetGuesser.apply(parsedTimeLine);
                    }
                    return createCue(parent, parsedTimeLine, offset, contentString);
                } catch (IllegalArgumentException e) {
                    log.error(e.getMessage(), e);
                    return null;
                } finally {
                    count++;
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
                        if (StringUtils.isBlank(l) && content.length() > 0) {
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

    static Cue createCue(String parent, TimeLine timeLine, Duration offset, String content) {
        return Cue.builder()
            .mid(parent)
            .sequence(timeLine.sequence)
            .start(timeLine.start.minus(offset))
            .end(timeLine.end.minus(offset))
            .content(content)
            .build();
        }

    public static TimeLine parseTimeline(String timeLine) {
        String[] split = timeLine.split("\\s+");
        try {
            return new TimeLine(
                Integer.parseInt(split[0]),
                parseTime(split[1]),
                parseTime(split[2])
            );
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException nfe) {
            throw new IllegalArgumentException("Could not parse " + timeLine + " (" + Arrays.asList(split) + ").  Reason: " + nfe.getClass() + " " + nfe.getMessage(), nfe);
        }


    }

    private static Duration parseTime(String duration) {
        String[] split = duration.split(":", 4);
        int index = 0;
        long hours = Long.parseLong(split[0]);
        long minutes = Long.parseLong(split[1]);
        long seconds = Long.parseLong(split[2]);
        long hunderds = Long.parseLong(split[3]);
        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds).plusMillis(hunderds * 10);
    }


    static void format(Iterator<? extends Cue> cueIterator, OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out, CHARSET);
        format(cueIterator, writer);
        writer.flush();
    }

    static void format(Iterator<? extends Cue> cueIterator, Writer writer) throws IOException {
        StringBuilder builder = new StringBuilder();
        while (cueIterator.hasNext()) {
            Cue cue = cueIterator.next();
            if (cue != null) {
                format(cue, builder);
                writer.write(builder.toString());
                builder.setLength(0);
            }
        }
    }

}
