package nl.vpro.api.rs.subtitles;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.SubtitlesFormat;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Provider
@Consumes(Constants.SRT)
public class SRTSubtitlesReader extends AbstractSubtitlesReader {


    public SRTSubtitlesReader() {
        super(SubtitlesFormat.SRT);
    }

}
