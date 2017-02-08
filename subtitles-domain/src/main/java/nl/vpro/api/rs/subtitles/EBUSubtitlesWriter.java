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
@Produces(Constants.EBU)
public class EBUSubtitlesWriter extends AbstractSubtitlesWriter {


    public EBUSubtitlesWriter() {
        super(Constants.EBU_TYPE);
    }

    @Override
    protected void stream(Subtitles subtitles, OutputStream entityStream) throws IOException {
        SubtitlesUtil.toEBU(SubtitlesUtil.iterator(subtitles), entityStream);
    }

}
