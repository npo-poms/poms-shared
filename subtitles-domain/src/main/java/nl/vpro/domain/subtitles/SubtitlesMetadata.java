package nl.vpro.domain.subtitles;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Data
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder")
public class SubtitlesMetadata {

    private SubtitlesId id;
    private SubtitlesOwnerType owner;
    private Duration offset;
    private Integer cueCount;


}
