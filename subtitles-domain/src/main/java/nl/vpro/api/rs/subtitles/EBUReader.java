package nl.vpro.api.rs.subtitles;

import java.io.InputStream;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.Cue;
import nl.vpro.domain.subtitles.TT888;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Provider
@Consumes(Constants.TT888)
public class EBUReader  extends AbstractIteratorReader {

    public EBUReader() {
        super(Constants.TT888_TYPE);
    }

    @Override
    protected Iterator<Cue> read(InputStream entityStream) {
        return TT888.parse(null, entityStream).iterator();
    }
}
