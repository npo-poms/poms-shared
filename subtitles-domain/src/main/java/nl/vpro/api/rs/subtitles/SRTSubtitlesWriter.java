package nl.vpro.api.rs.subtitles;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Provider
@Produces(Constants.SRT)
public class SRTSubtitlesWriter extends AbstractSubtitlesWriter {


    public SRTSubtitlesWriter() {
        super(Constants.SRT_TYPE);
    }

    @Override
    protected void stream(Subtitles subtitles, OutputStream entityStream) throws IOException {
        SubtitlesUtil.toSRT(SubtitlesUtil.iterator(subtitles), entityStream);
    }

}
