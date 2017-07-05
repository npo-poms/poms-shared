/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;

import org.apache.commons.lang3.StringUtils;

@XmlEnum
public enum Region {


    NL,
    /**
     * @deprecated Not supported by VMV
     */
    @Deprecated
    BENELUX;

    public static Region valueOfOrNull(String v) {
        if (StringUtils.isEmpty(v)) {
            return null;
        }
        return valueOf(v);
    }
}
