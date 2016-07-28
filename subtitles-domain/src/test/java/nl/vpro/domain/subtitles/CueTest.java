package nl.vpro.domain.subtitles;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public class CueTest {
    @Test
    public void parseTimeline() throws Exception {
        String line = "0003 00:02:08:11 00:02:11:06";
        Cue timeLine = Cue.parse(line, "bla");
        assertEquals(3, timeLine.sequence);
        assertEquals((2 * 60 + 8) * 1000L + 110, timeLine.start.toMillis());
        assertEquals((2 * 60 + 11) * 1000L + 60, timeLine.end.toMillis());
    }

}
