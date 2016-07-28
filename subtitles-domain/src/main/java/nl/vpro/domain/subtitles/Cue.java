package nl.vpro.domain.subtitles;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* @author Michiel Meeuwissen
* @since 1.8
*/
public class Cue {

    private static Logger LOG = LoggerFactory.getLogger(Cue.class);

    static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UT"));
    }

    public final String parent;
    public final int sequence;
    public final Duration start;
    public final Duration end;
    public final String content;

    private Cue(String parent, int sequence, Duration start, Duration end, String content) {
        this.parent = parent;
        this.sequence = sequence;
        this.start = start;
        this.end = end;
        this.content = content;
    }


    public static Cue parse(String parent, String timeLine, String content) throws ParseException {
        String[] split = timeLine.split("\\s+");
        return new Cue(
            parent,
            Integer.parseInt(split[0]),
            Duration.ofMillis(dateFormat.parse(split[1] + "0").getTime()),
            Duration.ofMillis(dateFormat.parse(split[2] + "0").getTime()),
            content
        );

    }


}
