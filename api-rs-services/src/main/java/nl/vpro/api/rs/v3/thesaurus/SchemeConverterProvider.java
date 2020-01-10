package nl.vpro.api.rs.v3.thesaurus;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ext.*;

import nl.vpro.domain.gtaa.Scheme;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
@Provider
public class SchemeConverterProvider implements ParamConverterProvider {
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (Scheme.class.isAssignableFrom(rawType)) {
             return (ParamConverter<T>) SchemeConverter.INSTANCE;
        }
        return null;
    }
}
