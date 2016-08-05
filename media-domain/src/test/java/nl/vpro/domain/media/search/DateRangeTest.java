package nl.vpro.domain.media.search;

import java.time.Instant;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DateRangeTest {

    @Test
    public void testTest1() {
        DateRange range = new DateRange(Instant.ofEpochMilli(100), Instant.ofEpochMilli(200));
        assertThat(range.test(Instant.ofEpochMilli(99))).isFalse();
        assertThat(range.test(Instant.ofEpochMilli(100))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(150))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(200))).isFalse();
        assertThat(range.test(Instant.ofEpochMilli(201))).isFalse();

    }

    @Test
    public void testTest2() {
        DateRange range = new DateRange(null, Instant.ofEpochMilli(200));
        assertThat(range.test(Instant.ofEpochMilli(99))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(100))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(150))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(200))).isFalse();
        assertThat(range.test(Instant.ofEpochMilli(201))).isFalse();

    }

    @Test
    public void testTest3() {
        DateRange range = new DateRange(Instant.ofEpochMilli(100), null);
        assertThat(range.test(Instant.ofEpochMilli(99))).isFalse();
        assertThat(range.test(Instant.ofEpochMilli(100))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(150))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(200))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(201))).isTrue();

    }

}
