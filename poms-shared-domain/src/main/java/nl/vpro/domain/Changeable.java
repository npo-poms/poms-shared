package nl.vpro.domain;

import java.time.Instant;

/**
 * An 'changeable' object represent an entity that has {@link #getCreationInstant()}  and {@link #getLastModifiedInstant()}.
 *
 * Also, it (optionally) can keep track whether it thinks currenlty {@link #hasChanges()}.
 *
 * @author Michiel Meeuwissen
 * @since 5.10
 */
public interface Changeable {
     default boolean hasChanges() {
        return true;
    }
    Instant getLastModifiedInstant();
    void setLastModifiedInstant(Instant lastModified);
    Instant getCreationInstant();
    void setCreationInstant(Instant creationDate);



}
