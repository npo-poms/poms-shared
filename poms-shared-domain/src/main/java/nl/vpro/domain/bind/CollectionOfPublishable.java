package nl.vpro.domain.bind;


import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.domain.Embargo;


/**
 * This serializer for collections will leave out the collection all values that are under embargo.
 *
 * @since 5.31
 * @see PublicationFilter
 */
public class CollectionOfPublishable  extends AbstractList.Serializer<Embargo> {
    {
        considerJsonInclude = true;
    }
    @Override
    protected void serializeValue(Embargo value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        if (PublicationFilter.filter(value, serializerProvider)) {

        } else {
            jgen.getCodec().writeValue(jgen, value);
        }
    }
}
