package nl.vpro.domain.constraint;

import java.io.Serial;
import java.util.Locale;

public class LocalizedString extends nl.vpro.i18n.LocalizedString {

    @Serial
    private static final long serialVersionUID = -20553709108210246L;

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
}
