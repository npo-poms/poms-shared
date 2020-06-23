package nl.vpro.domain.media.bind;

import java.util.Optional;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.meeuw.i18n.regions.Region;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class CountryCodeAdapter extends XmlAdapter<CountryWrapper, Region> {

    @Override
    @Nullable
    public Region unmarshal(@Nullable CountryWrapper countryWrapper) {
        return Optional.ofNullable(countryWrapper)
                .map(CountryWrapper::getCode)
                .orElse(null);
    }

    @Override
    @Nullable
    public CountryWrapper marshal(@Nullable Region v)  {
        return Optional.ofNullable(v)
                .map(CountryWrapper::new)
                .orElse(null);
    }


}
