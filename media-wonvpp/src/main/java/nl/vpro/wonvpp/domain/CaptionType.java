package nl.vpro.wonvpp.domain;

import java.util.Locale;

import jakarta.annotation.Nullable;

import org.meeuw.i18n.languages.validation.Language;

public record CaptionType(
    @Nullable Boolean closed,
    @Nullable @Language Locale language,
    @Nullable Boolean supplemental

) {
}
