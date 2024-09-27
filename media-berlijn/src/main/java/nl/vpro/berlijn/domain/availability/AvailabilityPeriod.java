package nl.vpro.berlijn.domain.availability;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Range;

import nl.vpro.util.Ranges;

/**
 * @param transmissionId guci
 */
@JsonIgnoreProperties({
    // used to exist?
    "geoip",
    "encryption"
})
@lombok.Builder
public record AvailabilityPeriod(
    String transmissionId,
    String onDemandTransmissionId,
    String publicationId,
    GeoIp geoIpRestriction,
    Boolean drm,
    Instant start,
    Instant stop,
    Boolean isStreamable
    ) {

    public Range<Instant> range() {
        return Ranges.closedOpen(start(), stop());
    }


    public String guci() {
        return transmissionId;
    }
}
