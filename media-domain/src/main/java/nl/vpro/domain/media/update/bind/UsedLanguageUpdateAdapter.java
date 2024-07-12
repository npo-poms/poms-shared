package nl.vpro.domain.media.update.bind;


import java.util.Locale;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.UsedLanguage;
import nl.vpro.domain.media.update.MediaUpdate;
import nl.vpro.xml.bind.LocaleAdapter;

/**
 * Used in {@link nl.vpro.domain.media.update.MediaUpdate}, which has a slightly different representation of {@link UsedLanguage}
 * @author Michiel Meeuwissen
 * @since 8.2
 */
public class UsedLanguageUpdateAdapter extends XmlAdapter<UsedLanguageUpdateAdapter.Wrapper, UsedLanguage> {


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
        namespace = Xmlns.UPDATE_NAMESPACE

    )
    @JsonPropertyOrder(
        {"usage", "value"}
    )
    public static class Wrapper {


        private Locale code;

        private UsedLanguage.Usage usage;

        public Wrapper() {
        }

        public Wrapper(String code){
            this.code = new Locale(code);
            this.usage = null;
        }

        public Wrapper(UsedLanguage language) {
            this.code = language == null ? null : language.code();
            this.usage = language == null || language.usage() == null || language.usage() == UsedLanguage.Usage.AUDIODESCRIPTION ?  null : language.usage();
        }


        @XmlValue
        @XmlJavaTypeAdapter(LocaleAdapter.class)
        @JsonProperty("value")
        public Locale getCode() {
            return code;
        }
        @JsonProperty("value")
        public void setCode(Locale code) {
            this.code = code;

        }

        public UsedLanguage getUsedLanguage() {
            return code == null ? null : (usage == null ? UsedLanguage.of(code) : new UsedLanguage(code, usage));
        }


        @XmlAttribute
        public UsedLanguage.Usage getUsage() {
            return usage == null || usage == UsedLanguage.Usage.AUDIODESCRIPTION ? null : usage;
        }

        public void setUsage(UsedLanguage.Usage usage) {
            this.usage = usage;
        }

    }
}
