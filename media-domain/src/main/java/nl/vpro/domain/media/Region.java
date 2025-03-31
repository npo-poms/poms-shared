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


    @XmlDocumentation("Means that this object can only be played in the Netherlands")
    NL("Nederland"),

     /**
     * @since 5.6
     */
    @XmlDocumentation("Nederland plus BES gemeentes")
    NLBES("Nederland en de BES-gemeenten"),

     /**
     * @since 5.6
     */
    @XmlDocumentation("Nederland plus BES gemeentes plus Curaçao, St. Maarten en Aruba")
    NLALL("Nederland, de BES-gemeenten, Curaçao, St. Maarten en Aruba"),


    /**
     * @deprecated Not supported by VMV
     */
    @Deprecated
    @XmlDocumentation("Means that this object can only be played in the Netherlands, Belgium and Luxemburg (This is, as far was we know, not support by the NPO player)")
    BENELUX("Benelux"),

    /**
     * @since 5.6
     */
    @XmlDocumentation("Means that this object can only be played in Europe")
    EUROPE("Europa"),

     /**
     * @since 5.6
     */
    @XmlDocumentation("European Union incl. BES gemeentes, Curaçao, St. Maarten en Aruba")
    EU("De EU inclusief de BES-gemeenten, Curaçao, St. Maarten en Aruba"),

    @XmlDocumentation("No georestriction")
    WR("Overal te zien") {
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
        if (v.toUpperCase().equals("EUROPA")) {
            return EUROPE;
        }
        return valueOf(v);

     }
}
