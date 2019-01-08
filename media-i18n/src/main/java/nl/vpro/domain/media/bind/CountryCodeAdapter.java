package nl.vpro.domain.media.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import nl.vpro.com.neovisionaries.i18n.CountryCode;

import java.util.Optional;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class CountryCodeAdapter extends XmlAdapter<CountryWrapper, CountryCode> {

    @Override
    public CountryCode unmarshal(CountryWrapper countryWrapper) {
        return Optional.ofNullable(countryWrapper)
                .map(CountryWrapper::getCode)
                .orElse(null);
    }

    @Override
    public CountryWrapper marshal(CountryCode v)  {
        return Optional.ofNullable(v)
                .map(CountryWrapper::new)
                .orElse(null);
    }

    public static class Code extends XmlAdapter<String, CountryCode> {
        @Override
        public CountryCode unmarshal(String v) {
            return Optional.ofNullable(CountryCode.getByCode(v))
                    .orElseThrow(() -> new IllegalArgumentException("No such country " + v));
        }

        @Override
        public String marshal(CountryCode countryCode) {
            return Optional.ofNullable(countryCode)
                    .map(Enum::name)
                    .orElse(null);
        }
    }
}
