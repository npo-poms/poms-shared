package nl.npo.wonvpp.domain;

import java.util.Locale;

import jakarta.annotation.Nullable;

import org.meeuw.i18n.languages.validation.Language;

/**
 *
 * @param closed
 * @param language
 * @param supplemental TODO: Not sure what this is suppose to mean
 */
public record CaptionType(
    @Nullable Boolean closed,
    @Nullable @Language Locale language,

    @Nullable Boolean supplemental

) {
}
