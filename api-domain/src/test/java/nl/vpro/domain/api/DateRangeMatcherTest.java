/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.time.Instant;
import java.util.Date;

import org.junit.Test;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class DateRangeMatcherTest extends RangeMatcherTest<Instant, DateRangeMatcher> {

    @Test
    public void testGetInclusiveEnd() throws Exception {
        DateRangeMatcher in = new DateRangeMatcher((Date) null, null, true);
        DateRangeMatcher out = JAXBTestUtil.roundTrip(in,
            "inclusiveEnd=\"true\"");
        assertThat(out.includeEnd()).isTrue();
    }

    @Override
    DateRangeMatcher getInstance() {
        return new DateRangeMatcher(new Date(100), new Date(200));
    }

    @Override
    Instant getValue() {
        return Instant.ofEpochMilli(150);
    }

    @Override
    public void testHashCode() {
        assertEquals(796275456, getInstance().hashCode());
    }

    @Test
    public void testGetBeginXml() throws Exception {
        Instant begin = Instant.EPOCH;
        DateRangeMatcher in = new DateRangeMatcher(begin, null);
        DateRangeMatcher out = JAXBTestUtil.roundTrip(in,
            "<api:begin>1970-01-01T01:00:00+01:00</api:begin>");
        assertThat(out.getBegin()).isEqualTo(begin);
    }

    @Test
    public void testGetEndXml() throws Exception {
        Instant end = Instant.EPOCH;
        DateRangeMatcher in = new DateRangeMatcher(null, end);
        DateRangeMatcher out = JAXBTestUtil.roundTrip(in,
            "<api:end>1970-01-01T01:00:00+01:00</api:end>");
        assertThat(out.getEnd()).isEqualTo(end);
    }

    @Test
    public void testApply() {
        DateRangeMatcher instance = getInstance();
        assertTrue(instance.test(Instant.ofEpochMilli(100)));
        assertTrue(instance.test(Instant.ofEpochMilli(150)));
        assertFalse(instance.test(Instant.ofEpochMilli(200)));
        assertFalse(instance.test(Instant.ofEpochMilli(400)));
        assertFalse(instance.test(Instant.ofEpochMilli(-1)));
    }

}
