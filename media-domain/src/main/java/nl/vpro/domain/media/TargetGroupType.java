package nl.vpro.domain.media;

import lombok.Getter;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Displayable;

@XmlEnum
@XmlType(name = "targetGroupEnum")
public enum TargetGroupType implements Displayable {
    KIDS_6("Kinderen tot 6 jaar (Zappelin)"),
    KIDS_12("Kinderen 6-12 (Zapp)"),
    YOUNG_ADULTS("Jongeren (NPO3)"),
    ADULTS("Volwassenen"),
    ADULTS_WITH_KIDS_6( "Volwassenen met kinderen 0-6 jaar"),
    ADULTS_WITH_KIDS_12("Volwassenen met kinderen 6-12 jaar"),
    EVERYONE("Iedereen")
    ;

    @Getter
    private final String displayName;

    TargetGroupType(String displayName) {
        this.displayName = displayName;
    }
   @Override
   public String toString() {
        return getDisplayName();
    }
}
