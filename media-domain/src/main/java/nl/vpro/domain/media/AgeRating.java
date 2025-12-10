package nl.vpro.domain.media;

import lombok.Getter;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

import jakarta.xml.bind.annotation.XmlEnumValue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;

import nl.vpro.util.XmlValued;
import nl.vpro.domain.media.bind.AgeRatingToString;
import nl.vpro.i18n.Displayable;

/**
 * The <a href="https://nicam.nl">NICAM</a> age rating.
 *
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@JsonSerialize(using = AgeRatingToString.Serializer.class)
@JsonDeserialize(using = AgeRatingToString.Deserializer.class)
public enum AgeRating implements Displayable, XmlValued {

    ALL("Alle leeftijden", 0) {
        @Override
        public String getDescription() {
            return getDisplayName();
        }
    },

    @XmlEnumValue("6")
    _6(6),
    @XmlEnumValue("9")
    _9(9),
    @XmlEnumValue("12")
    _12(12),

    /**
     * See MSE-4628
     * @since 5.12
     */
    @XmlEnumValue("14")
    _14(14),

    @XmlEnumValue("16")
    _16(16),

    /**
     * See MSE-4628
     * @since 5.12
     */
    @XmlEnumValue("18")
    _18(18),

    @Beta
    NOT_YET_RATED("Nog niet beoordeeld", -1) {
        @Override
        public String getDescription() {
            return getDisplayName();
        }
        @Override
        public boolean display() {
            // Related to MSE-5459 ?
            return "true".equals(System.getProperty("nl.vpro.domain.media.AgeRating.NOT_YET_RATED"));
        }
    };


    private final String displayName;
    @Getter
    private final int intValue;

    AgeRating(String displayName, int intValue) {
        this.displayName = displayName;
        this.intValue = intValue;
    }
    AgeRating(int intValue) {
        this(String.valueOf(intValue), intValue);
    }


    public static AgeRating xmlValueOf(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        if (Character.isDigit(text.charAt(0))) {
            return AgeRating.valueOf('_' + text);
        } else {
            return AgeRating.valueOf(text);
        }
    }

    @Override
    public String getXmlValue() {
        String n = name();
        return n.startsWith("_") ? n.substring(1) : n;
    }

    public static AgeRating valueOf(int kort) {
        for (AgeRating a : values()) {
            if (a.intValue == kort) {
                return a;
            }
        }
        throw new IllegalArgumentException("No agerating " + kort);
    }


    @Override
    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return "Vanaf " + displayName + " jaar";
    }

    @Override
    public Optional<URI> getIcon() {
        return Optional.of(URI.create("/kijkwijzer/icons/agerating/" + getXmlValue().toLowerCase() + ".svg"));
    }
    @Override
    public Optional<String> getIconClass() {
        return Optional.of("kijkwijzer-icon kijkwijzer-icon-agerating-" + getXmlValue().toLowerCase());
    }

    private static final AgeRating[] ACCEPTED = Arrays.stream(AgeRating.values())
        .filter(Displayable::display).toArray(AgeRating[]::new);

    /**
     * Returns all valid AgeRatings that are actually currently accepted.
     * @since 7.2
     */
    public static AgeRating[] acceptedValues() {
        return ACCEPTED;
    }

}
