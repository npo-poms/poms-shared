package nl.vpro.domain;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public interface XmlValued {

    default String getXmlValue() {
        if (this instanceof Enum) {
            Class<?> enumClass = getClass();
            String name = ((Enum) this).name();
            try {
                XmlEnumValue xmlValue = enumClass.getField(name).getAnnotation(XmlEnumValue.class);
                return xmlValue.value();
            } catch (NoSuchFieldException | NullPointerException e) {
                return name;
            }
        }
        throw new UnsupportedOperationException();
    }
}
