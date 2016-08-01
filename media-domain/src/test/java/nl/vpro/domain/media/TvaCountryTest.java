package nl.vpro.domain.media;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class TvaCountryTest {

    @Test(expected = IllegalArgumentException.class)
    public void valueOf() {
        TvaCountry.valueOf("BLA");
    }

    @Test
    public void iso3() {
        assertThat(TvaCountry.find("NLD")).isEqualTo(TvaCountry.NL);
    }
}
