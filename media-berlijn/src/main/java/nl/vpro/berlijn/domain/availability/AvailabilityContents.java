package nl.vpro.berlijn.domain.availability;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import nl.vpro.berlijn.domain.AssertValidatable;

@JsonIgnoreProperties({
    "s3FilePath"
})
public record AvailabilityContents(
    Instant restrictionsTimestamp,
    Instant notifyTimestamp,
    Instant lastUpdated,
    Instant created,

    Integer ageRestriction,

    String prid,

    /**
     * On two levels?
     */
    List<AvailabilityPeriod> availabilityPeriods,


    Platform platform,
    Boolean predictionCurrentlyIncluded,
    Instant predictionStartTimestamp,
    Instant predictionEndTimestamp,
    Instant predictionUpdatedTimestamp,
    @JsonProperty("notify")
    Boolean isNotify,
    Boolean predictionStopped,
    Source source,
    Restrictions restrictions,
    Boolean revoked,
    Instant revokedTimestamp,
    LocalDate date,

    /**
     * What's this?
     */
    JsonNode revoke


) implements AssertValidatable {
    @Override
    public void assertValid() {

    }
}
