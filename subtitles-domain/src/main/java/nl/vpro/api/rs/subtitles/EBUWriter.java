package nl.vpro.api.rs.subtitles;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.Cue;
import nl.vpro.domain.subtitles.SubtitlesFormat;
import nl.vpro.domain.subtitles.SubtitlesUtil;

import static nl.vpro.api.rs.subtitles.Constants.*;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Provider
@Produces(EBUTXT)
public class EBUWriter extends AbstractIteratorWriter {


    public EBUWriter() {
        super(SubtitlesFormat.EBUTXT);
    }

    @Override
    protected void stream(Iterator<Cue> cueIterator, OutputStream entityStream) throws IOException {
        SubtitlesUtil.toEBU(cueIterator, entityStream);
    }



}
