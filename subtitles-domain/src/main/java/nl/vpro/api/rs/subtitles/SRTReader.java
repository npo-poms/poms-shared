package nl.vpro.api.rs.subtitles;

import java.io.InputStream;
import java.util.Iterator;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.Cue;
import nl.vpro.domain.subtitles.WEBVTTandSRT;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Provider
@Consumes(Constants.SRT)
public class SRTReader extends AbstractIteratorReader {


    public SRTReader() {
        super(Constants.SRT_TYPE);
    }

    @Override
    protected Iterator<Cue> read(InputStream entityStream) {
        return WEBVTTandSRT.parseSRT(null, entityStream).iterator();
    }
}
