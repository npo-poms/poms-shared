package nl.vpro.domain.media.bind;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import nl.vpro.domain.media.TvaCountry;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class TvaCountryAdapter extends XmlAdapter<String, TvaCountry> {
    @Override
    public TvaCountry unmarshal(String v) {
        return TvaCountry.find(v);
    }

    @Override
    public String marshal(TvaCountry v) {
        return v.name();
    }
}
