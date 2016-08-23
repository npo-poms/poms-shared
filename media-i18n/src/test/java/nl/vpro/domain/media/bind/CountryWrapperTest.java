package nl.vpro.domain.media.bind;

import java.util.Locale;

import org.junit.Test;

import nl.vpro.com.neovisionaries.i18n.CountryCode;
import nl.vpro.i18n.Locales;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class CountryWrapperTest {
    @Test
    public void getNameUKNL() throws Exception {
        Locale.setDefault(Locales.DUTCH);
        CountryWrapper wrapper = new CountryWrapper(CountryCode.GB);
        assertThat(wrapper.getName()).isEqualTo("Verenigd Koninkrijk");
        
    }

    @Test
    public void getNameGBNL() throws Exception {
        Locale.setDefault(Locales.DUTCH);
        CountryWrapper wrapper = new CountryWrapper(CountryCode.GB_GBN);
        assertThat(wrapper.getName()).isEqualTo("Groot-Brittannië");

    }

    @Test
    public void getNameUS() throws Exception {
        Locale.setDefault(Locale.US);
        CountryWrapper wrapper = new CountryWrapper(CountryCode.GB);
        assertThat(wrapper.getName()).isEqualTo("United Kingdom");

    }

}