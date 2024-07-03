package nl.vpro.berlijn.domain.productmetadata;

import org.meeuw.i18n.languages.LanguageCode;

public record CaptionLanguage(
    boolean supplemental,
    boolean closed,
    LanguageCode language
) {
}
