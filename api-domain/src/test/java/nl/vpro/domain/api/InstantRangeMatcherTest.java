/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.Schedule;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class InstantRangeMatcherTest extends RangeMatcherTest<Instant, DateRangeMatcher> {

    @Test
    public void testGetInclusiveEnd() {
        DateRangeMatcher in = new DateRangeMatcher(null, null, true);
        DateRangeMatcher out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:dateRangeMatcher xmlns:local="uri:local" inclusiveEnd="true" xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009"/>
                """);
        assertThat(out.includeEnd()).isTrue();
    }

    @Override
    DateRangeMatcher getInstance() {
        return new DateRangeMatcher(Instant.ofEpochMilli(100), Instant.ofEpochMilli(200));
    }

    @Override
    Instant getValue() {
        return Instant.ofEpochMilli(150);
    }

    @Override
    @Test
    public void testHashCode() {
        assertEquals(796275456, getInstance().hashCode());
    }

    @Test
    public void testGetBeginXml() {
        Instant begin = Instant.EPOCH;
        DateRangeMatcher in = new DateRangeMatcher(begin, null);
        DateRangeMatcher out = JAXBTestUtil.roundTripContains(in,
            "<api:begin xmlns:api=\"urn:vpro:api:2013\">1970-01-01T01:00:00+01:00</api:begin>");
        assertThat(out.getBegin()).isEqualTo(begin);
    }

    @Test
    public void testGetEndXml() {
        Instant end = Instant.EPOCH;
        DateRangeMatcher in = new DateRangeMatcher(null, end);
        DateRangeMatcher out = JAXBTestUtil.roundTripContains(in,
            "<api:end xmlns:api=\"urn:vpro:api:2013\">1970-01-01T01:00:00+01:00</api:end>");
        assertThat(out.getEnd()).isEqualTo(end);
    }


    @Test
    public void json() {
        DateRangeMatcher rangeMatcher = DateRangeMatcher.builder()
            .begin(LocalDateTime.of(2017, 6, 24, 18, 0).atZone(Schedule.ZONE_ID).toInstant())
            .end(LocalDateTime.of(2017, 7, 24, 18, 0).atZone(Schedule.ZONE_ID).toInstant())
            .build();
        Jackson2TestUtil.roundTripAndSimilar(rangeMatcher, """
            {
              "begin" : 1498320000000,
              "end" : 1500912000000
            }""");

    }

    @Test
    public void jsonNatty() throws Exception {
        Instant now = Instant.now();
        DateRangeMatcher in = Jackson2Mapper.getInstance().readValue(new StringReader("""
            {
              "begin" : "now"
            }"""), DateRangeMatcher.class);
        assertThat(in.getBegin()).isCloseTo(now, within(10, ChronoUnit.SECONDS));

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
