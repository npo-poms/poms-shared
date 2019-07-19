package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;

import org.apache.commons.lang3.StringUtils;

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
