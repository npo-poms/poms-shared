package nl.vpro.domain.media.bind;

import lombok.Setter;

import java.util.Locale;

import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.media.UsedLanguage;
import nl.vpro.i18n.Locales;
import nl.vpro.i18n.LocalizedString;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "languageType",
    propOrder = {"name"})
@JsonPropertyOrder(
    {"code", "value"}
)
public class UsedLanguageWrapper {


    private Locale locale;
    @Setter
    private UsedLanguage.Usage usage;

    public UsedLanguageWrapper() {
    }

    public UsedLanguageWrapper(UsedLanguage language) {
        this.locale = language == null ? null : language.locale();
        this.usage = language == null || language.usage() == null || language.usage() == UsedLanguage.Usage.AUDIODESCRIPTION ?  null : language.usage();
    }


    @XmlValue
    @JsonProperty("value")
    public String getName() {
        return locale == null ? "" : locale.getDisplayLanguage(Locales.NETHERLANDISH);
    }
    @JsonProperty("value")
    public void setName(String name) {
        // I hate jaxb

    }

    @XmlAttribute
    public String getCode() {
        return locale == null ? null : locale.toLanguageTag();
    }

    public void  setCode(String code) {
        locale = LocalizedString.adapt(code);
    }

    public UsedLanguage getUsedLanguage() {
        return locale == null ? null : (usage == null ? UsedLanguage.of(locale) : new UsedLanguage(locale, usage));
    }


    @XmlAttribute
    public UsedLanguage.Usage getUsage() {
        return usage == null || usage == UsedLanguage.Usage.AUDIODESCRIPTION ? null : usage;
    }

}
