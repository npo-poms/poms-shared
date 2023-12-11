package nl.vpro.metis;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.Program;

/**
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
