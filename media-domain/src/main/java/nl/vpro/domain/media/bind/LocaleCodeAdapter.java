package nl.vpro.domain.media.bind;


import java.util.Locale;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import nl.vpro.i18n.LocalizedString;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class LocaleCodeAdapter extends XmlAdapter<String, Locale> {


    @Override
    public Locale unmarshal(String v) {
        return LocalizedString.adapt(v);

    }

    @Override
    public String marshal(Locale v) {
        return v.toLanguageTag();
    }


}
