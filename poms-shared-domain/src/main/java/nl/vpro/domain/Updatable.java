package nl.vpro.domain;

import java.time.Instant;

import org.apache.commons.lang3.StringUtils;

/**
 * An 'updatable' object represent an entity that has {@link #getCreationInstant()}  and {@link #getLastModifiedInstant()}.
 *
 * @author Michiel Meeuwissen
 * @since 5.10
 */
public interface Updatable {
     default boolean hasChanges() {
        return true;
    }
    Instant getLastModifiedInstant();
    void setLastModifiedInstant(Instant lastModified);
    Instant getCreationInstant();
    void setCreationInstant(Instant creationDate);



}
