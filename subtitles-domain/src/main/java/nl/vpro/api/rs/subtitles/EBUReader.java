package nl.vpro.api.rs.subtitles;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.Cue;
import nl.vpro.domain.subtitles.EBU;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Provider
@Consumes(Constants.EBU)
public class EBUReader implements MessageBodyReader<Iterator<Cue>> {


    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return mediaType.isCompatible(Constants.EBU_TYPE) && Iterator.class.isAssignableFrom(type);
    }

    @Override
    public Iterator<Cue> readFrom(Class<Iterator<Cue>> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        return EBU.parse(null, entityStream).iterator();
    }
}
