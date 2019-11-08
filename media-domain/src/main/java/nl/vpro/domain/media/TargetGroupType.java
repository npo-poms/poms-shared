package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Displayable;

@XmlEnum
@XmlType(name = "targetGroupEnum")
public enum TargetGroupType implements Displayable {
    KIDS_6 {
        @Override
        public String toString() {
            return "Kinderen tot 6 jaar (Zappelin)";
        }
    },
    KIDS_12 {
        @Override
        public String toString() {
            return "Kinderen 6-12 (Zapp)";
        }
    },
    YOUNG_ADULTS {
        @Override
        public String toString() {
            return "Jongeren (NPO3)";
        }
    },
    ADULTS {
        @Override
        public String toString() {
            return "Volwassenen";
        }
    },
    ADULTS_WITH_KIDS_6 {
        @Override
        public String toString() {
            return "Volwassenen met kinderen 0-6 jaar";
        }
    },
    ADULTS_WITH_KIDS_12 {
        @Override
        public String toString() {
            return "Volwassenen met kinderen 6-12 jaar";
        }
    },
    EVERYONE {
        @Override
        public String toString() {
            return "Iedereen";
        }
    }
}
