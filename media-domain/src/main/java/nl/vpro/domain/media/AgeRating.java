package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnumValue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Displayable;
import nl.vpro.domain.XmlValued;
import nl.vpro.domain.media.bind.AgeRatingToString;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@JsonSerialize(using = AgeRatingToString.Serializer.class)
@JsonDeserialize(using = AgeRatingToString.Deserializer.class)
public enum AgeRating implements Displayable, XmlValued {

    @XmlEnumValue("6")
    _6("6"),
    @XmlEnumValue("9")
    _9("9"),
    @XmlEnumValue("12")
    _12("12"),
    @XmlEnumValue("16")
    _16("16"),

    ALL("Alle leeftijden") {
        @Override
        public String getDescription() {
            return getDisplayName();
        }
    };

    private String displayName;

    AgeRating(String displayName) {
        this.displayName = displayName;
    }

    public static AgeRating xmlValueOf(String text) {
        if (text == null) {
            return null;
        }
        switch (text) {
            case "": return null;
            case "ALL":
                return AgeRating.ALL;
            default:
                return AgeRating.valueOf('_' + text);
        }
    }

    @Override
    public String getXmlValue() {
        String n = name();
        return n.startsWith("_") ? n.substring(1) : n;
    }

    public static AgeRating valueOf(Short kort) {
        if (kort == null) {
            return ALL;
        }
        return AgeRating.valueOf("_" + kort);
    }


    @Override
    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return "Vanaf " + displayName + " jaar";
    }
}
