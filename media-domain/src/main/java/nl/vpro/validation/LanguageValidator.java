package nl.vpro.validation;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.neovisionaries.i18n.LanguageAlpha3Code;


/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class LanguageValidator implements ConstraintValidator<Language, Object> {

    public static final String[] LEGACY = {"iw", "ji", "in", "jw"};

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
            VALID_ISO_LANGUAGES.addAll(Arrays.asList(LEGACY));
        }
    }

    @Override
    public void initialize(Language constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        List<Locale> invalids = new ArrayList<>();
        for (Locale locale : toCollectionOfLocales(value)) {
            if (!isValid(locale, context)) {
                invalids.add(locale);
            }
        }
        if (!invalids.isEmpty()) {
            context.disableDefaultConstraintViolation();
            String template = context.getDefaultConstraintMessageTemplate();
            context.buildConstraintViolationWithTemplate(template + "" + invalids.stream().map(Locale::toString).collect(Collectors.joining(", "))).addConstraintViolation();
            return false;
        } else {
            return true;
        }
    }

    protected Collection<Locale> toCollectionOfLocales(Object o) {
        List<Locale> result = new ArrayList<>();
        if (o != null) {
            if (o instanceof Collection) {
                for (Object s : (Collection) o) {
                    result.add(toLocale(s));
                }
            } else {
                result.add(toLocale(o));
            }
        }
        return result;
    }

    protected Locale toLocale(Object o) {
        if (o instanceof Locale) {
            return (Locale) o;
        } else {
            return Locale.forLanguageTag(o.toString());
        }
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
