package nl.vpro.domain;

import java.time.Instant;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.user.Editor;

import static nl.vpro.domain.AbstractPublishableObject_.*;
import static nl.vpro.domain.Changeables.setProperty;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@SuppressWarnings("JavadocReference")
public class Accountables {

    private Accountables() {
    }

    public static void fillFor(
        @NonNull Accountable accountable,
        @NonNull Instant now,
        @NonNull Editor currentUser) {
        Changeables.fillFor(accountable, now);
        accountable.setLastModifiedBy(currentUser);
        if (accountable.getCreatedBy() == null) {
            accountable.setCreatedBy(currentUser);
        }
    }


    /**
     * Used by implementations of {@link org.hibernate.Interceptor}. Fills in {@link Accountable#setCreatedBy(Editor)} and {@link Accountable#setLastModifiedBy(Editor)}, in
     * addition to {@link Changeables#updateEntity(Changeable, boolean, String, String, Object[], String[])}.
     */
    public static boolean updateEntity(
        @NonNull Editor user,
        boolean updateLastModified,
        @NonNull Accountable accountable,
        @NonNull Object[] state,
        @NonNull String[] propertyNames) {
        boolean updated = false;

        Changeables.updateEntity(accountable, updateLastModified,  CREATION_INSTANT, LAST_MODIFIED, state, propertyNames);

        if(accountable.getCreatedBy() == null) {
            accountable.setCreatedBy(user);
            setProperty(CREATED_BY, accountable.getCreatedBy(), state, propertyNames);
            updated = true;
        }

        if(accountable.getLastModifiedBy() == null || (updateLastModified && accountable.hasChanges())) {
            accountable.setLastModifiedBy(user);
            setProperty(LAST_MODIFIED_BY, accountable.getLastModifiedBy(), state, propertyNames);
            updated = true;
        }

        return updated;
    }


}
