package nl.vpro.nep.domain;

import lombok.Getter;

/**
 * @author Michiel Meeuwissen
 * @since 5.24
 */
public enum ItemizerStatus {
    COMPLETED(true),
    RUNNING(false),
    QUEUED(false),
    STARTED(false),
    RETRY(false),
    UNKNOWN(true),
    ERROR(true),
    CANCELLED(true);

    @Getter
    private final boolean endStatus;
;

    ItemizerStatus(boolean endStatus) {
        this.endStatus = endStatus;
    }
}
