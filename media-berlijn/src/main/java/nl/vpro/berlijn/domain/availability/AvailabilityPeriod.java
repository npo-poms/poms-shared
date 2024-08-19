package nl.vpro.berlijn.domain.availability;

import java.time.Instant;
import java.util.List;

public record AvailabilityPeriod(
    List<GeoIp> geoip,

    Encryption[] encryption,

    Instant stop,
    Instant start,


    //  suddenly new August 2024
    GeoIp geoIpRestriction,
    Boolean drm) {
}
