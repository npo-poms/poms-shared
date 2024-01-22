package nl.vpro.domain.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.Comparator;

import static java.util.Comparator.naturalOrder;

/**
 * Combines an {@link Instant} with a {@code mid}, together the defined the unique order the changes feed.
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@lombok.Builder
public class MediaSince implements Comparable<MediaSince> {

    private final Instant instant;

    private final String mid;


    static Instant instant(Instant explicit, MediaSince mediaSince) {
        if (explicit != null) {
            return explicit;
        }
        return mediaSince == null ? null : mediaSince.instant;
    }

    /**
     * @since 7.2
     */
    public static MediaSince of(Instant instant) {
        return MediaSince.builder().instant(instant).build();
    }

    /**
     * @since 7.2
     */
    public static MediaSince of(Instant instant, String mid) {
        return MediaSince.builder().instant(instant).mid(mid).build();
    }

    static String mid(String explicit, MediaSince mediaSince) {
        if (explicit != null) {
            return explicit;
        }
        return mediaSince == null ? null : mediaSince.mid;
    }

    /**
     * @since 7.2
     */
    public String asQueryParam() {
        String sinceString = instant == null ? null : instant.toString();
        if (mid != null && sinceString != null) {
            sinceString += "," + mid;
        }
        return sinceString;
    }

    /**
     * @since 7.2
     */
    public static String asQueryParam(MediaSince since) {
        return since == null ? null : since.asQueryParam();
    }

    /**
     * @since 7.2
     */
    @Override
    public int compareTo(MediaSince o) {
        return Comparator.comparing(MediaSince::getInstant, Comparator.nullsFirst(naturalOrder()))
            .thenComparing(MediaSince::getMid, Comparator.nullsFirst(naturalOrder())).compare(this, o);
    }

    /**
     * @since 7.11
     */
    public boolean isAfter(MediaSince other) {
        return compareTo(other) > 0;
    }

    /**
     * @since 7.11
     */
    public boolean isBefore(MediaSince other) {
        return compareTo(other) < 0;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + asQueryParam();
    }
}
