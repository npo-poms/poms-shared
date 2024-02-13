package nl.vpro.api.rs.subtitles;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;

import nl.vpro.domain.subtitles.Cue;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
abstract class AbstractIteratorReader implements MessageBodyReader<Iterator<Cue>> {


    private final MediaType mediaType;

    public AbstractIteratorReader(MediaType mediaType) {
        this.mediaType = mediaType;
    }



    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return mediaType.isCompatible(this.mediaType) && Iterator.class.isAssignableFrom(type);

    }

    @Override
    public Iterator<Cue> readFrom(Class<Iterator<Cue>> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws WebApplicationException {
        return read(entityStream);
    }
    abstract Iterator<Cue> read(InputStream entityStream);
}
