package nl.vpro.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.common.collect.Range;

/**
 * Utilities related to {@link Embargo} and {@link MutableEmbargo}
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class Embargos {

    private static final String PUBLISH_START = "publishstart";
    private static final String PUBLISH_STOP = "publishstop";

    private Embargos() {
    }

    public static ChangeReport copy(Embargo from, MutableEmbargo<?> to) {
        ChangeReport change = new ChangeReport();

        if (!Objects.equals(to.getPublishStartInstant(), from.getPublishStartInstant())) {
            to.setPublishStartInstant(from.getPublishStartInstant());
            change.change(PUBLISH_START);
        }
        if (!Objects.equals(to.getPublishStopInstant(), from.getPublishStopInstant())) {
            to.setPublishStopInstant(from.getPublishStopInstant());
            change.change(PUBLISH_STOP);
        }
        return change;
    }

    public static ChangeReport copyIfTargetUnset(Embargo from, MutableEmbargo<?> to) {
        ChangeReport change = new ChangeReport();
        if (to.getPublishStartInstant() == null && !Objects.equals(to.getPublishStartInstant(), from.getPublishStartInstant())) {
            to.setPublishStartInstant(from.getPublishStartInstant());
            change.change(PUBLISH_START);
        }
        if (to.getPublishStopInstant() == null && !Objects.equals(to.getPublishStopInstant(), from.getPublishStopInstant())) {
            to.setPublishStopInstant(from.getPublishStopInstant());
            change.change(PUBLISH_STOP);
        }
        return change;
    }

    public static ChangeReport copyIfSourceSet(Embargo from, MutableEmbargo<?> to) {
        ChangeReport change = new ChangeReport();
        if (from.getPublishStartInstant() != null && ! Objects.equals(to.getPublishStartInstant(), from.getPublishStartInstant())) {
            to.setPublishStartInstant(from.getPublishStartInstant());
            change.change(PUBLISH_START);
        }
        if (from.getPublishStopInstant() != null && ! Objects.equals(to.getPublishStopInstant(), from.getPublishStopInstant())) {
            to.setPublishStopInstant(from.getPublishStopInstant());
            change.change(PUBLISH_STOP);
        }
        return change;
    }

    /**
     * Takes from both the start and stop of the two embargo's the least restrictive one and copies them to the second.
     */
    public static ChangeReport copyIfMoreRestricted(Embargo from, MutableEmbargo<?> to) {
        ChangeReport change = new ChangeReport();
        if (from.getPublishStartInstant() != null &&
            (to.getPublishStartInstant() == null || to.getPublishStartInstant().isBefore(from.getPublishStartInstant()))
            ) {
            to.setPublishStartInstant(from.getPublishStartInstant());
            change.change(PUBLISH_START);
        }
        if (from.getPublishStopInstant() != null &&
            (to.getPublishStopInstant() == null || to.getPublishStopInstant().isAfter(from.getPublishStopInstant()))
            ) {
            to.setPublishStopInstant(from.getPublishStopInstant());
            change.change(PUBLISH_STOP);
        }
        return change;
    }

    /**
     * Takes from both the start and stop of the two embargo's the least restrictive one and copies them to the second.
     * <p>
     * Note that if the two embargos were not connected there will be times  (in between the two) which will be in de new embargo but, were in none.
     * <p>
     * If they were connected, the result is the union.
     */
     public static ChangeReport copyIfLessRestricted(Embargo from, MutableEmbargo<?> to) {
         ChangeReport change = new ChangeReport();

         if (from.getPublishStartInstant() == null ||
            (to.getPublishStartInstant() != null && to.getPublishStartInstant().isAfter(from.getPublishStartInstant()))
            ) {
            to.setPublishStartInstant(from.getPublishStartInstant());
             change.change(PUBLISH_START);
        }
        if (from.getPublishStopInstant() == null ||
            (to.getPublishStopInstant() != null && to.getPublishStopInstant().isBefore(from.getPublishStopInstant()))
            ) {
            to.setPublishStopInstant(from.getPublishStopInstant());
            change.change(PUBLISH_STOP);

        }
        return change;
    }

     /**
     * Takes from both the start and stop of the two embargo's the least restrictive one and copies them to the second.
     * <p>
     * Note that if the two embargos were not connected there will be times  (in between the two) which will be in de new embargo but, were in none.
     * <p>
     * If they were connected, the result is the union.
     */
     public static ChangeReport copyIfLessRestrictedOrTargetUnset(Embargo from, MutableEmbargo<?> to) {
         ChangeReport change = new ChangeReport();
         if (from.getPublishStartInstant() == null || to.getPublishStartInstant() == null || to.getPublishStartInstant().isAfter(from.getPublishStartInstant())) {
             to.setPublishStartInstant(from.getPublishStartInstant());
             change.change(PUBLISH_START);
         }
         if (from.getPublishStopInstant() == null || to.getPublishStopInstant() == null || to.getPublishStopInstant().isBefore(from.getPublishStopInstant())) {
            to.setPublishStopInstant(from.getPublishStopInstant());
             change.change(PUBLISH_STOP);
         }
         return change;
    }

    public static Embargo readyOnly(final MutableEmbargo<?> embargo) {
        return readyOnly(
            embargo.getPublishStartInstant(),
            embargo.getPublishStopInstant()
        );
    }

    public static Embargo readyOnly(final Instant start, final Instant stop) {
        return new Embargo() {

            private final Instant finalStop = stop == null || start == null ?
                stop : stop.isBefore(start) ? start : stop;
            @Override
            public Instant getPublishStartInstant() {
                return start;

            }
            @Override
            public Instant getPublishStopInstant() {
                return finalStop;
            }
            @Override
            public String toString() {
                return Embargos.toString(this);
            }

        };
    }
    public static MutableEmbargo<BasicEmbargo> of(final Instant start, final Instant stop) {
        return new BasicEmbargo(start, stop);
    }

    public static MutableEmbargo<WrappedEmbargo> of(
        Supplier<Instant> start,
        Consumer<Instant> startSetter,
        Supplier<Instant> stop,
        Consumer<Instant> stopSetter) {
        return new WrappedEmbargo(start, startSetter, stop, stopSetter);
    }


     public static MutableEmbargo<BasicEmbargo> of(Range<Instant> range) {
        BasicEmbargo result = new BasicEmbargo(null, null);
        result.set(range);
        return result;
    }

    public static MutableEmbargo<BasicEmbargo> of(Embargo readonlyEmbargo) {
        return new BasicEmbargo(readonlyEmbargo.getPublishStartInstant(), readonlyEmbargo.getPublishStopInstant());
    }

    public static MutableEmbargo<BasicEmbargo> unrestrictedInstance() {
        return new BasicEmbargo(null,  null);
    }

    /**
     * Just as {@link #unrestrictedInstance()}, but nicer for static imports (because in that case it doesn't show what Instance of)
     */
    public static MutableEmbargo<BasicEmbargo> unrestricted() {
        return unrestrictedInstance();
    }
    public static MutableEmbargo<BasicEmbargo> restrictedInstance() {
        return new BasicEmbargo(Instant.MAX, Instant.MIN);
    }

    public static boolean equals(Embargo e1, Embargo e2) {
         return
             Objects.equals(e1.getPublishStartInstant(), e2.getPublishStartInstant()) &&
                 Objects.equals(e1.getPublishStopInstant(), e2.getPublishStopInstant());
    }

    public static String toString(Embargo embargo) {
        Instant start = embargo.getPublishStartInstant();
        Instant stop = embargo.getPublishStopInstant();
        return "[" + (start == null ? "-∞" : start) + ".." + (stop == null ? "+∞" : stop) + "]";
    }


    @NonNull
    public static Instant getPublishStop(@Nullable Embargo readonlyEmbargo) {
        if (readonlyEmbargo == null) {
            return Instant.MAX;
        }
        Instant result = readonlyEmbargo.getPublishStopInstant();
        if (result == null) {
            return Instant.MAX;
        }
        return result;
    }
    @NonNull
    public static Instant getPublishStart(@Nullable Embargo readonlyEmbargo) {
        if (readonlyEmbargo == null) {
            return Instant.MIN;
        }
        Instant result = readonlyEmbargo.getPublishStartInstant();
        if (result == null) {
            return Instant.MIN;
        }
        return result;
    }

}
