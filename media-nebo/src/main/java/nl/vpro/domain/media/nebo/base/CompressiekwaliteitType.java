//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.06.07 at 01:34:44 PM CEST
//


package nl.vpro.domain.media.nebo.base;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for compressiekwaliteitType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="compressiekwaliteitType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="bb"/>
 *     &lt;enumeration value="sb"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "compressiekwaliteitType")
@XmlEnum
public enum CompressiekwaliteitType {

    @XmlEnumValue("bb")
    BB("bb"),
    @XmlEnumValue("sb")
    SB("sb");
    private final String value;

    CompressiekwaliteitType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CompressiekwaliteitType fromValue(String v) {
        for (CompressiekwaliteitType c: CompressiekwaliteitType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
