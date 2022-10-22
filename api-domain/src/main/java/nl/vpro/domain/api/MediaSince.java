package nl.vpro.domain.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

/**
 * Combines an {@link Instant} with a {@code mid}, together the defined the unique order the changes feed.
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@lombok.Builder
public class MediaSince {

    private final Instant instant;

    private final String mid;


    static Instant instant(Instant explicit, MediaSince mediaSince) {
        if (explicit != null) {
            return explicit;
        }
        return mediaSince == null ? null : mediaSince.instant;
    }

    static String mid(String explicit, MediaSince mediaSince) {
        if (explicit != null) {
            return explicit;
        }
        return mediaSince == null ? null : mediaSince.mid;
    }
}
