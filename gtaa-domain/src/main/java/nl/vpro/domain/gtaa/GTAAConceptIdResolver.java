package nl.vpro.domain.gtaa;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class GTAAConceptIdResolver extends TypeIdResolverBase {

    private static boolean inited = false;
    static {
        init();
    }

    @SuppressWarnings("unchecked")
    static void init() {
        if (! inited) {
            for (JsonSubTypes.Type type : GTAAConcept.class.getAnnotation(JsonSubTypes.class).value()) {
                Scheme.init((Class<? extends GTAAConcept>) type.value());
            }
            inited = true;
        }
    }

    @Override
    public String idFromValue(Object value) {
        return idFromValueAndType(value, value.getClass());

    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return suggestedType.getAnnotation(GTAAScheme.class).value().getJsonObjectType();

    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
    @Override
    public JavaType typeFromId(DatabindContext context, String objectType) {
        return TypeFactory.defaultInstance()
            .constructSimpleType(Scheme.ofJsonObjectType(objectType).getImplementation(), new JavaType[0]);
    }
}
