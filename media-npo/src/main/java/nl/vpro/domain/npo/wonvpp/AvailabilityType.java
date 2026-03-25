package nl.vpro.domain.npo.wonvpp;

import java.util.List;

public record AvailabilityType(
    PlatformEnum platform,
    List<AvailabilityPeriodType> availabilityPeriods

) {
}
