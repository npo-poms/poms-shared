package nl.vpro.domain.media;

import jakarta.xml.bind.annotation.XmlEnum;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.i18n.Displayable;


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
    PRODUCED_IN {
        @Override
        public String getDisplayName() {
            return "Geproduceerd in";
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

    public static GeoRoleType fromDisplayName(String string) {
        if (StringUtils.isEmpty(string)) {
            return UNDEFINED;
        }
        for (GeoRoleType role : GeoRoleType.values()) {
            if (string.equals(role.getDisplayName())) {
                return role;
            }
        }

        throw new IllegalArgumentException("No existing value: " + string);
    }

}
