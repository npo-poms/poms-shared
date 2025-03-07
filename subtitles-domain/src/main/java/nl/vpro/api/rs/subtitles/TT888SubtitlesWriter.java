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
@Produces(TT888_WITH_CHARSET)
public class TT888SubtitlesWriter extends AbstractSubtitlesWriter {


    public TT888SubtitlesWriter() {
        super(SubtitlesFormat.TT888);
    }

    @Override
    protected void stream(Subtitles subtitles, OutputStream outputStream) throws IOException {
        SubtitlesUtil.toTT888(iterate(subtitles, false), outputStream);
    }

}
