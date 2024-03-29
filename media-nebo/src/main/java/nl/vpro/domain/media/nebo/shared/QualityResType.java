//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.06.07 at 01:34:11 PM CEST
//


package nl.vpro.domain.media.nebo.shared;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for qualityResType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="qualityResType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="std"/>
 *     &lt;enumeration value="bb"/>
 *     &lt;enumeration value="sb"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "qualityResType")
@XmlEnum
public enum QualityResType {


    @XmlEnumValue("std")
    STD("std", 1000 * 1000),

    @XmlEnumValue("bb")
    BB("bb", 500 * 1000),

    @XmlEnumValue("sb")
    SB("sb", 200 * 1000);

    private final String value;
    private final int bitRate;

    QualityResType(String v, int bitRate) {
        value = v;
        this.bitRate = bitRate;
    }

    public String value() {
        return value;
    }

    public int getBitrate() {
        return bitRate;
    }


    public static QualityResType fromValue(String v) {
        for (QualityResType c: QualityResType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
