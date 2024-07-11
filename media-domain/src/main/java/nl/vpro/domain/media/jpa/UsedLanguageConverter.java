package nl.vpro.domain.media.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import nl.vpro.domain.media.UsedLanguage;
import nl.vpro.i18n.Locales;

@Converter
public class UsedLanguageConverter implements AttributeConverter<UsedLanguage, String> {
    @Override
    public String convertToDatabaseColumn(UsedLanguage attribute) {
        return attribute.locale().toLanguageTag();
    }

    @Override
    public UsedLanguage convertToEntityAttribute(String dbData) {
        return UsedLanguage.of(Locales.ofString(dbData));
    }
}
