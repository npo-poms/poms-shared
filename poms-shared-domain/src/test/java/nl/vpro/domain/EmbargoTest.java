package nl.vpro.domain;

import lombok.extern.slf4j.Slf4j;

import java.time.*;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Range;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 5.6.1
 */
@Slf4j
public class EmbargoTest {

    @Test
    public void test() {
        Instant start = LocalDateTime.of(2018, 3, 5, 11, 55).atZone(ZoneId.of("Europe/Amsterdam")).toInstant();
        Instant stop = LocalDateTime.of(2018, 10, 20, 11, 55).atZone(ZoneId.of("Europe/Amsterdam")).toInstant();
        Instant between = LocalDateTime.of(2018, 4, 6, 11, 55).atZone(ZoneId.of("Europe/Amsterdam")).toInstant();

        Embargo embargo = new BasicEmbargo(start, stop);

        assertThat(embargo.asRange().contains(between)).isTrue();
        assertThat(embargo.asRange().contains(start)).isTrue();
        assertThat(embargo.asRange().contains(stop)).isFalse();

        assertThat(embargo.inPublicationWindow(between)).isTrue();
        assertThat(embargo.inPublicationWindow(start)).isTrue();
        assertThat(embargo.inPublicationWindow(stop)).isFalse();

    }

    @Test
    public void testRange() {
        assertRange(null, null, Range.all());

        assertRange(null, Instant.ofEpochMilli(100), Range.lessThan(Instant.ofEpochMilli(100)));

        assertRange(Instant.ofEpochMilli(100), null, Range.atLeast(Instant.ofEpochMilli(100)));

        assertRange(Instant.ofEpochMilli(100), Instant.ofEpochMilli(200), Range.closedOpen(Instant.ofEpochMilli(100),Instant.ofEpochMilli(200)));
    }
    public void assertRange(@Nullable Instant start, @Nullable Instant stop, Range<Instant> range) {
        Embargo impl = new BasicEmbargo(start, stop);
        assertThat(impl.asRange()).isEqualTo(range);
        if (start != null) {
            assertThat(impl.inPublicationWindow(start)).isTrue();
            assertThat(impl.isUnderEmbargo(start)).isFalse();
            assertThat(impl.asRange().contains(start)).isTrue();
            assertThat(impl.inPublicationWindow(start.minusMillis(1))).isFalse();
            assertThat(impl.asRange().contains(start.minusMillis(1))).isFalse();
            assertThat(impl.isUnderEmbargo(start)).isFalse();
            assertThat(impl.isUnderEmbargo(start.minusMillis(1))).isTrue();

            assertThat(impl.wasUnderEmbargo(start)).isFalse();
            assertThat(impl.wasUnderEmbargo(start.plusMillis(1))).isFalse();
            assertThat(impl.wasUnderEmbargo(start.minusMillis(1))).isFalse();

            assertThat(impl.willBePublished(start.minusMillis(1))).isTrue();
            assertThat(impl.willBePublished(start)).isFalse();
            assertThat(impl.willBePublished(start.plusMillis(1))).isFalse();

        }
        if (stop != null) {
            assertThat(impl.inPublicationWindow(stop)).isFalse();
            assertThat(impl.isUnderEmbargo(stop)).isTrue();
            assertThat(impl.asRange().contains(stop)).isFalse();
            assertThat(impl.inPublicationWindow(stop.minusMillis(1))).isTrue();
            assertThat(impl.asRange().contains(stop.minusMillis(1))).isTrue();
            assertThat(impl.isUnderEmbargo(stop)).isTrue();
            assertThat(impl.isUnderEmbargo(stop.minusMillis(1))).isFalse();

            assertThat(impl.wasUnderEmbargo(stop)).isTrue();
            assertThat(impl.wasUnderEmbargo(stop.plusMillis(1))).isTrue();
            assertThat(impl.wasUnderEmbargo(stop.minusMillis(1))).isFalse();

            assertThat(impl.willBeUnderEmbargo(stop)).isFalse();
            assertThat(impl.willBeUnderEmbargo(stop.plusMillis(1))).isFalse();
            assertThat(impl.willBeUnderEmbargo(stop.minusMillis(1))).isTrue();
        }
        log.info("{} / {} ", impl, range.toString());
    }

}
