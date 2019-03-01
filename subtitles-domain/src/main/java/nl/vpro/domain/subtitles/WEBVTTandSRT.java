package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Slf4j
public class WEBVTTandSRT {


    static final String WEBVTT_INTRO = "WEBVTT";

    public static final Charset SRT_CHARSET = SubtitlesFormat.SRT.getCharset();
    public static final Charset VTT_CHARSET = Charset.forName("UTF-8");


    public static Stream<Cue> parseWEBVTT(String parent, InputStream inputStream) {
        return parseWEBVTT(parent, Duration.ZERO, inputStream, null);
    }

    public static Stream<Cue> parseSRT(String parent, InputStream inputStream) {
        return parseSRT(parent, Duration.ZERO, inputStream, null);
    }


    public static Stream<Cue> parseWEBVTT(String parent, Duration offset, InputStream inputStream, Charset charset) {
        return parse(parent, offset, new InputStreamReader(inputStream, charset == null ? VTT_CHARSET : charset), ".");
    }

    public static Stream<Cue> parseSRT(String parent, Duration offset, InputStream inputStream, Charset charset) {
        return parse(parent, offset, new InputStreamReader(inputStream, charset == null ? SRT_CHARSET : charset), ",");
    }

    static Stream<Cue> parse(String parent, Duration offset,  Reader reader, String decimalSeparator) {
        final Iterator<String> stream = new BufferedReader(reader)
            .lines().iterator();

        Iterator<Cue> cues = new Iterator<Cue>() {

            boolean needsFindNext = true;
            String headLine= null;
            String timeLine = null;
            StringBuilder content = new StringBuilder();
            boolean readIntro = false;

            @Override
            public boolean hasNext() {
                findNext();
                return timeLine != null;
            }

            @Override
            public Cue next() {
                findNext();
                if (timeLine == null || headLine == null) {
                    throw new NoSuchElementException();
                }
                needsFindNext = true;
                try {
                    return parseCue(parent, headLine, offset, timeLine, content.toString(), decimalSeparator);
                } catch (IllegalArgumentException e) {
                    log.warn("Error: {} while parsing\nheadline:{}\ntimeline:{}", e.getMessage(), headLine, timeLine);
                    return null;
                }

            }

            protected void findNext() {
                if (needsFindNext) {
                    headLine = null;
                    timeLine = null;
                    content.setLength(0);
                    while (stream.hasNext()) {
                        String l = stream.next();
                        if (StringUtils.isNotBlank(l)) {
                            if (! readIntro) {
                                if (WEBVTT_INTRO.equals(l.trim())) {
                                    readIntro = true;
                                    continue;
                                }
                            }
                            headLine = l.trim();
                            readIntro = true;
                            break;
                        }
                    }
                    if (stream.hasNext()) {
                        timeLine = stream.next();
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


    static Cue parseCue(String parent, String headLine, Duration offset, String timeLine, String content, String decimalSeparator) {
        String[] split = timeLine.split("\\s+");
        try {
            if (offset == null) {
                offset = Duration.ZERO;
            }
            return new Cue(
                parent,
                Integer.parseInt(headLine),
                parseDuration(split[0], decimalSeparator).minus(offset),
                parseDuration(split[2], decimalSeparator).minus(offset),
                content
            );
        } catch(NumberFormatException nfe) {
            throw new IllegalArgumentException("For " + parent + " could not parse " + timeLine + " (" + Arrays.asList(split) + "). Headline: " + headLine + ". Expected content: " + content + ".  Reason: " + nfe.getClass() + " " + nfe.getMessage(), nfe);
        }

    }

    static void formatSRT(Iterator<? extends Cue> cueIterator, OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out, SRT_CHARSET);
        StringBuilder builder = new StringBuilder();
        while (cueIterator.hasNext()) {
            Cue cue = cueIterator.next();
            if (cue != null) {
                formatCue(cue, builder, ",");
                writer.write(builder.toString());
                builder.setLength(0);
            }
        }
        writer.flush();
    }

    static void formatWEBVTT(Iterator<? extends Cue> cueIterator, OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out, VTT_CHARSET);
        formatWEBVTT(cueIterator, writer);
        writer.flush();
    }

    static void formatWEBVTT(Iterator<? extends Cue> cueIterator, Writer writer) throws IOException {
        writer.write(WEBVTT_INTRO);
        writer.write("\n\n");
        StringBuilder builder = new StringBuilder();
        while (cueIterator.hasNext()) {
            Cue cue = cueIterator.next();
            if (cue != null) {
                formatCue(cue, builder, ".");
                writer.write(builder.toString());
                builder.setLength(0);
            }
        }
    }

    static String formatDuration(Duration duration, String separator) {
        Long millis = duration.toMillis();
        boolean negative = millis < 0L;
        if (negative) {
            millis *= -1;
        }
        Long hours = millis / 3600000;
        millis -= hours * 3600000;
        Long minutes = millis / 60000;
        millis -= minutes * 60000;
        Long seconds = millis / 1000;
        millis -= seconds * 1000;
        return String.format("%s%02d:%02d:%02d%s%03d", negative ? "-" : "",  hours, minutes, seconds, separator, millis);
    }

    static Duration parseDuration(String duration, String decimalSeparator) {
        String[] split = duration.split(":", 3);
        int index = 0;
        Long hours;
        if (split.length == 3) {
            hours = Long.parseLong(split[0]);
            index++;
        } else {
            hours = 0L;
        }
        Long minutes;
        if (split.length >= 2) {
            minutes =  Long.parseLong(split[index]);
            index++;
        } else {
            minutes = 0L;
        }
        String [] split2 = split[index].split(Pattern.quote(decimalSeparator), 2);
        Long seconds = Long.parseLong(split2[0]);
        Long millis = Long.parseLong(split2[1]);
        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds).plusMillis(millis);
    }

    static StringBuilder formatCue(Cue cue, StringBuilder builder, String decimalSeparator) {
        builder.append(cue.getSequence());
        Duration offset = Duration.ZERO;
        builder.append("\n");
        if (cue.getStart() != null) {
            builder.append(formatDuration(cue.getStart().minus(offset), decimalSeparator));
        }
        builder.append(" --> ");
        if (cue.getEnd() != null) {
            builder.append(formatDuration(cue.getEnd().minus(offset), decimalSeparator));
        }
        builder.append("\n");
        if (cue.getContent() != null) {
            builder.append(cue.getContent());//replaceAll("\\s+", " "));
        }
        builder.append("\n\n");
        return builder;
    }

}
