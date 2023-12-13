package nl.vpro.metis;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.Program;

/**
 * Every 'mid' has a type (basically just a prefix).
 *
 * This may indicate what kind of thing it is. E.g. 'WO' for 'Web Only'.
 * It also may indicate where it comes from. E.g. 'POMS' for POMS, 'SRCS' for Sourcing Service, 'RCRS' for Radio Content Repository Service.
 * @author Michiel Meeuwissen
 * @since 1.7
 */
public enum IdType {
    /**
     * WebOnly
     */
    WO,
    /**
     * Livestreams
     */
    LI,
    /**
     * Voor thema-kanalen
     */
    THX,
    /**
     * Voor allerlei POMS-specifieke zaken. Locaties, Segmenten, Images, dat soort dingen.
     */
    POMS,

    /**
     * Sourcing Service
     */
    SRCS,

    /**
     * Voor RCRS. Radio content repository service (of zoiets?)
     */
    RCRS,

    /**
     * Voor PREPR
     */
    PREPR;

    public static IdType of(@Nullable MediaType pomsType) {
        if (pomsType != null && Program.class == pomsType.getMediaObjectClass()) {
            return switch (pomsType) {
                case STRAND, BROADCAST -> IdType.POMS;
                default -> IdType.WO;
            };
        } else {
            return IdType.POMS;
        }

    }



}
