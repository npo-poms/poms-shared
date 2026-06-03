package nl.vpro.berlijn.domain.productmetadata;

public enum ContentType {

    /**
     * Season
     */
    season,
    series,
    /**
     * A {@link nl.vpro.domain.media.ProgramType#BROADCAST} that actually is an episode of a {@link nl.vpro.domain.media.GroupType#SEASON}
     */
    episode,
    /**
     * A {@link nl.vpro.domain.media.ProgramType#BROADCAST} that is not part of a series or season?
     */
    programme
}
