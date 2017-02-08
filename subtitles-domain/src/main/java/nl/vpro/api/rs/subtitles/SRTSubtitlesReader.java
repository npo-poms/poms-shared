package nl.vpro.api.rs.subtitles;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

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
