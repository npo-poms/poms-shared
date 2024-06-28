package nl.vpro.domain;

import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Utilities related to {@link Changeable}
 * @author Michiel Meeuwissen
 * @since 5.10
 */
public class Changeables {

    public static final ThreadLocal<Clock> CLOCK = ThreadLocal.withInitial(Clock::systemUTC);

    /**
     * Returns the clock that is for determining default creation dates and such things. Also, it used by {@link Embargo}.
     * <p>
     * The clock is a Thread local, and normally is just {@link Clock#systemUTC()}, but e.g. during testing it may be some
     * other clock implementation (like {@link Clock#fixed(Instant, ZoneId)}, or {@link org.meeuw.math.time.TestClock}).
     * @since 5.31
     */
    public static Clock clock() {
        return CLOCK.get();
    }

    /**
     * Shortcut to {@link #clock()}.{@link Clock#instant() instant()}
     */
    public static Instant instant() {
        return clock().instant();
    }

    private Changeables() {
    }

    /**
     * Calls both {@link Changeable#setLastModifiedInstant(Instant)} and {@link Changeable#setCreationInstant(Instant)}. The second one only if presently unset.
     */
    public static void fillFor(@NonNull Changeable accountable, @NonNull Instant now) {
        accountable.setLastModifiedInstant(now);
        if (accountable.getCreationInstant() == null) {
            accountable.setCreationInstant(now);
        }
    }

    public static void copyFrom(@NonNull Changeable source, @NonNull Changeable target) {
        target.setLastModifiedInstant(source.getLastModifiedInstant());
        target.setCreationInstant(source.getCreationInstant());
    }

    public static  void copyFromIfTargetUnset(@NonNull Changeable source, @NonNull Changeable target) {

        if (target.getLastModifiedInstant() == null) {
            target.setLastModifiedInstant(source.getLastModifiedInstant());
        }
        if (target.getCreationInstant() == null) {
            target.setCreationInstant(source.getCreationInstant());
        }
    }


    public static void copyFromIfSourceSet(@NonNull Changeable source,  @NonNull Changeable target) {
        if (source.getLastModifiedInstant() != null) {
            target.setLastModifiedInstant(source.getLastModifiedInstant());
        }
        if (source.getCreationInstant() != null) {
            target.setCreationInstant(source.getCreationInstant());
        }
    }


    /**
     * Used by implementations of {@code org.hibernate.Interceptor}. Fills {@link Changeable#setLastModifiedInstant(Instant)} and {@link Changeable#setCreationInstant(Instant)} (if needed).
     * @since 5.11
     */
    public static boolean updateEntity(
        @NonNull Changeable changeable,
        boolean updateLastModified,
        @NonNull String creationInstantProperty,
        @NonNull String lastModifiedInstantProperty,
        @NonNull Object[] state,
        @NonNull String[] propertyNames) {
        boolean updated = false;

        final Instant now = instant();

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
     * Used by implementations of {@code org.hibernate.Interceptor}
     */
    public static void setProperty(
        @NonNull String propertyName,
        @Nullable Object propertyValue,
        @Nullable Object[] state,
        @NonNull String[] propertyNames) {
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
