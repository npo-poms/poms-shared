package nl.vpro.domain.media.bind;



import java.util.Locale;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
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
