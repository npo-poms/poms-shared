package nl.vpro.sourcingservice;

import lombok.Data;

import nl.vpro.domain.media.AgeRating;
import nl.vpro.domain.media.GeoRestriction;

@Data
@Deprecated
public class Restrictions {

    GeoRestriction geoRestriction;

    /**
     * The age restriction determines at which hours of the day an object may be broadcast.
     * <p>
     * Modelled as a NICAM {@link AgeRating}, but it is not really the same.
     */
    AgeRating ageRating;
}
