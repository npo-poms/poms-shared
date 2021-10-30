package nl.vpro.domain.bind;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import nl.vpro.domain.Embargo;
import nl.vpro.jackson2.Views;

/**
 * This jackson filter is enabled when using poms domain classes.
 *
 * The idea is that it will not marshall objects that are under embargo.
 *
 * In poms this is arranged via hibernate filters, but this could be an alternative, since we publish JSON basically.
 *
 * @see CollectionOfPublishable
 * @since 5.31
 */
@Slf4j
public class PublicationFilter extends SimpleBeanPropertyFilter {

    public static ThreadLocal<Boolean> enabled = ThreadLocal.withInitial(() -> false);

    protected static boolean filter(Object pojo, SerializerProvider prov) {
        Class<?> activeView = prov.getActiveView();
        if (enabled.get() && Views.Publisher.class.isAssignableFrom(activeView)) {
            if (pojo instanceof Embargo) {
                if (((Embargo) pojo).isUnderEmbargo()) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public void serializeAsField(Object pojo, JsonGenerator gen, SerializerProvider prov, PropertyWriter writer) throws Exception {
        if (filter(pojo, prov)) {
            log.debug("Skipping write of {}", pojo);
            return;
        }
        super.serializeAsField(pojo, gen, prov, writer);
    }

    @Override
    public void serializeAsElement(Object elementValue, JsonGenerator gen, SerializerProvider prov, PropertyWriter writer) throws Exception {
        if (elementValue instanceof Collection) {
            log.debug("Found {}", elementValue);
        }
        if (filter(elementValue, prov)) {
            log.debug("Skipping write of element {}", elementValue);
            return;
        }
        super.serializeAsElement(elementValue, gen, prov, writer);
    }

    @Override
    public String toString() {
        return PublicationFilter.class.getSimpleName() + " enabled: " + enabled.get();
    }



}
