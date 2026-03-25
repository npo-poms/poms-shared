package nl.vpro.domain.npo.wonvpp;

import jakarta.annotation.Nullable;

import org.meeuw.i18n.languages.ISO_639_Code;
import org.meeuw.i18n.languages.validation.Language;

public record LanguageType(
    @Language ISO_639_Code language,
    @Nullable String usage
)  {


}
