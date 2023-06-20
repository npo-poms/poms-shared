package nl.vpro.domain;

import java.time.Instant;

/**
 * An 'changeable' object represent an entity that has {@link #getCreationInstant()}  and {@link #getLastModifiedInstant()}.
 * <p>
 * Also, it (optionally) can keep track whether it thinks currently {@link #hasChanges()}.
 *
 * @author Michiel Meeuwissen
 * @since 5.10
 */
public interface Changeable extends Trackable {

    default boolean hasChanges() {
        return true;
    }

    void setLastModifiedInstant(Instant lastModified);
    void setCreationInstant(Instant creationDate);

}
