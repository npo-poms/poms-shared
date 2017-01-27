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

import nl.vpro.domain.subtitles.StandaloneCue;
import nl.vpro.util.CountedIterator;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Provider
@Consumes(Constants.VTT)
public class SRTReader implements MessageBodyReader<CountedIterator<StandaloneCue>> {


    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return mediaType.isCompatible(Constants.VTT_TYPE) && Iterator.class.isAssignableFrom(type);

    }

    @Override
    public CountedIterator<StandaloneCue> readFrom(Class<CountedIterator<StandaloneCue>> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {

        return null;

    }
}
