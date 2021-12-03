package nl.vpro.domain.classification;

import java.util.Locale;

/**
 * Extension of {@link nl.vpro.i18n.LocalizedString} in the correct XML namespace
 */

public class LocalizedString extends nl.vpro.i18n.LocalizedString {
    private static final long serialVersionUID = -6545505135867762847L;

    public static LocalizedString copy(nl.vpro.i18n.LocalizedString copy) {
        return  new LocalizedString(
            copy.getValue(),
            copy.getLocale(),
            copy.getCharsetName()
        );
    }

    protected LocalizedString(String value, Locale locale, String charsetName) {
        setValue(value);
        setLocale(locale);
        setCharsetName(charsetName);
    }

    public static LocalizedString of(String value, Locale locale) {
         if (value == null) {
            return null;
        } else {
            return new LocalizedString(value, locale, null);
        }
    }

    public LocalizedString() {

    }


    /**
     * Deprecated. Just use {@link nl.vpro.i18n.LocalizedString#get(Locale, Iterable)}
     */
    @Deprecated
    static String get_(Locale locale, Iterable<? extends nl.vpro.i18n.LocalizedString> strings) {
        return get(locale, strings);
    }

}
