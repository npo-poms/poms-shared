package nl.vpro.domain;

import java.time.Instant;

import nl.vpro.domain.user.Editor;

import static nl.vpro.domain.AbstractPublishableObject_.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@SuppressWarnings("JavadocReference")
public class Accountables {

    public static void fillFor(Accountable accountable, Instant now, Editor currentUser) {
        accountable.setLastModifiedInstant(now);
        if (accountable.getCreationInstant() == null) {
            accountable.setCreationInstant(now);
        }
        accountable.setLastModifiedBy(currentUser);
        if (accountable.getCreatedBy() == null) {
            accountable.setCreatedBy(currentUser);
        }
    }


    /**
     * Used by implementations of {@link org.hibernate.Interceptor}
     */
    public static boolean updateEntity(
        Editor user,
        boolean updateLastModified,
        Accountable accountable, Object[] state, String[] propertyNames) {
        boolean updated = false;

        final Instant now = Instant.now();

        if(accountable.getCreationInstant() == null) {
            accountable.setCreationInstant(now);
            setProperty(creationInstant.getName(), accountable.getCreationInstant(), state, propertyNames);
            updated = true;
        }

        if(accountable.getCreatedBy() == null) {
            accountable.setCreatedBy(user);
            setProperty(createdBy.getName(), accountable.getCreatedBy(), state, propertyNames);
            updated = true;
        }

        if(accountable.getLastModifiedInstant() == null || (updateLastModified && accountable.hasChanges())) {
            accountable.setLastModifiedInstant(now);
            setProperty(lastModified.getName(), accountable.getLastModifiedInstant(), state, propertyNames);
            updated = true;
        }
        if(accountable.getLastModifiedBy() == null || (updateLastModified && accountable.hasChanges())) {
            accountable.setLastModifiedBy(user);
            setProperty(lastModifiedBy.getName(), accountable.getLastModifiedBy(), state, propertyNames);
            updated = true;
        }

        return updated;
    }

    /**
     * Used by implementations of {@link org.hibernate.Interceptor}
     */
    public static void setProperty(String propertyName, Object propertyValue, Object[] state, String[] propertyNames) {
        for(int i = 0; i < propertyNames.length; i++) {
            if(propertyNames[i].equals(propertyName)) {
                state[i] = propertyValue;
                break;
            }
        }
    }
}
