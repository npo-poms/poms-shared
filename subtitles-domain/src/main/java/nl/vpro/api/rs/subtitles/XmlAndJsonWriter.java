package nl.vpro.api.rs.subtitles;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXB;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vpro.domain.subtitles.StandaloneCue;
import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.jackson2.Jackson2Mapper;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Provider
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class XmlAndJsonWriter implements MessageBodyWriter<Iterator<StandaloneCue>> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return (MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType) || MediaType.APPLICATION_XML_TYPE.isCompatible(mediaType))
            && Iterator.class.isAssignableFrom(type);

    }

    @Override
    public long getSize(Iterator<StandaloneCue> cueIterator, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;

    }

    private static final ObjectMapper MAPPER = Jackson2Mapper.getInstance();


    @Override
    public void writeTo(Iterator<StandaloneCue> cueIterator, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        Subtitles subtitles = Subtitles.from(cueIterator);
        if (MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType)) {
            MAPPER.writeValue(entityStream, subtitles);
        } else if (MediaType.APPLICATION_XML_TYPE.isCompatible(mediaType)) {
            JAXB.marshal(subtitles, entityStream);
        }

    }


}
