package nl.vpro.domain.media;

import lombok.Getter;

import jakarta.xml.bind.annotation.*;

import nl.vpro.i18n.Displayable;


@XmlEnum
@XmlType(name = "targetGroupEnum")
public enum TargetGroupType implements Displayable {
    KIDS_6("Kinderen tot 6 jaar (Zappelin)"),
    KIDS_12("Kinderen 6-12 (Zapp)"),

    /**
     * @since 8.10
     */
    @XmlEnumValue("3-6")
    _3_6("Kindprofiel 3-6"),

    /**
     * @since 8.10
     */
    @XmlEnumValue("-9")
    _9("Kindprofiel tot 9"),

    /**
     * @since 8.10
     */
    @XmlEnumValue("-12")
    _12("Kindprofiel tot 12"),

    /**
     * @since 8.10
     */
    @XmlEnumValue("-14")
    _14("Kindprofiel tot 14"),

    /**
     * @since 8.10
     */
    @XmlEnumValue("-16")
    _16("Kindprofiel tot 16"),

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
