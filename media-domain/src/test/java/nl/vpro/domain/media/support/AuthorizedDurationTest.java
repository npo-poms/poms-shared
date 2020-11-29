package nl.vpro.domain.media.support;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public class AuthorizedDurationTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testGetDurationDeprecated() {
        AuthorizedDuration duration = new AuthorizedDuration(new Date(100 * 1000));
        assertThat(duration.get().get(ChronoUnit.SECONDS)).isEqualTo(100);

    }

    @Test
    public void testGetDuration() {
        AuthorizedDuration duration = new AuthorizedDuration(Duration.ofMillis(100 * 1000));
        assertThat(duration.get().get(ChronoUnit.SECONDS)).isEqualTo(100);

    }

    @Test
    public void testOf() {
        AuthorizedDuration duration = AuthorizedDuration.of(100, ChronoUnit.SECONDS);
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
    public void testUnits() {
        AuthorizedDuration duration = AuthorizedDuration.ofMillis(1000);
        assertThat(duration.getUnits()).containsExactly(ChronoUnit.SECONDS, ChronoUnit.NANOS);
    }

    @Test
    public void xml() {
        AuthorizedDuration result = JAXBTestUtil.roundTripAndSimilar(AuthorizedDuration.of(186010, ChronoUnit.MILLIS),
            "<local:authorizedDuration xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:local=\"uri:local\">P0DT0H3M6.010S</local:authorizedDuration>"
        );
        assertThat(result.get().toMillis()).isEqualTo(186010);

    }

    @Test
    public void json() {
        AuthorizedDuration result =  Jackson2TestUtil.roundTripAndSimilarValue(
            AuthorizedDuration.of(185010, ChronoUnit.MILLIS),
            "185010"
        );
        assertThat(result.get().toMillis()).isEqualTo(185010);
    }

}
