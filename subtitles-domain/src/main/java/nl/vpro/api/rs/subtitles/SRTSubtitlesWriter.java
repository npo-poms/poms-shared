package nl.vpro.api.rs.subtitles;

import java.io.IOException;
import java.io.OutputStream;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesFormat;
import nl.vpro.domain.subtitles.SubtitlesUtil;

import static nl.vpro.api.rs.subtitles.Constants.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Provider
@Produces(SRT_WITH_CHARSET)
public class SRTSubtitlesWriter extends AbstractSubtitlesWriter {


    public SRTSubtitlesWriter() {
        super(SubtitlesFormat.SRT);
    }

    @Override
    protected void stream(Subtitles subtitles, OutputStream entityStream) throws IOException {
        SubtitlesUtil.toSRT(iterate(subtitles, false), entityStream);
    }

}
