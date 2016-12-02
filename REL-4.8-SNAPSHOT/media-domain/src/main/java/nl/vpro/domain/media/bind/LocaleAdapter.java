package nl.vpro.domain.media.bind;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */

import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocaleAdapter extends XmlAdapter<LocaleWrapper, Locale> {


    @Override
    public Locale unmarshal(LocaleWrapper v) {
        return v.getLocale();

    }

    @Override
    public LocaleWrapper marshal(Locale v) {
        return new LocaleWrapper(v);

    }


}
