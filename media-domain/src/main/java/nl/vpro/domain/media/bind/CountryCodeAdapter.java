package nl.vpro.domain.media.bind;

import java.util.Optional;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.meeuw.i18n.regions.Region;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class CountryCodeAdapter extends XmlAdapter<CountryWrapper, Region> {

    @Override
    public Region unmarshal(CountryWrapper countryWrapper) {
        return Optional.ofNullable(countryWrapper)
                .map(CountryWrapper::getCode)
                .orElse(null);
    }

    @Override
    public CountryWrapper marshal(Region v)  {
        return Optional.ofNullable(v)
                .map(CountryWrapper::new)
                .orElse(null);
    }


}
