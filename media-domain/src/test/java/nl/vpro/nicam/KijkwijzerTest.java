package nl.vpro.nicam;


import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.AgeRating;
import nl.vpro.domain.media.ContentRating;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class KijkwijzerTest {

    @Test
    public void parse() {
        roundTrip("", new Kijkwijzer(null));
        roundTrip("2", new Kijkwijzer(AgeRating._6));
        roundTrip("3", new Kijkwijzer(AgeRating._9));
        roundTrip("4", new Kijkwijzer(AgeRating._12));
        roundTrip("5", new Kijkwijzer(AgeRating._16));
        roundTrip("2ahsg", new Kijkwijzer(AgeRating._6, ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL, ContentRating.SEKS, ContentRating.GEWELD));
        roundTrip("4hsgd", new Kijkwijzer(AgeRating._12, ContentRating.DRUGS_EN_ALCOHOL, ContentRating.SEKS, ContentRating.GEWELD, ContentRating.DISCRIMINATIE));

    }

    @Test
    public void donna() {
        roundTripDonna("1", new Kijkwijzer(AgeRating.ALL));
        roundTripDonna("2", new Kijkwijzer(AgeRating._6));
        roundTripDonna("3", new Kijkwijzer(AgeRating._12));
        roundTripDonna("4", new Kijkwijzer(AgeRating._16));
        roundTripDonna("5", new Kijkwijzer(AgeRating._9));
        roundTripDonna("2ahsg", new Kijkwijzer(AgeRating._6, ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL, ContentRating.SEKS, ContentRating.GEWELD));
    }

    protected void roundTripDonna(String code, Kijkwijzer kijkwijzer) {
        assertThat(kijkwijzer.toDonnaCode()).isEqualTo(code);
        assertThat(Kijkwijzer.parseDonna(code)).isEqualTo(kijkwijzer);
    }

    protected void roundTrip(String code, Kijkwijzer kijkwijzer) {
        assertThat(kijkwijzer.toCode()).isEqualTo(code);
        assertThat(Kijkwijzer.parse(code)).isEqualTo(kijkwijzer);
    }
}
