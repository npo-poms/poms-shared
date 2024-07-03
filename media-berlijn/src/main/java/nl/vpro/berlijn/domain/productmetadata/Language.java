package nl.vpro.berlijn.domain.productmetadata;

import org.meeuw.i18n.languages.LanguageCode;

public record Language(
    LanguageCode language,
    Usage usage
) {


    enum Usage {
        audiodescription,
        dubbed,
        supplemental
    }
}
