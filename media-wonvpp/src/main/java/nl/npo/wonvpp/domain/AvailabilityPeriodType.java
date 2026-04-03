package nl.npo.wonvpp.domain;

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
