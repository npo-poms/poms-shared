package nl.vpro.berlijn.domain.availability;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Range;

import static java.time.Instant.EPOCH;
import static java.time.Instant.ofEpochMilli;
import static nl.vpro.berlijn.domain.availability.AvailabilityPeriod.ALWAYS;
import static nl.vpro.berlijn.domain.availability.GeoIp.EU;
import static nl.vpro.berlijn.domain.availability.GeoIp.NL;
import static org.meeuw.assertj.Assertions.assertThat;

class AvailabilityPeriodTest {

    @Test
    void spanOfEmpty() {
        Range<Instant> span = AvailabilityPeriod.span(null, List.of());
        assertThat(span).isEqualTo(ALWAYS);
    }

    @Test
    void spanOfNull() {
        Range<Instant> span = AvailabilityPeriod.span(null, null);
        assertThat(span).isEqualTo(ALWAYS);
    }


    @Test
    void spanOf1WithNull() {
        Range<Instant> span = AvailabilityPeriod.span(null, List.of(AvailabilityPeriod.builder().build()));
        assertThat(span).isEqualTo(ALWAYS);
    }

    @Test
    void spanOf1WithSTart() {
        Range<Instant> span = AvailabilityPeriod.span(null, List.of(
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(0))
                .build()));
        assertThat(span).isEqualTo(Range.atLeast(Instant.EPOCH));
    }


    @Test
    void spanOf2WithSTart() {
        Range<Instant> span = AvailabilityPeriod.span(null, List.of(
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(0))
                .build(),
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(100))
                .build()
            )
        );
        assertThat(span).isEqualTo(Range.atLeast(Instant.EPOCH));
    }

    @Test
    void spanOf1MatchingWithSTart() {
        Range<Instant> span = AvailabilityPeriod.span(null, List.of(
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(0))
                .geoIpRestriction(EU)
                .build(),
            AvailabilityPeriod.builder()
                .start(ofEpochMilli(100))
                .build()
            )
        );
        assertThat(span).isEqualTo(Range.atLeast(Instant.EPOCH.plusMillis(100)));
    }

     @Test
    void spanOf2Distinct() {
        Range<Instant> span = AvailabilityPeriod.span(NL, List.of(
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
        assertThat(span).isEqualTo(Range.closedOpen(EPOCH, Instant.ofEpochMilli(300)));
    }
}
