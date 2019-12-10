/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.domain.page.Crid;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public class CridToString {

    public static class Serializer extends JsonSerializer<Crid> {

        @Override
        public void serialize(Crid crid, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(crid.getValue());
        }
    }

    public static class Deserializer extends JsonDeserializer<Crid> {

        @Override
        public Crid deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return new Crid(jp.getValueAsString());
        }
    }
}
