package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;

import nl.vpro.domain.Displayable;


/**
 * @author Giorgio Vinci
 * @since 5.11
 */
@XmlEnum
public enum GeoRoleType implements Displayable {

    RECORDED_IN {
        @Override
        public String getDisplayName() {
            return "Opgenomen in";
        }
    },
    SUBJECT {
        @Override
        public String getDisplayName() {
            return "Onderwerp";
        }
    },
    UNDEFINED {
        @Override
        public String getDisplayName() {
            return "Overig";
        }
    };


    @Override
    public String toString() {
        return getDisplayName();
    }

    public static GeoRoleType fromToString(String string) {
        for (GeoRoleType role : GeoRoleType.values()) {
            if (string.equals(role.getDisplayName())) {
                return role;
            }
        }

        throw new IllegalArgumentException("No existing value: " + string);
    }

}
