package nl.vpro.domain.classification;

import java.util.Locale;

import static nl.vpro.i18n.Locales.score;

public class LocalizedString extends nl.vpro.i18n.LocalizedString {
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


    @Deprecated
    static String get_(Locale locale, Iterable<? extends nl.vpro.i18n.LocalizedString> strings) {
        nl.vpro.i18n.LocalizedString candidate = null;
        if (strings != null) {
            int score = -1;
            for (nl.vpro.i18n.LocalizedString string : strings) {
                int s = score(string.getLocale(), locale);
                if (s > score) {
                    candidate = string;
                    score = s;
                }
            }
        }
        return candidate == null ? null : candidate.getValue();

    }
}
