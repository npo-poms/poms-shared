package nl.vpro.domain;

import java.time.Instant;
import java.util.Objects;

import com.google.common.collect.Range;

/**
 * Utilities related to {@link Embargo}
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class Embargos {

    public static void copy(ReadonlyEmbargo from, Embargo to) {
        to.setPublishStartInstant(from.getPublishStartInstant());
        to.setPublishStopInstant(from.getPublishStopInstant());
    }

    public static void copyIfTargetUnset(ReadonlyEmbargo from, Embargo to) {
        if (to.getPublishStartInstant() == null) {
            to.setPublishStartInstant(from.getPublishStartInstant());
        }
        if (to.getPublishStopInstant() == null) {
            to.setPublishStopInstant(from.getPublishStopInstant());
        }
    }

    public static void copyIfSourceSet(ReadonlyEmbargo from, Embargo to) {
        if (from.getPublishStartInstant() != null) {
            to.setPublishStartInstant(from.getPublishStartInstant());
        }
        if (from.getPublishStopInstant() != null) {
            to.setPublishStopInstant(from.getPublishStopInstant());
        }
    }

    /**
     * Takes from both the start and stop of the two embargo's the least restrictive one and copies them to the second.
     */
    public static void copyIfMoreRestricted(ReadonlyEmbargo from, Embargo to) {
        if (from.getPublishStartInstant() != null &&
            (to.getPublishStartInstant() == null || to.getPublishStartInstant().isBefore(from.getPublishStartInstant()))
            ) {
            to.setPublishStartInstant(from.getPublishStartInstant());
        }
        if (from.getPublishStopInstant() != null &&
            (to.getPublishStopInstant() == null || to.getPublishStopInstant().isAfter(from.getPublishStopInstant()))
            ) {
            to.setPublishStopInstant(from.getPublishStopInstant());
        }
    }

    /**
     * Takes from both the start and stop of the two embargo's the least restrictive one and copies them to the second.
     *
     * Note that if the two embargos were not connected there will be times  (in between the two) which will be in de new embargo but, were in none.
     *
     * If they were connected, the result is the union.
     */
     public static void copyIfLessRestricted(ReadonlyEmbargo from, Embargo to) {
         if (from.getPublishStartInstant() == null ||
            (to.getPublishStartInstant() != null && to.getPublishStartInstant().isAfter(from.getPublishStartInstant()))
            ) {
            to.setPublishStartInstant(from.getPublishStartInstant());
        }
        if (from.getPublishStopInstant() == null ||
            (to.getPublishStopInstant() != null && to.getPublishStopInstant().isBefore(from.getPublishStopInstant()))
            ) {
            to.setPublishStopInstant(from.getPublishStopInstant());
        }
    }

     /**
     * Takes from both the start and stop of the two embargo's the least restrictive one and copies them to the second.
     *
     * Note that if the two embargos were not connected there will be times  (in between the two) which will be in de new embargo but, were in none.
     *
     * If they were connected, the result is the union.
     */
     public static void copyIfLessRestrictedOrTargetUnset(ReadonlyEmbargo from, Embargo to) {
         if (from.getPublishStartInstant() == null || to.getPublishStartInstant() == null || to.getPublishStartInstant().isAfter(from.getPublishStartInstant())) {
             to.setPublishStartInstant(from.getPublishStartInstant());
         }
         if (from.getPublishStopInstant() == null || to.getPublishStopInstant() == null || to.getPublishStopInstant().isBefore(from.getPublishStopInstant())) {
            to.setPublishStopInstant(from.getPublishStopInstant());
        }

    }

    public static ReadonlyEmbargo readyOnly(final Embargo embargo) {
        return readyOnly(
            embargo.getPublishStartInstant(),
            embargo.getPublishStopInstant()
        );
    }

    public static ReadonlyEmbargo readyOnly(final Instant start, final Instant stop) {
        return new ReadonlyEmbargo() {
            @Override
            public Instant getPublishStartInstant() {
                return start;

            }
            @Override
            public Instant getPublishStopInstant() {
                return stop;
            }
            @Override
            public String toString() {
                return Embargos.toString(this);
            }

        };
    }
    public static Embargo<BasicEmbargo> of(final Instant start, final Instant stop) {
        return new BasicEmbargo(start, stop);
    }

     public static Embargo<BasicEmbargo> of(Range<Instant> range) {
        BasicEmbargo result = new BasicEmbargo(null, null);
        result.set(range);
        return result;
    }

    public static Embargo<BasicEmbargo> of(ReadonlyEmbargo readonlyEmbargo) {
        return new BasicEmbargo(readonlyEmbargo.getPublishStartInstant(), readonlyEmbargo.getPublishStopInstant());
    }

    public static Embargo<BasicEmbargo> unrestrictedInstance() {
        return new BasicEmbargo(null,  null);
    }
    public static Embargo<BasicEmbargo> restrictedInstance() {
        return new BasicEmbargo(Instant.MAX, Instant.MIN);
    }

    public static boolean equals(ReadonlyEmbargo e1, ReadonlyEmbargo e2) {
         return Objects.equals(e1.getPublishStartInstant(), e2.getPublishStartInstant()) &&
             Objects.equals(e1.getPublishStopInstant(), e2.getPublishStopInstant());
    }

    public static String toString(ReadonlyEmbargo embargo) {
        Instant start = embargo.getPublishStartInstant();
        Instant stop = embargo.getPublishStopInstant();
        return "[" + (start == null ? "" : start) + "-" + (stop == null ? "" : stop) + "]";
    }

}
