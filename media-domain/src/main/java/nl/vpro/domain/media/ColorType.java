package nl.vpro.domain.media;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.util.XmlValued;
import nl.vpro.jackson2.BackwardsCompatibleJsonEnum;


/**
 * <p>Java class for colorType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="colorType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="COLOR"/>
 *     &lt;enumeration value="BLACK AND WHITE"/>
 *     &lt;enumeration value="BLACK AND WHITE AND COLOR"/>
 *     &lt;enumeration value="COLORIZED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlEnum
@JsonSerialize(using = BackwardsCompatibleJsonEnum.Serializer.class)
@JsonDeserialize(using = ColorType.Deserializer.class)
public enum ColorType implements XmlValued {

    @XmlEnumValue("BLACK AND WHITE")
    BLACK_AND_WHITE("BLACK AND WHITE"),
    @XmlEnumValue("BLACK AND WHITE AND COLOR")
    BLACK_AND_WHITE_AND_COLOR("BLACK AND WHITE AND COLOR"),
    COLOR("COLOR"),
    COLORIZED("COLORIZED");
    private final String value;

    ColorType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ColorType fromValue(String v) {
        for (ColorType c: ColorType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }


    public static class Deserializer extends BackwardsCompatibleJsonEnum.Deserializer<ColorType> {
        public Deserializer() {
            super(ColorType.class);
        }
    }

}
