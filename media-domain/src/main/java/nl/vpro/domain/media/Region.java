/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.Getter;

import javax.xml.bind.annotation.XmlEnum;

import org.apache.commons.lang3.StringUtils;
import org.meeuw.xml.bind.annotation.XmlDocumentation;

import nl.vpro.i18n.Displayable;


/**
 * The region as used in {@link GeoRestriction}. The order wants to be from more to less restrictive.
 */
@XmlEnum
//@XmlJavaTypeAdapter(value = RegionAdapter.class)
public enum Region implements Displayable {


    /**
     * Netherlands
     * /
    @XmlDocumentation("Means that this object can only be played in the Netherlands")
    NL("Nederland"),

    /**
     * Netherlands and communities in the Caribbean
     * @since 5.6
     */
    @XmlDocumentation("Nederland plus BES gemeentes")
    NLBES("Nederland en de BES-gemeenten"),

    /**
     * Netherlands and communities in the Caribbean and Curaçao, St. Maarten and Aruba
     * @since 5.6
     */
    @XmlDocumentation("Nederland plus BES gemeentes plus Curaçao, St. Maarten en Aruba")
    NLALL("Nederland, de BES-gemeenten, Curaçao, St. Maarten en Aruba"),


    /**
     * Belgium, Netherlands and Luxemburg
     * @deprecated Not supported by VMV
     */
    @Deprecated
    @XmlDocumentation("Means that this object can only be played in the Netherlands, Belgium and Luxemburg (This is, as far was we know, not supported by the NPO player)")
    BENELUX("Benelux"),

    /**
     * Europe
     * @since 5.6
     */
    @XmlDocumentation("Means that this object can only be played in Europe")
    EUROPE("Europa"),

    /**
      * European Union incl. BES gemeentes, Curaçao, St. Maarten en Aruba
     * @since 5.6
     */
    @XmlDocumentation("European Union incl. BES gemeentes, Curaçao, St. Maarten en Aruba")
    EU("De EU inclusief de BES-gemeenten, Curaçao, St. Maarten en Aruba"),


    /**
     * It can be handy to have a region value that effectively doesn't restrict anything. This way there is no need for n
     * null checks and things like that.
     * @since 7.6
     */
    @XmlDocumentation("No georestriction")
    UNIVERSE("Overal te zien") {
        @Override
        public boolean display() {
            return false;
        }
    }


    ;



    @Getter
    private final String displayName;

    Region(String displayName) {
        this.displayName = displayName;
    }

    public static Region valueOfOrNull(String v) {
        try {
            return valueOfOrNullOrIllegalArgument(v);
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }

    public static Region valueOfOrNullOrIllegalArgument(String v) {
        if (StringUtils.isEmpty(v)) {
            return null;
        }
        if (v.equalsIgnoreCase("EUROPA")) {
            return EUROPE;
        }
        return valueOf(v);

     }

}
