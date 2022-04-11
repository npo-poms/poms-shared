package nl.vpro.domain;

import java.time.Instant;

/**
 * Has information about when it was created and last modified.
 *
 * This is more or less an immutable version of {@link Changeable}
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface Trackable {

    Instant getLastModifiedInstant();
    Instant getCreationInstant();

}
