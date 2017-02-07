package nl.vpro.domain;

import java.time.Instant;

import nl.vpro.domain.user.Editor;

/**
 * The basic accountability fields like 'last modified by', 'last modified instant' and 'created by'.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface Accountable {

    boolean hasChanges();

    /**
     * Accept the mutations on this object. Saving an object directly after calling this, will not update last modified
     */
    void acceptChanges();

    Instant getLastModifiedInstant();
    void setLastModifiedInstant(Instant lastModified);
    Instant getCreationInstant();
    void setCreationInstant(Instant creationDate);

    Editor getCreatedBy();
    void setCreatedBy(Editor createdBy);
    Editor getLastModifiedBy();
    void setLastModifiedBy(Editor lastModifiedBy);


}
