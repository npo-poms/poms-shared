package nl.vpro.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

import nl.vpro.com.neovisionaries.i18n.CountryCode;
import nl.vpro.com.neovisionaries.i18n.LanguageCode;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class Locales {

    public static Locale DUTCH   = of(LanguageCode.nl, CountryCode.NL);
    public static Locale FLEMISH = of(LanguageCode.nl, CountryCode.BE);

    private static final ThreadLocal<Locale> DEFAULT = ThreadLocal.withInitial(Locale::getDefault);



    public static Locale getDefault() {
        return DEFAULT.get();
    }
    public static void setDefault(Locale locale) {
        DEFAULT.set(locale);
    }
    public static void resetDefault() {
        DEFAULT.remove();
    }

    public static Locale of(LanguageCode lc, CountryCode code) {
        return new Locale(lc.name(), code.getAlpha2());
    }



    public static String getCountryName(CountryCode code, Locale locale) {
        if (code.getIso3166_2() != null) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle("CountryCode", locale);
                return bundle.getString(code.getIso3166_2());
            } catch(MissingResourceException mre) {
                return code.getName();
            }
        }  else {
            CountryCode byAlpha3 = null;
            if (code.getAssignment() != CountryCode.Assignment.OFFICIALLY_ASSIGNED) {
                if (code.getAlpha3() != null) {
                    for (CountryCode c : CountryCode.values()) {
                        if (Objects.equals(code.getAlpha3(), c.getAlpha3()) && c.getAssignment() == CountryCode.Assignment.OFFICIALLY_ASSIGNED) {
                            byAlpha3 = c;
                            break;
                        }
                    }
                }
            }
            if (byAlpha3 != null && byAlpha3 != code) {
                return getCountryName(byAlpha3, locale);
            }
            return code.toLocale().getDisplayCountry(locale);
        }
    }

}
