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

import nl.vpro.domain.subtitles.StandaloneCue;
import nl.vpro.domain.subtitles.SubtitlesUtil;
import nl.vpro.util.CountedIterator;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Provider
@Produces(Constants.EBU)
public class EBUWriter implements MessageBodyWriter<CountedIterator<StandaloneCue>> {


    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return mediaType.isCompatible(Constants.EBU_TYPE) && Iterator.class.isAssignableFrom(type);

    }

    @Override
    public long getSize(CountedIterator<StandaloneCue> cueIterator, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;

    }

    @Override
    public void writeTo(CountedIterator<StandaloneCue> cueIterator, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        Iterator<StandaloneCue> i = Util.headers(cueIterator, httpHeaders, "ebu");
        SubtitlesUtil.toEBU(i, entityStream);
    }



}
