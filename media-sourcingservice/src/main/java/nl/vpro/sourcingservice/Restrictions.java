package nl.vpro.sourcingservice;

import lombok.Data;

import nl.vpro.domain.media.AgeRating;
import nl.vpro.domain.media.GeoRestriction;

@Data
public
class Restrictions {
    GeoRestriction geoRestriction;
    AgeRating ageRating;
}
