package nl.vpro.api.rs.v3.thesaurus;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.ext.*;

/**
 * Unused I tried to support also urls on schemes argument. It's hard...
 * I gave it up for now (hence it is not a Provider)
 * @author Michiel Meeuwissen
 * @since 5.12
 */
//@Provider
@Slf4j
public class SchemeConverterProvider implements ParamConverterProvider {
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        for (Annotation a : annotations) {
            if (a instanceof QueryParam) {
                // I would have prefered to inspect the presence of @ValidGtaaScheme, but this seems to be pretty much impossible
                if (((QueryParam) a).value().equals("schemes")) {
                    return (ParamConverter<T>) SchemeConverter.INSTANCE;
                }
            }
        }
        return null;
    }
}
