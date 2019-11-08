package nl.vpro.domain.media;

import java.util.Optional;

import javax.xml.bind.annotation.XmlEnumValue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;

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

    /**
     * See MSE-4628
     * @since 5.12
     */
    @XmlEnumValue("14")
    @Beta
    _14("14"),

    @XmlEnumValue("16")
    _16("16"),

    /**
     * See MSE-4628
     * @since 5.12
     */
    @XmlEnumValue("18")
    @Beta
    _18("18"),

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

    @Override
    public Optional<String> getIcon() {
        return Optional.of("/kijkwijzer/icons/agerating/" + getXmlValue().toLowerCase() + ".svg");
    }
    @Override
    public Optional<String> getIconClass() {
        return Optional.of("kijkwijzer-icon kijkwijzer-icon-agerating-" + getXmlValue().toLowerCase());
    }
}
