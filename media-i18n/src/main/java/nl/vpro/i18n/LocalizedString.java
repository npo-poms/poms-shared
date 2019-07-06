package nl.vpro.i18n;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.neovisionaries.i18n.LanguageCode;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
@XmlAccessorType(XmlAccessType.NONE)
@Slf4j
public class LocalizedString { //implements javax.xml.registry.infomodel.LocalizedString {

    private static final Map<String, String> MAP_TO_ISO = new HashMap<>();

    static {
        // These codes are mainly used in tva-xml's.
        //  they just make up stuff.
        //see http://www-01.sil.org/iso639-3/documentation.asp?id=zxx             -
        MAP_TO_ISO.put("xx", "zxx");
        MAP_TO_ISO.put("cz", "cs");

    }


    public static LocalizedString of(String value, Locale locale) {
        if (value == null) {
            return null;
        } else {
            LocalizedString string = new LocalizedString();
            string.value = value;
            string.locale = locale;
            return string;
        }
    }



    @XmlAttribute(name = "lang", namespace = XMLConstants.XML_NS_URI)
    @JsonProperty("lang")
    @XmlJavaTypeAdapter(value = XmlLangAdapter.class)
    private Locale locale;

    @XmlValue
    private String value;

    private String charset;

    //@Override
    public String getCharsetName()  {
        return charset;

    }

    //@Override
    public Locale getLocale() {
        return locale;
    }

    //@Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    //@Override
    public String getValue() {
        return value;
    }

    //@Override
    public void setCharsetName(String charsetName) {
        this.charset = charsetName;

    }

    //@Override
    public void setValue(String value) {
        this.value = value;
    }

    public static String get(Locale locale, Iterable<LocalizedString> strings) {
        LocalizedString candidate = null;
        if (strings != null) {
            int score = -1;
            for (LocalizedString string : strings) {
                int s = string.getScore(locale);
                if (string.getScore(locale) > score) {
                    candidate = string;
                    score = s;
                }
            }
        }
        return candidate == null ? null : candidate.getValue();

    }

    @Override
    public String toString() {
        return value;
    }

    private int getScore(Locale locale) {
        int score = 0;
        if (this.locale == null || locale == null) {
            return score;
        }
        if (Objects.equals(locale.getLanguage(), this.locale.getLanguage())) {
            score++;
        } else {
            return score;
        }
        if (Objects.equals(locale.getCountry(), this.locale.getCountry())) {
            score++;
        } else {
            return score;
        }
        if (Objects.equals(locale.getVariant(), this.locale.getVariant())) {
            score++;
        }
        return score;
    }

    public static class XmlLangAdapter extends XmlAdapter<String, Locale> {

        @Override
        public Locale unmarshal(String v) {
            return adapt(v);

        }

        @Override
        public String marshal(Locale v) {
            return v == null ? null : v.toString();

        }
    }


    public static Locale adapt(String v) {
        if (v == null) {
            return null;
        }
        String[] split = v.split("[_-]", 3);
        String replace = MAP_TO_ISO.get(split[0].toLowerCase());
        if (replace != null) {
            log.warn("Found unknown iso language code {}, replaced with {}", split[0], replace);
            split[0] = replace;
        }
        LanguageCode languageCode = LanguageCode.getByCode(split[0], false);
        String language = languageCode == null ? split[0] : languageCode.name().toLowerCase();

        switch (split.length) {
            case 1:
                return new Locale(language);
            case 2:
                return new Locale(language, split[1].toUpperCase());
            default:
                return new Locale(language, split[1].toUpperCase(), split[2]);
        }
    }
}
