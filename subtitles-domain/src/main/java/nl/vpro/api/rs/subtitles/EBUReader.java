package nl.vpro.api.rs.subtitles;

import java.io.InputStream;
import java.time.Duration;
import java.util.Iterator;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.Cue;
import nl.vpro.domain.subtitles.EBU;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
@Provider
@Consumes(Constants.EBU)
public class EBUReader extends AbstractIteratorReader {

    public EBUReader() {
        super(Constants.EBU_TYPE);
    }

    @Override
    protected Iterator<Cue> read(InputStream entityStream) {
        return EBU.parse(null, null, (timeLine) -> Duration.ZERO,  entityStream).iterator();
    }
}
