package nl.vpro.domain.npo.wonvpp;

import java.time.Instant;

public record AvailabilityPeriodType(
    String transmissionId,
    String onDemandTransmissionId,
    String publicationId,
    GeoIpRestriction geoIpRestriction,
    boolean drm,
    Instant start,
    Instant stop) {
}
