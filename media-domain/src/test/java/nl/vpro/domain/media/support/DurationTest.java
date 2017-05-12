package nl.vpro.domain.media.support;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public class DurationTest {

    @Test
    public void testGetDuration() throws Exception {
        Duration duration = new Duration(new Date(100 * 1000));
        assertThat(duration.get().get(ChronoUnit.SECONDS)).isEqualTo(100);

    }

    @Test
    public void testOf() throws Exception {
        Duration duration = Duration.of(100, ChronoUnit.SECONDS);
        assertThat(duration.get().get(ChronoUnit.SECONDS)).isEqualTo(100);
    }
/*

    @Test
    public void testOfPeriod() throws Exception {
        Duration duration = Duration.ofTemporalAmount(Period.ofDays(1));
        assertThat(duration.get().get(ChronoUnit.SECONDS)).isEqualTo(86400L);
    }
*/


    @Test
    public void testUnits() throws Exception {
        Duration duration = Duration.ofMillis(1000);
        assertThat(duration.getUnits()).containsExactly(ChronoUnit.SECONDS, ChronoUnit.NANOS);
    }

    @Test
    public void xml() throws IOException, SAXException {
        Duration result = JAXBTestUtil.roundTripAndSimilar(Duration.of(186010, ChronoUnit.MILLIS),
            "<local:duration xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:local=\"uri:local\">P0DT0H3M6.010S</local:duration>"
        );
        assertThat(result.get().toMillis()).isEqualTo(186010);

    }

    @Test
    public void json() throws Exception {
        Duration result =  Jackson2TestUtil.roundTripAndSimilarValue(
            Duration.of(185010, ChronoUnit.MILLIS),
            "185010"
        );
        assertThat(result.get().toMillis()).isEqualTo(185010);
    }

}
