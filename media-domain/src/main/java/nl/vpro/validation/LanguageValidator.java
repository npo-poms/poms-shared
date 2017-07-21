package nl.vpro.validation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.com.neovisionaries.i18n.LanguageAlpha3Code;

import nl.vpro.com.neovisionaries.i18n.LanguageCode;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class LanguageValidator implements ConstraintValidator<Language, MediaObject<?>> {

    // http://www-01.sil.org/iso639-3/documentation.asp?id=zxx
    private static final Set<String> VALID_ISO_LANGUAGES = new HashSet<>();
    private static final Set<String> VALID_ISO3_LANGUAGES = new HashSet<>();
    static {
        VALID_ISO_LANGUAGES.addAll(Arrays.asList(Locale.getISOLanguages()));
        for (LanguageAlpha3Code cod : LanguageAlpha3Code.values()) {
            VALID_ISO3_LANGUAGES.add(cod.toString());
            if (cod.getAlpha2() != null && ! VALID_ISO_LANGUAGES.contains(cod.getAlpha2().name())){
                VALID_ISO_LANGUAGES.add(cod.getAlpha2().name());
            }
            VALID_ISO_LANGUAGES.addAll(Arrays.asList(LanguageCode.LEGACY));
        }
    }

    @Override
    public void initialize(Language constraintAnnotation) {

    }

    @Override
    public boolean isValid(MediaObject<?> value, ConstraintValidatorContext context) {
        for (Locale locale : value.getLanguages()) {
            if (! isValid(locale, context)) {
                return false;
            }
        }
        return true;

    }


    protected boolean isValid(Locale value, ConstraintValidatorContext context) {
        // we don't accept countries and variants (yet)
        return
            (StringUtils.isEmpty(value.getCountry()) && StringUtils.isEmpty(value.getVariant())
                && (
                VALID_ISO3_LANGUAGES.contains(value.getLanguage()) ||
                    VALID_ISO_LANGUAGES.contains(value.getLanguage())));

    }
}
