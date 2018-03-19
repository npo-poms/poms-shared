/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;

import org.apache.commons.lang3.StringUtils;
import org.meeuw.xml.bind.annotation.XmlDocumentation;

@XmlEnum
public enum Region {


    @XmlDocumentation("Means that this object can only be played in the Netherlands")
    NL,

    /**
     * @deprecated Not supported by VMV
     */
    @Deprecated
    @XmlDocumentation("Means that this object can only be played in the Netherlands, Belgium and Luxemburg (This is, as far was we know, not support by the NPO player)")
    BENELUX,

    /**
     * @since 5.6
     */
    @XmlDocumentation("Means that this object can only be played in Europe")
    EUROPE,

    /**
     * @since 5.6
     */
    @XmlDocumentation("Nederland plus BES gemeentes")
    NLBES,

     /**
     * @since 5.6
     */
    @XmlDocumentation("Nederland plus BES gemeentes plus Curacao, St. Maarten en Aruba")
    NLALL,

    /**
     * @since 5.6
     */
    @XmlDocumentation("incl. BES gemeentes, Curacao, St. Maarten en Aruba")
    EU;

    public static Region valueOfOrNull(String v) {
        if (StringUtils.isEmpty(v)) {
            return null;
        }
        return valueOf(v);
    }
}
