package nl.vpro.api.rs.subtitles;

import java.io.InputStream;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.Cue;
import nl.vpro.domain.subtitles.EBUTXT;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Provider
@Consumes(Constants.EBUTXT)
public class EBUReader  extends AbstractIteratorReader {

    public EBUReader() {
        super(Constants.EBUTXT_TYPE);
    }

    @Override
    protected Iterator<Cue> read(InputStream entityStream) {
        return EBUTXT.parse(null, entityStream).iterator();
    }
}
