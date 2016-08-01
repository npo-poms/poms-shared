package nl.vpro.domain.media;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

/**
 * Tests that PublicationUpdate#getDelay is consistent with PublicationUpdate#compareTo
 *
 * @author Michiel Meeuwissen
 */
@RunWith(Theories.class)
public class PublicationUpdateTheoryTest {

    private static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;

    @DataPoint
    public static PublicationUpdate now = new PublicationUpdate(PublicationUpdate.Action.PUBLISH,  "MID_1", 1L, Instant.now());

    @DataPoint
    public static PublicationUpdate tomorrow = new PublicationUpdate(PublicationUpdate.Action.PUBLISH, "MID_3", 3L, Instant.ofEpochMilli(System.currentTimeMillis() + DAY_IN_MILLIS));

    @DataPoint
    public static PublicationUpdate yesterday = new PublicationUpdate(PublicationUpdate.Action.PUBLISH, "MID_4", 4L, Instant.ofEpochMilli(System.currentTimeMillis() - DAY_IN_MILLIS));

    @DataPoint
    public static PublicationUpdate minLong = new PublicationUpdate(PublicationUpdate.Action.PUBLISH, "MID_5", 5L, Instant.ofEpochMilli(Integer.MIN_VALUE));

    @DataPoint
    public static PublicationUpdate maxLong = new PublicationUpdate(PublicationUpdate.Action.PUBLISH, "MID_6", 6L, Instant.ofEpochMilli(Integer.MAX_VALUE));

    @DataPoint
    public static PublicationUpdate _1990;

    static {
        try {
            _1990 =
                new PublicationUpdate(PublicationUpdate.Action.PUBLISH, "MID_7", 7L, new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("1990-12-25 17:47:00").toInstant());
        } catch(ParseException e) {
        }
    }

    @Theory
    public void testCompareToWhenSooner(PublicationUpdate update1, PublicationUpdate update2) {
        assumeTrue(update1.getTime().toEpochMilli() < update2.getTime().toEpochMilli());
        assertThat(update1.compareTo(update2)).isLessThan(0);
    }

    @Theory
    public void testCompareToWhenEqual(PublicationUpdate update1, PublicationUpdate update2) {
        assumeTrue(update1.getTime().toEpochMilli() == update2.getTime().toEpochMilli());
        assertThat(update1.compareTo(update2)).isEqualTo(0);
    }

    @Theory
    public void testCompareToWhenLater(PublicationUpdate update1, PublicationUpdate update2) {
        assumeTrue(update1.getTime().toEpochMilli() > update2.getTime().toEpochMilli());
        assertThat(update1.compareTo(update2)).isGreaterThan(0);
    }
}
