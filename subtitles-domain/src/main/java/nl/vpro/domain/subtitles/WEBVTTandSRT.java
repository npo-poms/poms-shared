package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.*;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.util.SkipAtStartInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * https://developer.mozilla.org/en-US/docs/Web/API/WebVTT_A
 * TODO: we support only a subset of WEBVTT.
 *
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Slf4j
public class WEBVTTandSRT {

    private WEBVTTandSRT() {
    }


    static final String WEBVTT_INTRO = "WEBVTT";

    public static final Charset SRT_CHARSET = SubtitlesFormat.SRT.getCharset();
    public static final Charset VTT_CHARSET = UTF_8;


    public static ParseResult parseWEBVTT(String parent, InputStream inputStream) {
        return parseWEBVTT(parent, Duration.ZERO, inputStream, null);
    }

    public static Stream<Cue> parseSRT(String parent, InputStream inputStream) {
        return parseSRT(parent, Duration.ZERO, inputStream, null);
    }


    public static ParseResult parseWEBVTT(String parent, Duration offset, InputStream inputStream, Charset charset) {
        return parse(parent,
            offset,
            new InputStreamReader(inputStream, charset == null ? VTT_CHARSET : charset), ".");
    }

    public static Stream<Cue> parseSRT(String parent, Duration offset, InputStream inputStream, Charset charset) {
        if (! UTF_8.equals(charset)) {
            inputStream = SkipAtStartInputStream.skipUnicodeByteOrderMarks(inputStream);
        }
        return parse(parent, offset, new InputStreamReader(inputStream, charset == null ? SRT_CHARSET : charset), ",").getCues();
    }

    /**
     * A WebVTT timestamp consists of the following components, in the given order:
     * <p>
     * Optionally (required if hours is non-zero):
     * Two or more ASCII digits, representing the hours as a base ten integer.
     * // REMARK: We accept 1 too.
     * A U+003A COLON character (:)
     * Two ASCII digits, representing the minutes as a base ten integer in the range 0 ≤ minutes ≤ 59.
     * A U+003A COLON character (:)
     * Two ASCII digits, representing the seconds as a base ten integer in the range 0 ≤ seconds ≤ 59.
     * A U+002E FULL STOP character (.).
     * Three ASCII digits, representing the thousandths of a second seconds-frac as a base ten integer.
     */
    static final String TIMESTAMP = "((?:\\d{1,}:\\d{1,2}:\\d{2}[.,]\\d{3})|(?:\\d{1,2}:\\d{2}[.,]\\d{3}))";
    static final Pattern CUETIMING =
        Pattern.compile(TIMESTAMP + "[ \\t]+-->[ \\t]+" + TIMESTAMP + ".*");

    static ParseResult parse(
        String parent,
        Duration offset,
        Reader reader,
        String decimalSeparator) {

        final List<Meta> headers = new ArrayList<>();
        final Iterator<String> stream = new BufferedReader(reader)
            .lines()
            .iterator();

        Iterator<Cue> cues = new Iterator<Cue>() {

            boolean needsFindNext = true;
            String cueIdentifier;
            String timeLine = null;
            final StringBuilder content = new StringBuilder();
            boolean readIntro = false;

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
                    return parseCue(parent, cueIdentifier, offset, timeLine, content.toString(), decimalSeparator);
                } catch (IllegalArgumentException e) {
                    log.warn("Error: {} while parsing\nheadline:{}\ntimeline:{}", e.getMessage(), cueIdentifier, StringUtils.abbreviate(timeLine, 100));
                    return null;
                }

            }

