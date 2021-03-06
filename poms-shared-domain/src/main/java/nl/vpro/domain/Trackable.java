package nl.vpro.domain;

import java.time.Instant;

/**
 * Has information about when it as created and last modified.
 *
 * This is more or less an immutable version of {@link Changeable}
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface Trackable {
    default boolean hasChanges() {
        return true;
    }
    Instant getLastModifiedInstant();
    Instant getCreationInstant();

}
