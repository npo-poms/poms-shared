package nl.vpro.domain.npo.wonvpp;

import jakarta.annotation.Nullable;

import org.meeuw.i18n.languages.validation.Language;

public record CaptionType(
    @Nullable Boolean closed,
    @Nullable @Language String language,
    @Nullable Boolean supplemental

) {
}
