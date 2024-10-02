package nl.vpro.berlijn.domain.availability;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import nl.vpro.berlijn.domain.AssertValidatable;
import nl.vpro.berlijn.validation.IntegerValues;


/**
 *
 * See <a href="https://publiekeomroep.atlassian.net/wiki/spaces/MS/pages/2964521011/Media+Availability+service#Datamodel">datamodel</a>
 *
 * @param predictionStartTimestamp This I think is data from the voorspel-feed (I saw in code of MDS).
 *
 */
@JsonIgnoreProperties({
    "s3FilePath",

    // all these fields are sometimes present, but are all meaningless, they shouldn't exist any more
    "restrictions",
    "restrictionsTimestamp",
    "notify",
    "revokedTimestamp",
    "predictionCurrentlyIncluded",
    "predictionStopped",
    "notifyTimestamp",
    "predictionUpdatedTimestamp",
    "revoked",
    "date"

})
@lombok.Builder
public record AvailabilityContents(
     String prid,
     Platform platform,
     Instant created,
     Instant lastUpdated,
     @IntegerValues({16, 18})
     Integer ageRestriction,
     List<String> broadcasters,
     List<AvailabilityPeriod> availabilityPeriods,
     Revoke revoke,
     Source source,
     Instant  predictionStartTimestamp,
     Instant  predictionEndTimestamp


) implements AssertValidatable {
    @Override
    public void assertValid() {

    }
}
