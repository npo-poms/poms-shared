package nl.vpro.domain.media;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class TvaCountryTest {

    @Test
    public void valueOf() {
        assertThatThrownBy(() ->
            TvaCountry.valueOf("BLA")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void iso3() {
        assertThat(TvaCountry.find("NLD")).isEqualTo(TvaCountry.NL);
    }
}
