package nl.vpro.api.rs.subtitles;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesFormat;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */

abstract class AbstractSubtitlesReader implements MessageBodyReader<Subtitles> {

    private final MediaType mediaType;
    private final Charset charset;
    private final SubtitlesFormat format;

    public AbstractSubtitlesReader(SubtitlesFormat format) {
        this.mediaType = MediaType.valueOf(format.getMediaType());
        this.charset = format.getCharset();
        this.format = format;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return mediaType.isCompatible(this.mediaType) && Subtitles.class.isAssignableFrom(type);
    }

    @Override
    public Subtitles readFrom(Class<Subtitles> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        return Subtitles.builder()
            .value(entityStream)
            .format(format)
            .build();
    }
}
