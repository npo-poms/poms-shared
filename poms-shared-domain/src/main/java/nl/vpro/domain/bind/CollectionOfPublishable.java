package nl.vpro.domain.bind;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.domain.Embargo;


/**
 * This serializer for collections will leave out the collection all entries that are not {@link Embargo#isPublishable()}.
 *
 * @since 5.31
 * @see PublicationFilter
 */
@Slf4j
public class CollectionOfPublishable extends AbstractJsonIterable.Serializer<Embargo> {
    {
        considerJsonInclude = true;
    }
    @Override
    protected void serializeValue(Embargo value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        if (PublicationFilter.filter(value, serializerProvider)) {
            log.debug("{} is not publishable", value);
        } else {
            jgen.getCodec().writeValue(jgen, value);
        }
    }
}
