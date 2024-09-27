package nl.vpro.berlijn.domain.availability;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Range;

import nl.vpro.util.Ranges;

import static nl.vpro.berlijn.domain.availability.GeoIp.ofNullable;

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

    public static final Range<Instant> ALWAYS = Ranges.closedOpen(null, null);

    public Range<Instant> range() {
        return Ranges.closedOpen(start(), stop());
    }


    public String guci() {
        return transmissionId;
    }

    public static Range<Instant> span(GeoIp geoIp, Collection<AvailabilityPeriod> periods) {

        final var effectiveGeoIp  = ofNullable(geoIp);
        return Optional.ofNullable(Optional.ofNullable(periods).stream()
            .flatMap(Collection::stream)
            .filter(ap -> ofNullable(ap.geoIpRestriction()) == effectiveGeoIp)
            .map(AvailabilityPeriod::range)
            .reduce(null, (r1, r2) -> r1 == null ? r2 : r1.span(r2))
            ).orElse(ALWAYS);
    }
}
