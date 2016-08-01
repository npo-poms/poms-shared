package nl.vpro.domain.media.search;

import java.util.Date;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DateRangeTest {

    @Test
    public void testTest1() {
        DateRange range = new DateRange(new Date(100), new Date(200));
        assertThat(range.test(new Date(99))).isFalse();
        assertThat(range.test(new Date(100))).isTrue();
        assertThat(range.test(new Date(150))).isTrue();
        assertThat(range.test(new Date(200))).isFalse();
        assertThat(range.test(new Date(201))).isFalse();

    }

    @Test
    public void testTest2() {
        DateRange range = new DateRange(null, new Date(200));
        assertThat(range.test(new Date(99))).isTrue();
        assertThat(range.test(new Date(100))).isTrue();
        assertThat(range.test(new Date(150))).isTrue();
        assertThat(range.test(new Date(200))).isFalse();
        assertThat(range.test(new Date(201))).isFalse();

    }

    @Test
    public void testTest3() {
        DateRange range = new DateRange(new Date(100), null);
        assertThat(range.test(new Date(99))).isFalse();
        assertThat(range.test(new Date(100))).isTrue();
        assertThat(range.test(new Date(150))).isTrue();
        assertThat(range.test(new Date(200))).isTrue();
        assertThat(range.test(new Date(201))).isTrue();

    }

}