            private void findNext() {
                if (needsFindNext) {
                    cueIdentifier= null;
                    timeLine = null;
                    content.setLength(0);

                    String headLine = null;
                    while (stream.hasNext()) {
                        String l = stream.next();
                        if (StringUtils.isNotBlank(l)) {
                            if (! readIntro) {
                                if (WEBVTT_INTRO.equals(l.trim())) {
                                    StringBuilder content = new StringBuilder();
                                    while (stream.hasNext()) {
                                        l = stream.next();
                                        if (StringUtils.isBlank(l)) {
                                            readIntro = true;
                                        } else {
                                            log.debug("Read {}", l);
                                            if (readIntro) {
                                                break;
                                            } else {
                                                content.append(l);
                                            }
                                        }
                                    }
                                    if (content.length() > 0) {
                                        headers.add(
                                            Meta.builder().content(content.toString()).type(MetaType.INTRO).build()
                                        );
                                    }
                                    while (StringUtils.isBlank(l) && stream.hasNext()) {
                                        l = stream.next();
                                    }
                                }
                            }
                            readIntro = true;

                            while (l.startsWith("NOTE") || l.startsWith("STYLE") || l.startsWith("REGION")) {
                                while (stream.hasNext()) {
                                    l = stream.next();
                                    if (StringUtils.isBlank(l)) {
                                        break;
                                    }

                                }
                                while (StringUtils.isBlank(l) && stream.hasNext()) {
                                    l = stream.next();
                                }
                            }
                            headLine = l.trim();
                            break;
                        }
                    }
                    if (headLine != null) {
                        if (! CUETIMING.matcher(headLine).matches()) {
                            cueIdentifier = headLine;
                            if (stream.hasNext()) {
                                timeLine = stream.next();
                            }
                        } else {
                            timeLine = headLine;
                        }
                    }

                    // now read conent

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
        return ParseResult.of(
            StreamSupport.stream(Spliterators.spliteratorUnknownSize(cues, Spliterator.ORDERED), false),
            headers);


    }



    static Cue parseCue(String parent, String cueNumber, Duration offset, String timeLine, String content, String decimalSeparator) {
        String[] split = timeLine.split("\\s+", 4);
        try {
            if (offset == null) {
                offset = Duration.ZERO;
            }
            if (split.length < 3) {
                throw new IllegalArgumentException("Timeline could not be parsed " + timeLine);
            }
            String start = split[0];
            String arrow = split[1];
            String end = split[2];
            String settings = split.length > 3 ? split[3] :null;
            Integer sequence;
            try {
                sequence = Integer.parseInt(cueNumber);
            } catch (NumberFormatException nfe) {
                sequence = null;
            }
            return Cue.builder()
                .mid(parent)
                .identifier(cueNumber)
                .sequence(sequence)
                .start(parseDuration(start, decimalSeparator).minus(offset))
                .end(parseDuration(end, decimalSeparator).minus(offset))
                .settings(CueSettings.webvtt(settings))
                .content(content)
                .build();
        } catch(NumberFormatException nfe) {
            throw new IllegalArgumentException("For " + parent + " could not parse timeline " + StringUtils.abbreviate(timeLine, 100) +
                " (" + Arrays.stream(split).limit(20).map(s ->  StringUtils.abbreviate(s, 100)).collect(Collectors.joining(","))
                + "). Headline: " + cueNumber + ". Expected content: " + StringUtils.abbreviate(content, 100) + ".  Reason: " + nfe.getClass() + " " + nfe.getMessage(), nfe);
        }

    }

    static void formatSRT(Iterator<? extends Cue> cueIterator, OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out, SRT_CHARSET);
        StringBuilder builder = new StringBuilder();
        while (cueIterator.hasNext()) {
            Cue cue = cueIterator.next();
            if (cue != null) {
                formatCue(cue, builder, ",", false);
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
                formatCue(cue, builder, ".", true);
                writer.write(builder.toString());
                builder.setLength(0);
            }
        }
    }

    static String formatDuration(Duration duration, String separator) {
        long millis = duration.toMillis();
        boolean negative = millis < 0L;
        if (negative) {
            millis *= -1;
        }
        long hours = millis / 3600000;
        millis -= hours * 3600000;
        long minutes = millis / 60000;
        millis -= minutes * 60000;
        long seconds = millis / 1000;
        millis -= seconds * 1000;
        return String.format("%s%02d:%02d:%02d%s%03d", negative ? "-" : "",  hours, minutes, seconds, separator, millis);
    }

    static Duration parseDuration(String duration, String decimalSeparator) {
        boolean negative = false;
        if (duration.startsWith("-")) {
            negative = true;
            duration = duration.substring(1);
        }
        String[] split = duration.split(":", 3);
        int index = 0;
        long hours;
        if (split.length == 3) {
            hours = Long.parseLong(split[0]);
            index++;
        } else {
            hours = 0L;
        }
        long minutes;
        if (split.length >= 2) {
            minutes =  Long.parseLong(split[index]);
            index++;
        } else {
            minutes = 0L;
        }
        String [] split2 = split[index].split(Pattern.quote(decimalSeparator), 2);
        long seconds = Long.parseLong(split2[0]);
        long millis = Long.parseLong(split2[1]);
        Duration result =  Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds).plusMillis(millis);
        if (negative) {
            result = result.negated();
        }
        return result;
    }

    static StringBuilder formatCue(Cue cue, StringBuilder builder, String decimalSeparator, boolean settings) {
        if (cue.getSequence() != null) {
            builder.append(cue.getSequence());
        }
        Duration offset = Duration.ZERO;
        builder.append("\n");
        if (cue.getStart() != null) {
            builder.append(formatDuration(cue.getStart().minus(offset), decimalSeparator));
        }
        builder.append(" --> ");
        if (cue.getEnd() != null) {
            builder.append(formatDuration(cue.getEnd().minus(offset), decimalSeparator));
        }
        if (settings && cue.getSettings() != null && StringUtils.isNotBlank(cue.getSettings().getValue())) {
            builder.append(" ").append(cue.getSettings().getValue());
        }
        builder.append("\n");
        if (cue.getContent() != null) {
            builder.append(cue.getContent());//replaceAll("\\s+", " "));
        }
        builder.append("\n\n");
        return builder;
    }


}
