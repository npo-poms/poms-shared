package nl.npo.wonvpp.domain;

import java.util.List;

public record AvailabilityType(
    PlatformEnum platform,
    List<AvailabilityPeriodType> availabilityPeriods

) {
}
