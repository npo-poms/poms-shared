package nl.vpro.domain.media.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import nl.vpro.com.neovisionaries.i18n.CountryCode;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class CountryCodeAdapter extends XmlAdapter<CountryWrapper, CountryCode> {

    @Override
    public CountryCode unmarshal(CountryWrapper v) {
        return v == null ? null : v.getCode();
    }

    @Override
    public CountryWrapper marshal(CountryCode v)  {
        return v == null ? null : new CountryWrapper(v);
    }

    public static class Code extends XmlAdapter<String, CountryCode> {
        @Override
        public CountryCode unmarshal(String v) throws Exception {

            CountryCode countryCode = CountryCode.getByCode(v);
            if (countryCode == null) {
                throw new IllegalArgumentException("No such country " + v);
            }
            return countryCode;

        }

        @Override
        public String marshal(CountryCode v) throws Exception {
            return v == null ? null : v.name();
        }
    }
}
