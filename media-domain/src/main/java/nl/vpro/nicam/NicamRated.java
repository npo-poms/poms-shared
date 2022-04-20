package nl.vpro.nicam;

import java.util.List;

import nl.vpro.domain.media.AgeRating;
import nl.vpro.domain.media.ContentRating;

/**
 * See <a href "https://nicam.nl/">NICAM</a>
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public interface NicamRated {

    AgeRating getAgeRating();
    List<ContentRating> getContentRatings();

    default boolean isNicamRated() {
        List<ContentRating> cr = getContentRatings();
        return getAgeRating() != null || (cr != null && !cr.isEmpty());
    }
}
