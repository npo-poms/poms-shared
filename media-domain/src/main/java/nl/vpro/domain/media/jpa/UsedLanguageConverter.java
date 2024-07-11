package nl.vpro.domain.media.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import nl.vpro.domain.media.UsedLanguage;
import nl.vpro.i18n.Locales;

@Converter
public class UsedLanguageConverter implements AttributeConverter<UsedLanguage, String> {
    @Override
    public String convertToDatabaseColumn(UsedLanguage attribute) {
        return attribute.locale().toLanguageTag() + (attribute.usage() == UsedLanguage.Usage.AUDIODESCRIPTION ? "" : ":" + attribute.usage());
    }

    @Override
    public UsedLanguage convertToEntityAttribute(String dbData) {
        String[] split = dbData.split(":", 2);
        if (split.length == 1) {
            return UsedLanguage.of(Locales.ofString(dbData));
        } else {
            return new UsedLanguage(Locales.ofString(split[0]), UsedLanguage.Usage.valueOf(split[1]));
        }
    }
}
