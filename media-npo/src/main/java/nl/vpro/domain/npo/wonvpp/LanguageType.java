package nl.vpro.domain.npo.wonvpp;

import jakarta.annotation.Nullable;

import org.meeuw.i18n.languages.*;
import org.meeuw.i18n.languages.validation.Language;

public record LanguageType(
    @Language(scope = Scope.I, type = {Type.L, Type.C}) ISO_639_Code language,
    @Nullable String usage
)  {

}
