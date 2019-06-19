package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum GeoRelationType {

    RECORDED_IN {
        @Override
        public String toString() { return "Opgenomen in"; }
    },
    SUBJECT {
        @Override
        public String toString() {
            return "Onderwerp";
        }
    },
    UNDEFINED {
        @Override
        public String toString() {
            return "Overig";
        }
    };

    public static GeoRelationType fromToString(String string) {
        for (GeoRelationType role : GeoRelationType.values()) {
            if (string.equals(role.toString())) {
                return role;
            }
        }

        throw new IllegalArgumentException("No existing value: " + string);
    }

}
