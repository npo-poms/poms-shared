package nl.vpro.domain;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Utilities related to {@link Changeable}
 * @author Michiel Meeuwissen
 * @since 5.10
 */
@SuppressWarnings("JavadocReference")
public class Changeables {

    public static void fillFor(@Nonnull Changeable accountable, @Nonnull Instant now) {
        accountable.setLastModifiedInstant(now);
        if (accountable.getCreationInstant() == null) {
            accountable.setCreationInstant(now);
        }
    }

     public static void copyFrom(@Nonnull Changeable source, @Nonnull Changeable target) {
        target.setLastModifiedInstant(source.getLastModifiedInstant());
        target.setCreationInstant(source.getCreationInstant());
    }

    public static  void copyFromIfTargetUnset(@Nonnull Changeable source, @Nonnull Changeable target) {

        if (target.getLastModifiedInstant() == null) {
            target.setLastModifiedInstant(source.getLastModifiedInstant());
        }
        if (target.getCreationInstant() == null) {
            target.setCreationInstant(source.getCreationInstant());
        }
    }


    public static void copyFromIfSourceSet(@Nonnull Changeable source,  @Nonnull Changeable target) {
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
        @Nonnull Changeable changeable,
        boolean updateLastModified,
        @Nonnull String creationInstantProperty,
        @Nonnull String lastModifiedInstantProperty,
        @Nonnull Object[] state,
        @Nonnull String[] propertyNames) {
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
    public static void setProperty(@Nonnull String propertyName, @Nonnull Object propertyValue, @Nonnull Object[] state, @Nonnull String[] propertyNames) {
        for(int i = 0; i < propertyNames.length; i++) {
            if(propertyNames[i].equals(propertyName)) {
                state[i] = propertyValue;
                break;
            }
        }
    }
    public static void headers(Changeable changeable, Map<String, List<Object>> httpHeaders) {
        httpHeaders.put("Last-Modified", Arrays.asList(changeable.getLastModifiedInstant()));
        httpHeaders.put("X-Created", Arrays.asList(changeable.getCreationInstant()));
    }

    public static void fillFromHeaders(Changeable changeable, Map<String, ? extends List<?>> httpHeaders) {

        // TODO
    }


}
