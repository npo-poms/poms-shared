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
    EUROPE
    ;

    public static Region valueOfOrNull(String v) {
        if (StringUtils.isEmpty(v)) {
            return null;
        }
        return valueOf(v);
    }
}
