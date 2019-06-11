package nl.vpro.domain.media.bind;

import be.olsson.i18n.subdivision.SubdivisionFactory;

import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;
import org.meeuw.i18n.CountrySubDivision;
import org.meeuw.i18n.CurrentCountry;

import com.neovisionaries.i18n.CountryCode;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class CountryWrapperTest {
    @Test
    public void getNameUKNL() {
        CountryWrapper wrapper = new CountryWrapper(new CurrentCountry(CountryCode.GB));
        assertThat(wrapper.getName()).isEqualTo("Verenigd Koninkrijk");

    }

    @Test
    public void getNameGBNL() {
        CountryWrapper wrapper = new CountryWrapper(
            new CountrySubDivision(
                SubdivisionFactory.getSubdivision(CountryCode.GB, "GBN")
            )
        );
        assertThat(wrapper.getName()).isEqualTo("Groot-Brittannië");

    }

    @Test
    @Ignore
    public void getNameGBUK() {
        Locale.setDefault(Locale.UK);
        CountryWrapper wrapper = new CountryWrapper(
              new CountrySubDivision(
                SubdivisionFactory.getSubdivision(CountryCode.GB, "GBN")
              )
        );
        assertThat(wrapper.getName()).isEqualTo("Great Britain");

    }

    @Test
    @Ignore
    public void getNameUS() {
        Locale.setDefault(Locale.US);
        CountryWrapper wrapper = new CountryWrapper(new CurrentCountry(CountryCode.GB));
        assertThat(wrapper.getName()).isEqualTo("United Kingdom");

    }


    @Test
    public void getNameENGNL() {
        //Locale.setDefault(Locales.DUTCH);
        CountryWrapper wrapper = new CountryWrapper(
            new CountrySubDivision(
                SubdivisionFactory.getSubdivision(CountryCode.GB, "ENG")
            )
        );
        assertThat(wrapper.getName()).isEqualTo("Engeland");

    }

    @Test
    @Ignore
    public void getNameENGUK() {
        Locale.setDefault(Locale.UK);
        CountryWrapper wrapper = new CountryWrapper(
            new CountrySubDivision(
                SubdivisionFactory.getSubdivision(CountryCode.GB, "ENG")
            )
        );
        assertThat(wrapper.getName()).isEqualTo("England");

    }

    @Test
    public void getNameUTUK() {
        Locale.setDefault(Locale.UK);
        CountryWrapper wrapper = new CountryWrapper(
            new CountrySubDivision(
                SubdivisionFactory.getSubdivision(CountryCode.NL, "UT")
            )
        );
        assertThat(wrapper.getName()).isEqualTo("Utrecht");

    }

}
