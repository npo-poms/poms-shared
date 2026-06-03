package nl.vpro.berlijn.domain.availability;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Range;

import static java.time.Instant.EPOCH;
import static java.time.Instant.ofEpochMilli;
import static nl.vpro.berlijn.domain.availability.AvailabilityPeriod.ALWAYS;
import static nl.vpro.berlijn.domain.availability.GeoIp.*;
import static org.assertj.core.api.Assertions.assertThat;

class AvailabilityPeriodTest {

    @Test
    void spanOfEmpty() {
        Optional<Range<Instant>> span = AvailabilityPeriod.span(null, List.of());
        assertThat(span).isEmpty();
    }

    @Test
    void spanOfNull() {
        Optional<Range<Instant>> span = AvailabilityPeriod.span(null, null);
        assertThat(span).isEmpty();
    }


    @Test
    void spanOf1WithNull() {
        Optional<Range<Instant>> span = AvailabilityPeriod.span(null, List.of(AvailabilityPeriod.builder().build()));
        assertThat(span).contains(ALWAYS);
    }

    @Test
    void spanOf1WithSTart() {
        Optional<Range<Instant>> span = AvailabilityPeriod.span(null, List.of(
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(0))
                .build()));
        assertThat(span).contains(Range.atLeast(Instant.EPOCH));
    }


    @Test
    void spanOf2WithSTart() {
        Optional<Range<Instant>> span = AvailabilityPeriod.span(null, List.of(
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(0))
                .build(),
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(100))
                .build()
            )
        );
        assertThat(span).contains(Range.atLeast(Instant.EPOCH));
    }

    @Test
    void spanOf1MatchingWithSTart() {
        Optional<Range<Instant>> span = AvailabilityPeriod.span(null, List.of(
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(0))
                .geoIpRestriction(EU)
                .build(),
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(100))
                .build()
            )
        );
        assertThat(span).contains(Range.atLeast(Instant.EPOCH.plusMillis(100)));
    }

     @Test
    void spanOf2Distinct() {
        Optional<Range<Instant>> span = AvailabilityPeriod.span(NL, List.of(
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(0))
                .stop(ofEpochMilli(100))
                .geoIpRestriction(NL)
                .build(),
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(200))
                .stop(ofEpochMilli(300))
                .geoIpRestriction(NL)
                .build(),
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(200))
                .stop(ofEpochMilli(1000))
                .geoIpRestriction(EU)
                .build()
            )
        );
        assertThat(span).contains(Range.closedOpen(EPOCH, Instant.ofEpochMilli(300)));
    }

    @Test
    void noMatch() {
        Optional<Range<Instant>> span = AvailabilityPeriod.span(Europa, List.of(
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(0))
                .stop(ofEpochMilli(100))
                .geoIpRestriction(NL)
                .build(),
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(200))
                .stop(ofEpochMilli(300))
                .geoIpRestriction(NL)
                .build(),
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(200))
                .stop(ofEpochMilli(1000))
                .geoIpRestriction(EU)
                .build()
            )
        );
        assertThat(span).isEmpty();
    }
}
