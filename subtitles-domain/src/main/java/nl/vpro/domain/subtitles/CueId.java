package nl.vpro.domain.subtitles;

import lombok.Getter;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
@Getter
public class CueId  {

    private final SubtitlesId subtitlesId;

    private final Integer sequence;

    public CueId(SubtitlesId subtitlesId, Integer sequence) {
        this.subtitlesId = subtitlesId;
        this.sequence = sequence;
    }
}
