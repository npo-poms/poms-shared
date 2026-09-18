package nl.vpro.domain.media.bind;

import java.util.Locale;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.meeuw.i18n.countries.CurrentCountry;
import org.meeuw.i18n.subdivisions.CountrySubdivision;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meeuw.i18n.countries.Country.of;
import static org.meeuw.i18n.countries.codes.CountryCode.GB;
import static org.meeuw.i18n.countries.codes.CountryCode.NL;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
class CountryWrapperTest {

    @Test
    void getNameUKNL() {
        CountryWrapper wrapper = new CountryWrapper(of(GB));
        assertThat(wrapper.getName()).isEqualTo("Verenigd Koninkrijk");
    }

    @Test
    void getNameGBNL() {
        CountryWrapper wrapper = new CountryWrapper(CountrySubdivision.of(GB, "GBN").get());
        assertThat(wrapper.getName()).isEqualTo("Groot-Brittannië");
    }

    @Test
    @Disabled("name is hard coded to be always dutch")
    void getNameGBUK() {
        Locale.setDefault(Locale.UK);
        CountryWrapper wrapper = new CountryWrapper(CountrySubdivision.of(GB, "GBN").get());
        assertThat(wrapper.getName()).isEqualTo("Great Britain");
    }

    @Test
    @Disabled
    void getNameUS() {
        Locale.setDefault(Locale.US);
        CountryWrapper wrapper = new CountryWrapper(CurrentCountry.of(GB));
        assertThat(wrapper.getName()).isEqualTo("United Kingdom");
    }


    @Test
    void getNameENGNL() {
        //Locale.setDefault(Locales.DUTCH);
        CountryWrapper wrapper = new CountryWrapper(CountrySubdivision.of(GB, "ENG").get());
        assertThat(wrapper.getName()).isEqualTo("Engeland");
    }

    @Test
    @Disabled("name is hard coded to be always dutch")
    void getNameENGUK() {
        Locale.setDefault(Locale.UK);
        CountryWrapper wrapper = new CountryWrapper(CountrySubdivision.of(GB, "ENG").get());
        assertThat(wrapper.getName()).isEqualTo("England");
    }

    @Test
    @Disabled("name is hard coded to be always dutch")
    void getNameUTUK() {
        Locale.setDefault(Locale.UK);
        CountryWrapper wrapper = new CountryWrapper(CountrySubdivision.of(NL, "UT").get());
        assertThat(wrapper.getName()).isEqualTo("Utrecht");
    }

}
