package nl.vpro.domain.media.bind;

import java.util.Locale;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
public class LocaleWrapper {


    private static final Locale NL = new Locale("nl");


    private Locale locale;

    public LocaleWrapper() {
    }

    public LocaleWrapper(Locale code) {
        this.locale = code;
    }


    @XmlValue
    @JsonProperty("value")
    public String getName() {
        return locale == null ? "" : locale.getDisplayLanguage(NL);
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
    public Locale getLocale() {
        return locale;
    }
}
