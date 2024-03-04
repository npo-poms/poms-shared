package nl.vpro.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An enum can be made to extend this, which indicates that an extra method will be present {@link #getJsonPropertyValue()}} which
 * will be the {@link JsonProperty} of the enum value.
 * <p>
 * Normally this would be {@link Enum#name()}}, but sometimes this is overriden, via the said annotation, and you need programmatic access to it.
 *
 * @author Michiel Meeuwissen
 * @since 8.0
 * @see XmlValued
 */
public interface JsonProperties {

    default String getJsonPropertyValue() {
        if (this instanceof Enum) {
            Class<?> enumClass = getClass();
            String name = ((Enum<?>) this).name();
            try {
                JsonProperty jsonValue = enumClass.getField(name).getAnnotation(JsonProperty.class);
                return jsonValue.value();
            } catch (NoSuchFieldException | NullPointerException e) {
                return name;
            }
        }
        throw new UnsupportedOperationException("Only supported for enums");
    }

    /**
     *
     */
    static <E extends Enum<E> & JsonProperties> E valueOfJson(E[] values, String value) {
        for (E v : values) {
            if (v.getJsonPropertyValue().equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("No constant with json value " + value);
    }

}
