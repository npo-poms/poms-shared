package nl.vpro.domain.media.bind;


import java.util.Locale;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.meeuw.i18n.languages.ISO_639;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.UsedLanguage;
import nl.vpro.domain.media.update.MediaUpdate;
import nl.vpro.i18n.Locales;
import nl.vpro.xml.bind.LocaleAdapter;

/**
 * Representation of a {@link UsedLanguage} for normal mediaobject. This includes the name of the language (in dutch) as {@link XmlValue}.
 * @author Michiel Meeuwissen
 * @since 8.2
 */
public class UsedLanguageAdapter extends XmlAdapter<UsedLanguageAdapter.Wrapper, UsedLanguage> {


    @Override
    public UsedLanguage unmarshal(Wrapper v) {
        return v.getUsedLanguage();
    }

    @Override
    public Wrapper marshal(UsedLanguage v) {
        return new Wrapper(v);
    }


    /**
     * Used in {@link MediaUpdate}, which has a slightly different representation of {@link UsedLanguage}  * @author Michiel Meeuwissen
     * @since 8.2
     */
    @XmlAccessorType(XmlAccessType.NONE)
    @XmlType(name = "languageType",
        propOrder = {""},
        namespace = Xmlns.MEDIA_NAMESPACE
    )
    @JsonPropertyOrder(
        {"code", "usage", "value"}
    )
    public static class Wrapper extends UsedLanguage.AbstractWrapper {

        public Wrapper() {
        }

        public Wrapper(UsedLanguage language) {
            super(language);
        }

        @XmlValue
        @JsonProperty
        public String getValue() {
            return
                ISO_639.get(getCode().getLanguage())
                    .map(c -> c.getDisplayName(Locales.DUTCH))
                    .orElseGet(() -> getCode().getDisplayLanguage(Locales.DUTCH));
        }

        public void setValue(String ignored) {
            // jaxb
        }

        @Override
        @XmlAttribute
        @XmlJavaTypeAdapter(LocaleAdapter.class)
        @JsonProperty("code")
        public Locale getCode() {
            return super.getCode();
        }



    }
}
