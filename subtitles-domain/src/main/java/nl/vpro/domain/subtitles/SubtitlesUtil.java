package nl.vpro.domain.subtitles;

import java.io.*;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public class SubtitlesUtil {

    private static Logger LOG = LoggerFactory.getLogger(SubtitlesUtil.class);


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
        return parseEBU(parent, new InputStreamReader(inputStream, Charset.forName("ISO-6937")));
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
                    LOG.error(e.getMessage(), e);
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
}
