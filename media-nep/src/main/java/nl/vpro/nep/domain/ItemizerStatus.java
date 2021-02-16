package nl.vpro.nep.domain;

import lombok.Getter;

/**
 * @author Michiel Meeuwissen
 * @since 5.24
 */
public enum ItemizerStatus {
    COMPLETED(false),
    RUNNING(true),
    QUEUED(true),
    STARTED(true),
    RETRY(true),
    UNKNOWN(false),
    ERROR(false),
    CANCELLED(false);

    @Getter
    final boolean busy;

    ItemizerStatus(boolean busy) {
        this.busy = busy;
    }
}
