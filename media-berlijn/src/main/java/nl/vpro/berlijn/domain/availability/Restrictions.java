package nl.vpro.berlijn.domain.availability;

import java.util.List;

@lombok.Builder
public record Restrictions(
     List<GeoIp> geoip,
     List<Encryption> encryption,
     List<AvailabilityPeriod> availabilityPeriods
) {
}
