package nl.vpro.domain.bind;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import nl.vpro.domain.Embargo;
import nl.vpro.jackson2.Views;

/**
 * This jackson filter is enabled when using poms domain classes.
 * <p>
 * The idea is that it makes it possible ot marshall objects that are under embargo, when using the {@link Views.Publisher} view.
 * <p>
 * In poms this is arranged via hibernate filters, but this could be an alternative, since we publish JSON basically.
 * <p>
 * This is now used to generate 'published' json for tests cases.
 *
 * @see CollectionOfPublishable
 * @since 5.31
 */
@Slf4j
public class PublicationFilter extends SimpleBeanPropertyFilter {

    /**
     * For now on default this filter does nothing.
     * <p>
     * You may override that by setting in this thread local to true
     *
     */
    public static final ThreadLocal<Boolean> ENABLED = ThreadLocal.withInitial(() -> false);

    protected static boolean filter(Object pojo, SerializerProvider prov) {
        Class<?> activeView = prov.getActiveView();
        if (ENABLED.get() && Views.Publisher.class.isAssignableFrom(activeView)) {
            if (pojo instanceof Embargo embargo) {
                return ! embargo.isPublishable();
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
        if (filter(elementValue, prov)) {
            log.debug("Skipping write of element {}", elementValue);
            return;
        }
        super.serializeAsElement(elementValue, gen, prov, writer);
    }

    @Override
    public String toString() {
        return PublicationFilter.class.getSimpleName() + " enabled: " + ENABLED.get();
    }

}
