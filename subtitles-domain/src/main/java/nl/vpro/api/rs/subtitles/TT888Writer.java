package nl.vpro.api.rs.subtitles;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.Cue;
import nl.vpro.domain.subtitles.SubtitlesFormat;
import nl.vpro.domain.subtitles.SubtitlesUtil;

import static nl.vpro.api.rs.subtitles.Constants.*;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Provider
@Produces(TT888)
public class TT888Writer extends AbstractIteratorWriter {


    public TT888Writer() {
        super(SubtitlesFormat.TT888);
    }

    @Override
    protected void stream(Iterator<Cue> cueIterator, OutputStream entityStream) throws IOException {
        SubtitlesUtil.toTT888(cueIterator, entityStream);
    }



}
