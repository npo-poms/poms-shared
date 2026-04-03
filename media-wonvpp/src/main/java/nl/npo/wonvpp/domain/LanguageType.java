package nl.npo.wonvpp.domain;

import java.util.Locale;

import jakarta.annotation.Nullable;

import org.meeuw.i18n.languages.Scope;
import org.meeuw.i18n.languages.Type;
import org.meeuw.i18n.languages.validation.Language;

public record LanguageType(
    @Language(scope = Scope.I, type = {Type.L, Type.C}) Locale language,
    @Nullable String usage
)  {

}
