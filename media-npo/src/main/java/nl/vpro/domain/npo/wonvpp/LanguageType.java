package nl.vpro.domain.npo.wonvpp;

import jakarta.annotation.Nullable;

import org.meeuw.i18n.languages.validation.Language;

public record LanguageType(
    @Language String language,
    @Nullable String usage
)  {


}
