package nl.vpro.wonvpp.domain;

import java.util.List;

public record AvailabilityType(
    PlatformEnum platform,
    List<AvailabilityPeriodType> availabilityPeriods

) {
}
