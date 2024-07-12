package nl.vpro.domain.media;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.i18n.languages.LanguageCode;
import org.meeuw.i18n.languages.validation.Language;

import nl.vpro.i18n.Displayable;
import nl.vpro.i18n.LocalizedString;
import nl.vpro.validation.WarningValidatorGroup;

/**
 * Combines a language (as a {@link Locale}) with its role for a {@link MediaObject}, as a {@link Usage}
 * @since 8.2
 */
@XmlType(name = "usedLanguageType")
@Embeddable
public record UsedLanguage (

    @PomsValidCountry(groups = WarningValidatorGroup.class)
    @Language(mayContainCountry = true, groups = WarningValidatorGroup.class)
    @Column(name = "languages")
    @XmlAttribute(name = "code", required = true)
    @NonNull Locale code,

    @Column
    @Enumerated(EnumType.STRING)
    @NonNull Usage usage

) implements Displayable, Serializable {

    public static UsedLanguage of(Locale locale) {
        return new UsedLanguage(locale, Usage.AUDIODESCRIPTION);
    }

    public static UsedLanguage of(LanguageCode code) {
        return new UsedLanguage(code.toLocale(), Usage.AUDIODESCRIPTION);
    }

    public static UsedLanguage dubbed(LanguageCode code) {
        return new UsedLanguage(code.toLocale(), Usage.DUBBED);
    }

    public static UsedLanguage of(String code) {
        return of(LanguageCode.get(code).orElseThrow());
    }

    public static List<UsedLanguage> of(List<Locale> locale) {
        return locale.stream().map(UsedLanguage::of).collect(Collectors.toList());
    }

    public static List<UsedLanguage> asList(Locale... locale) {
        return Arrays.stream(locale).map(UsedLanguage::of).collect(Collectors.toList());
    }

    @Override
    public LocalizedString getDisplayName(Locale locale) {
        return LocalizedString.of(code().getDisplayName(locale), locale);
    }

    /**
     * Just to map the enum of kafka objects.
     */
    public static Usage usageOf(Enum<?> value) {
        if (value == null) {
            return Usage.AUDIODESCRIPTION;
        }
        return Usage.valueOf(value.name().toUpperCase());
    }


    @XmlType(name = "languageUsageEnum")
    public enum Usage {
        /**
         * The language is spoken
         */
        AUDIODESCRIPTION,

        /**
         * The language is used for dubbing
         */
        DUBBED,

        /**
         * ?
         */
        SUPPLEMENTAL,

        /**
         * The (sign) language is used as a translation
         * For subtitles see {@link AvailableSubtitles}
         */
        SIGNING
    }


}
