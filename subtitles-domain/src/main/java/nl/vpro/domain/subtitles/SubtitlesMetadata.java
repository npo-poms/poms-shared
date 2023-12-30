package nl.vpro.domain.subtitles;

import java.time.Duration;

import nl.vpro.domain.media.support.OwnerType;

/**
 * A cheaper view on {@link Subtitles}, most fields, but most importantly not {@link Subtitles#getContent()}
 *
 * @author Michiel Meeuwissen
 * @since 5.5
 */

public interface SubtitlesMetadata {

    SubtitlesId getId();
    OwnerType getOwner();
    Duration getOffset();
    Integer getCueCount();
    SubtitlesWorkflow getWorkflow();
}
