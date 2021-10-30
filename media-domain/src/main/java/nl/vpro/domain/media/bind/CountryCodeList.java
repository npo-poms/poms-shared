package nl.vpro.domain.media.bind;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.meeuw.i18n.regions.Region;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;

import nl.vpro.domain.bind.AbstractList;
import nl.vpro.jackson2.Jackson2Mapper;

/**
 * Might not be needed.
 * @author Michiel Meeuwissen
 * @since 5.12
 */
@Slf4j
public class CountryCodeList {

    private CountryCodeList() {
    }

    public static class Serializer extends AbstractList.Serializer<Region> {

        @Override
        protected void serializeValue(Region value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
            if (value == null) {
                log.warn("country code is null");
                jgen.writeNull();
            } else {
                jgen.writeObject(new CountryWrapper(value));
            }
        }
    }

    public static class Deserializer extends AbstractList.Deserializer<Region> {

        @Override
        protected Region deserialize(JsonNode node, DeserializationContext ctxt) throws IOException {
            if (node == null) {
                return null;
            }
            try {
                CountryWrapper wrapper = Jackson2Mapper.getInstance().readerFor(CountryWrapper.class).readValue(node);
                return wrapper == null ? null : wrapper.getCode();

            } catch (Exception e) {
                log.warn(e.getMessage());
                return null;

            }
        }
    }
}
