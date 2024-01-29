package nl.vpro.api.rs.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.*;

import static nl.vpro.api.rs.subtitles.Constants.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Provider
@Produces(VTT_WITH_CHARSET)
@Slf4j
public class VTTSubtitlesWriter extends AbstractSubtitlesWriter {


    public VTTSubtitlesWriter() {
        super(SubtitlesFormat.WEBVTT);
    }

    @Override
    protected void stream(Subtitles subtitles, OutputStream entityStream) throws IOException {
        SubtitlesContent content = subtitles.getContent();
        if (subtitles.isAvoidParsing() && content.getFormat() == SubtitlesFormat.WEBVTT) {
            log.debug("The subtitles are already in webvtt format");
            entityStream.write(content.getValue());
        } else {
            SubtitlesUtil.toVTT(iterate(subtitles, false), entityStream);
        }
    }
}
