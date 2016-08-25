package nl.vpro.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
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
            return code.toLocale().getDisplayCountry(locale);
        }
    }

}
