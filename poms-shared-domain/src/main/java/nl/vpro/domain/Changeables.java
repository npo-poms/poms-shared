package nl.vpro.domain;

import java.time.Instant;

/**
 *
 * @author Michiel Meeuwissen
 * @since 5.10
 */
public class Changeables {

    public static void fillFor(Changeable accountable, Instant now) {
        accountable.setLastModifiedInstant(now);
        if (accountable.getCreationInstant() == null) {
            accountable.setCreationInstant(now);
        }
    }

     public static void copyFrom(Changeable source, Changeable target) {
        target.setLastModifiedInstant(source.getLastModifiedInstant());
        target.setCreationInstant(source.getCreationInstant());
    }

    public static  void copyFromIfTargetUnset(Changeable source, Changeable target) {

        if (target.getLastModifiedInstant() == null) {
            target.setLastModifiedInstant(source.getLastModifiedInstant());
        }
        if (target.getCreationInstant() == null) {
            target.setCreationInstant(source.getCreationInstant());
        }
    }


    public static void copyFromIfSourceSet(Changeable source, Changeable target) {
        if (source.getLastModifiedInstant() != null) {
            target.setLastModifiedInstant(source.getLastModifiedInstant());
        }
        if (source.getCreationInstant() != null) {
            target.setCreationInstant(source.getCreationInstant());
        }
    }


    /**
     * Used by implementations of {@link org.hibernate.Interceptor}
     * @since 5.11
     */
    public static boolean updateEntity(
        Changeable changeable,
        boolean updateLastModified,
        String creationInstantProperty,
        String lastModifiedInstantProperty,
        Object[] state,
        String[] propertyNames) {
        boolean updated = false;

        final Instant now = Instant.now();

        if(changeable.getCreationInstant() == null) {
            changeable.setCreationInstant(now);
            setProperty(creationInstantProperty, changeable.getCreationInstant(), state, propertyNames);
            updated = true;
        }
        if(changeable.getLastModifiedInstant() == null || (updateLastModified && changeable.hasChanges())) {
            changeable.setLastModifiedInstant(now);
            setProperty(lastModifiedInstantProperty, changeable.getLastModifiedInstant(), state, propertyNames);
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
