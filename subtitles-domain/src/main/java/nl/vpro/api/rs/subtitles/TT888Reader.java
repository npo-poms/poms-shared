package nl.vpro.api.rs.subtitles;

import java.io.InputStream;
import java.time.Duration;
import java.util.Iterator;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.Cue;
import nl.vpro.domain.subtitles.TT888;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Provider
@Consumes(Constants.TT888)
public class TT888Reader extends AbstractIteratorReader {

    public TT888Reader() {
        super(Constants.TT888_TYPE);
    }

    @Override
    protected Iterator<Cue> read(InputStream entityStream) {
        return TT888.parse(null, null, (timeLine) -> Duration.ZERO, entityStream).iterator();
    }
}
