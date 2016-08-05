package nl.vpro.domain.subtitles;

import java.io.*;
import java.text.ParseException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.io.IOUtils;
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
        return parse(subtitles.getMid(), new StringReader(subtitles.getContent()));
    }


    public static Stream<Cue> parse(String parent, InputStream inputStream) throws UnsupportedEncodingException {
        return parse(parent, new InputStreamReader(inputStream, "ISO-6937"));
    }

    public static Stream<Cue> parse(final String parent, Reader reader) {
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
