package nl.vpro.api.rs.subtitles;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.SubtitlesFormat;
import nl.vpro.domain.subtitles.WEBVTTandSRT;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Provider
@Consumes(Constants.SRT)
public class SRTSubtitlesReader extends AbstractSubtitlesReader {


    public SRTSubtitlesReader() {
        super(Constants.SRT_TYPE, WEBVTTandSRT.SRT_CHARSET, SubtitlesFormat.SRT);
    }

}
