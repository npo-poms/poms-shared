package nl.vpro.nep.domain;

import lombok.Getter;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since 5.24
 */
public enum ItemizerStatus implements Displayable {
    COMPLETED(true, "Klaar"),
    RUNNING(false, "Bezig"),
    QUEUED(false, "Gequeued"),
    STARTED(false, "Gestart"),
    RETRY(false, "Opnieuw proberen"),
    UNKNOWN(true, "Onbekend"),
    ERROR(true, "Fout"),
    CANCELLED(true, "Afgebroken");

    @Getter
    private final boolean endStatus;

    @Getter
    private final String displayName;

    ItemizerStatus(boolean endStatus, String displayName) {
        this.endStatus = endStatus;
        this.displayName = displayName;
    }
}
