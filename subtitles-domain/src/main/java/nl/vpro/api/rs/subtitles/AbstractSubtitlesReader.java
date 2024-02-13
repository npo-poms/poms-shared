package nl.vpro.api.rs.subtitles;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;

import nl.vpro.domain.Changeables;
import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesFormat;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */

abstract class AbstractSubtitlesReader implements MessageBodyReader<Subtitles> {

    private final MediaType mediaType;
    private final SubtitlesFormat format;

    public AbstractSubtitlesReader(SubtitlesFormat format) {
        this.mediaType = MediaType.valueOf(format.getMediaType());
        this.format = format;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return mediaType.isCompatible(this.mediaType) && Subtitles.class.isAssignableFrom(type);
    }

    @Override
    public Subtitles readFrom(Class<Subtitles> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws WebApplicationException {
        Subtitles subtitles = read(entityStream, format);
        Changeables.fillFromHeaders(subtitles, httpHeaders);
        return subtitles;
    }

    protected static Subtitles read(InputStream entityStream, SubtitlesFormat format) {
         return Subtitles.builder()
            .value(entityStream)
            .format(format)
            .build();
    }
}
