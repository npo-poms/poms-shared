package nl.vpro.domain.media;

import lombok.Getter;

import java.util.Set;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.i18n.Displayable;


@XmlEnum
@XmlType(name = "targetGroupEnum")
public enum TargetGroupType implements Displayable {
    KIDS_6("Kinderen tot 6 jaar", Set.of(AgeRating.ALL)),


    /**
     * @since 8.10
     */
    KIDS_9("Kinderen 6 tot 9 jaar", Set.of(AgeRating.ALL, AgeRating._6)),

    KIDS_12("Kinderen 9 tot 12 jaar", Set.of(AgeRating.ALL, AgeRating._6, AgeRating._9)),

    /**
     * @since 8.10
     */
    KIDS_14("Kinderen 12 tot 14 jaar", Set.of(AgeRating.ALL, AgeRating._6, AgeRating._9, AgeRating._12)),

    /**
     * @since 8.10
     */
    KIDS_16("Kinderen 12 tot 14 jaar", Set.of(AgeRating.ALL, AgeRating._6, AgeRating._9, AgeRating._12, AgeRating._14)),

    YOUNG_ADULTS("Jongeren (NPO3)", Set.of(AgeRating.ALL, AgeRating._6, AgeRating._9, AgeRating._12, AgeRating._14, AgeRating._16)),
    ADULTS("Volwassenen", Set.of(AgeRating.ALL, AgeRating._6, AgeRating._9, AgeRating._12, AgeRating._14, AgeRating._16)),
    ADULTS_WITH_KIDS_6( "Volwassenen met kinderen 0-6 jaar", Set.of(AgeRating.ALL, AgeRating._6, AgeRating._9, AgeRating._12, AgeRating._14, AgeRating._16)),
    ADULTS_WITH_KIDS_12("Volwassenen met kinderen 6-12 jaar", Set.of(AgeRating.ALL, AgeRating._6, AgeRating._9, AgeRating._12, AgeRating._14, AgeRating._16)),
    EVERYONE("Iedereen", Set.of(AgeRating.ALL, AgeRating._6, AgeRating._9, AgeRating._12, AgeRating._14, AgeRating._16))
    ;

    @Getter
    private final String displayName;

    /**
     * There is a link between target groups and age-rating. If the mediaobject has an agerating, then the only targetgroups allowed are the ones wich have that agerating
     * @sicne 8.10
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
