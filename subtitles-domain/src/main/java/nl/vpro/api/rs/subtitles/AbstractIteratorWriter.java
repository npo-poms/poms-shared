package nl.vpro.api.rs.subtitles;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;

import nl.vpro.domain.subtitles.Cue;
import nl.vpro.domain.subtitles.SubtitlesFormat;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
abstract class AbstractIteratorWriter implements MessageBodyWriter<Iterator<Cue>> {

    private final MediaType mediaType;
    private final String extension;

    public AbstractIteratorWriter(SubtitlesFormat format) {
        this.mediaType = MediaType.valueOf(format.getMediaType());
        this.extension = format.getExtension();
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return mediaType.isCompatible(this.mediaType) && Iterator.class.isAssignableFrom(type);

    }

    @Override
    public long getSize(Iterator<Cue> cueIterator, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Iterator<Cue> cueIterator, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        cueIterator = Util.headers(cueIterator, httpHeaders, extension);
        stream(cueIterator, entityStream);
    }

    protected abstract void stream(Iterator<Cue> cueIterator, OutputStream entityStream) throws IOException;



}
