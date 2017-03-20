package nl.vpro.domain;

import java.time.Instant;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class Embargos {

    public static void copy(Embargo from, Embargo to) {
        to.setPublishStartInstant(from.getPublishStartInstant());
        to.setPublishStopInstant(from.getPublishStopInstant());
    }

    public static void copyIfTargetUnset(Embargo from, Embargo to) {
        if (to.getPublishStartInstant() == null) {
            to.setPublishStartInstant(from.getPublishStartInstant());
        }
        if (to.getPublishStopInstant() == null) {
            to.setPublishStopInstant(from.getPublishStopInstant());
        }
    }

    public static Embargo readyOnly(final Instant start, final Instant stop) {
        return new Embargo() {
            @Override
            public Instant getPublishStartInstant() {
                return start;

            }

            @Override
            public Embargo setPublishStartInstant(Instant publishStart) {
                throw new UnsupportedOperationException();

            }

            @Override
            public Instant getPublishStopInstant() {
                return stop;

            }

            @Override
            public Embargo setPublishStopInstant(Instant publishStop) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
