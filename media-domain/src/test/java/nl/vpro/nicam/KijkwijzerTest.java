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

    @Test
    public void padded() {
        roundTripPadded("-1", new Kijkwijzer(AgeRating.NOT_YET_RATED));

        roundTripPadded("00", new Kijkwijzer(AgeRating.ALL));
        roundTripPadded("06", new Kijkwijzer(AgeRating._6));
        roundTripPadded("12", new Kijkwijzer(AgeRating._12));
        roundTripPadded("16", new Kijkwijzer(AgeRating._16));
        roundTripPadded("09", new Kijkwijzer(AgeRating._9));
        roundTripPadded("06ahsg", new Kijkwijzer(AgeRating._6, ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL, ContentRating.SEKS, ContentRating.GEWELD));
    }

    @Test
    public void paddedOrDonna() {


        parsePaddedOrDonna("1", new Kijkwijzer(AgeRating.ALL));
        parsePaddedOrDonna("2", new Kijkwijzer(AgeRating._6));
        parsePaddedOrDonna("3", new Kijkwijzer(AgeRating._12));
        parsePaddedOrDonna("4", new Kijkwijzer(AgeRating._16));
        parsePaddedOrDonna("5", new Kijkwijzer(AgeRating._9));
        parsePaddedOrDonna("2ahsg", new Kijkwijzer(AgeRating._6, ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL, ContentRating.SEKS, ContentRating.GEWELD));
        parsePaddedOrDonna("-1", new Kijkwijzer(AgeRating.NOT_YET_RATED));
        parsePaddedOrDonna("00", new Kijkwijzer(AgeRating.ALL));
        parsePaddedOrDonna("06", new Kijkwijzer(AgeRating._6));
        parsePaddedOrDonna("12", new Kijkwijzer(AgeRating._12));
        parsePaddedOrDonna("16", new Kijkwijzer(AgeRating._16));
        parsePaddedOrDonna("09", new Kijkwijzer(AgeRating._9));
        parsePaddedOrDonna("06ahsg", new Kijkwijzer(AgeRating._6, ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL, ContentRating.SEKS, ContentRating.GEWELD));
    }


    protected void roundTripDonna(String code, Kijkwijzer kijkwijzer) {
        assertThat(kijkwijzer.toDonnaCode()).isEqualTo(code);
        assertThat(Kijkwijzer.parseDonna(code)).isEqualTo(kijkwijzer);
    }

    protected void roundTrip(String code, Kijkwijzer kijkwijzer) {
        assertThat(kijkwijzer.toCode()).isEqualTo(code);
        assertThat(Kijkwijzer.parse(code)).isEqualTo(kijkwijzer);
    }

    protected void roundTripPadded(String code, Kijkwijzer kijkwijzer) {
        assertThat(kijkwijzer.toPaddedCode()).isEqualTo(code);
        assertThat(Kijkwijzer.parsePaddedCode(code).get()).isEqualTo(kijkwijzer);
    }

    protected Kijkwijzer parsePaddedOrDonna(String code, Kijkwijzer kijkwijzer) {
        Kijkwijzer result =
            Kijkwijzer.parsePaddedCode(code)
                .orElseGet(() -> Kijkwijzer.parseDonna(code));
        assertThat(result).isEqualTo(kijkwijzer);
        return result;
    }
}
