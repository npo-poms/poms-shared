package nl.vpro.api.rs.subtitles;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.EBU;
import nl.vpro.domain.subtitles.SubtitlesFormat;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Provider
@Consumes(Constants.EBU)
public class EBUSubtitlesReader extends AbstractSubtitlesReader {


    public EBUSubtitlesReader() {
        super(Constants.EBU_TYPE, EBU.EBU_CHARSET, SubtitlesFormat.EBU);
    }

}
