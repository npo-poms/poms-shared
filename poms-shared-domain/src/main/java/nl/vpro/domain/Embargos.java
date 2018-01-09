package nl.vpro.domain;

import java.time.Instant;

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
        };
    }
    public static Embargo of(final Instant start, final Instant stop) {
        return new BasicEmbargo(start, stop);
    }

}
