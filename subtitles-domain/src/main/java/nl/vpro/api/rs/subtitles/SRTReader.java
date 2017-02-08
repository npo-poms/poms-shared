package nl.vpro.api.rs.subtitles;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;

import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesFormat;
import nl.vpro.domain.subtitles.WEBVTTandSRT;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Provider
@Consumes(Constants.VTT)
public class SRTReader implements MessageBodyReader<Subtitles> {


    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return mediaType.isCompatible(Constants.VTT_TYPE) && Subtitles.class.isAssignableFrom(type);

    }

    @Override
    public Subtitles readFrom(Class<Subtitles> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(new InputStreamReader(entityStream, WEBVTTandSRT.SRT_CHARSET), writer);
        return new Subtitles(null, null, null, SubtitlesFormat.SRT, writer.toString());
    }
}
