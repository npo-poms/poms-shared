package nl.vpro.domain.media;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.i18n.Displayable;


/**
 * Used as a value in {@link TargetGroup#value}.
 */
@XmlEnum
@XmlType(name = "targetGroupEnum")
public enum TargetGroupType implements Displayable {

    KIDS_6("Kinderen tot 6 jaar", EnumSet.of(AgeRating.ALL)),

    /**
     * @since 8.10
     */
    KIDS_9("Kinderen 6 tot 9 jaar", EnumSet.range(AgeRating.ALL, AgeRating._6)),

    KIDS_12("Kinderen 9 tot 12 jaar", EnumSet.range(AgeRating.ALL,  AgeRating._9)),

    /**
     * @since 8.10
     */
    KIDS_14("Kinderen 12 tot 14 jaar", EnumSet.range(AgeRating.ALL,  AgeRating._12)),

    /**
     * @since 8.10
     */
    KIDS_16("Kinderen 14 tot 16 jaar", EnumSet.range(AgeRating.ALL,  AgeRating._14)),

    YOUNG_ADULTS("Jongeren (NPO3)", EnumSet.range(AgeRating.ALL,  AgeRating._16)),
    ADULTS("Volwassenen", EnumSet.range(AgeRating.ALL, AgeRating._16)),
    ADULTS_WITH_KIDS_6( "Volwassenen met kinderen 0-6 jaar", EnumSet.range(AgeRating.ALL, AgeRating._16)),
    ADULTS_WITH_KIDS_12("Volwassenen met kinderen 6-12 jaar", EnumSet.range(AgeRating.ALL,  AgeRating._16)),
    EVERYONE("Iedereen", EnumSet.range(AgeRating.ALL,  AgeRating._16))
    ;

    @Getter
    private final String displayName;

    /**
     * There is a link between target groups and age-rating. If the {@link MediaObject media object} has an {@link MediaObject#getAgeRating() age rating}, then the only target groups that are allowed are the ones which contain that age rating.
     * @since 8.10
     */
    @Getter
    private final Set<AgeRating> ageRatings;

    TargetGroupType(String displayName, Set<AgeRating> ageRatings) {
        this.displayName = displayName;
        this.ageRatings = ageRatings;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
