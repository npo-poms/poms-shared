package nl.vpro.api.rs.subtitles;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.SubtitlesFormat;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Provider
@Consumes(Constants.TT888)
public class TT888SubtitlesReader extends AbstractSubtitlesReader {


    public TT888SubtitlesReader() {
        super(SubtitlesFormat.TT888);
    }

}
