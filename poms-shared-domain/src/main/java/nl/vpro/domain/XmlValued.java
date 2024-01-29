package nl.vpro.domain;

import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * An enum can be made to extend this, which indicates that an extra method will be present {@link #getXmlValue()} which
 * will be the {@link XmlEnumValue} of the enum value.
 * <p>
 * Normally this would be {@link Enum#name()}}, but sometimes this is overriden, via the said annotation, and you need programmatic access to it.
 *
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public interface XmlValued {

    default String getXmlValue() {
        if (this instanceof Enum) {
            Class<?> enumClass = getClass();
            String name = ((Enum<?>) this).name();
            try {
                XmlEnumValue xmlValue = enumClass.getField(name).getAnnotation(XmlEnumValue.class);
                return xmlValue.value();
            } catch (NoSuchFieldException | NullPointerException e) {
                return name;
            }
        }
        throw new UnsupportedOperationException("Only supported for enums");
    }

    /**
     *
     * @since 5.20.2
     */
    static <E extends Enum<E> & XmlValued> E valueOfXml(E[] values, String value) {
        for (E v : values) {
            if (v.getXmlValue().equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("No constant with xml value " + value);
    }

}
