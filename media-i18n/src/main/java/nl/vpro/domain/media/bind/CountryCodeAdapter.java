package nl.vpro.domain.media.bind;

import java.util.Optional;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.meeuw.i18n.Country;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class CountryCodeAdapter extends XmlAdapter<CountryWrapper, Country> {

    @Override
    public Country unmarshal(CountryWrapper countryWrapper) {
        return Optional.ofNullable(countryWrapper)
                .map(CountryWrapper::getCode)
                .orElse(null);
    }

    @Override
    public CountryWrapper marshal(Country v)  {
        return Optional.ofNullable(v)
                .map(CountryWrapper::new)
                .orElse(null);
    }

    public static class Code extends XmlAdapter<String, Country> {
        @Override
        public Country unmarshal(String v) {
            return Country.getByCode(v)
                    .orElseThrow(() -> new IllegalArgumentException("No such country " + v));
        }

        @Override
        public String marshal(Country countryCode) {
            return Optional.ofNullable(countryCode)
                    .map(Country::getISOCode)
                    .orElse(null);
        }
    }
}
