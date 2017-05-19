package nl.vpro.domain.subtitles;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class TT888Test {
    @Test
    public void parseTimeline() throws Exception {
        String line = "0003 00:02:08:11 00:02:11:06";
        TT888.TimeLine timeLine = TT888.TimeLine.parse(line);
        assertEquals(3, timeLine.getSequence());
        assertEquals((2 * 60 + 8) * 1000L + 110, timeLine.getStart().toMillis());
        assertEquals((2 * 60 + 11) * 1000L + 60, timeLine.getEnd().toMillis());
    }
}
