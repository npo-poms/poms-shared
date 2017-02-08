package nl.vpro.api.rs.subtitles;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.Cue;
import nl.vpro.domain.subtitles.SubtitlesUtil;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Provider
@Produces(Constants.EBU)
public class EBUWriter extends AbstractIteratorWriter {


    public EBUWriter() {
        super(Constants.EBU_TYPE);
    }

    @Override
    protected void stream(Iterator<Cue> cueIterator, OutputStream entityStream) throws IOException {
        SubtitlesUtil.toEBU(cueIterator, entityStream);
    }



}
