package nl.vpro.berlijn.domain.productmetadata;

import org.meeuw.i18n.languages.LanguageCode;


 /**
  * @param type The language code for the sign language
  * @param translation
  * @param language For some reason sign languages seem te be identified with a country?. But sometimes it also contains something like 'en', which is neiter a sign language, nor a country.
  */
public record SignLanguage(
    LanguageCode type,
    boolean translation,
    String language
) {
}
