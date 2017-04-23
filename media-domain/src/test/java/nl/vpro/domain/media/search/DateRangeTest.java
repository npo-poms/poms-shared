package nl.vpro.domain.media.search;

import java.io.IOException;
import java.time.Instant;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import nl.vpro.jackson2.DateModule;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class DateRangeTest {

    @Test
    public void testTest1() {
        DateRange range = new DateRange(Instant.ofEpochMilli(100), Instant.ofEpochMilli(200));
        assertThat(range.test(Instant.ofEpochMilli(99))).isFalse();
        assertThat(range.test(Instant.ofEpochMilli(100))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(150))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(200))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(201))).isFalse();

    }

    @Test
    public void testTest11() {
        DateRange range = new DateRange(DateRange.Value.of(Instant.ofEpochMilli(100)),  DateRange.Value.builder().value(Instant.ofEpochMilli(200)).inclusive(false).build());
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
        assertThat(range.test(Instant.ofEpochMilli(200))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(201))).isFalse();

    }

    @Test
    public void testTest21() {
        DateRange range = new DateRange(null, DateRange.Value.builder().value(Instant.ofEpochMilli(200)).inclusive(false).build());
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
    
    @Test
    public void unmarshallBackwards() throws IOException {
        DateRange range = Jackson2Mapper.getInstance().readValue("{\n" +
            "  \"start\" : 100,\n" +
            "  \"stop\" : 200\n" +
            "}", DateRange.class);

        assertThat(range.getStart().getValue().toEpochMilli()).isEqualTo(100L);
        assertThat(range.getStop().getValue().toEpochMilli()).isEqualTo(200L);
    }

    @Test
    public void json() throws Exception {
        Jackson2TestUtil.roundTripAndSimilar(
            new DateRange(
                DateRange.Value.builder().value(Instant.ofEpochMilli(100)).build(), 
                DateRange.Value.builder().value(Instant.ofEpochMilli(200)).inclusive(false).build()), "{\n" +
                "  \"start\" : {\n" +
                "    \"value\" : 100\n" +
                "  },\n" +
                "  \"stop\" : {\n" +
                "  \"inclusive\" :false,\n" +
                "    \"value\" : 200\n" +
                "  }\n" +
                "}");


    }

    @Test
    public void jsonString() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        mapper.registerModule(javaTimeModule);
        mapper.registerModule(new DateModule());

        String example = "{\n" +
            "  \"start\" :\"2016-08-10T22:00:00.000Z\",\n" +
            "  \"stop\" : 200\n" +
            "}";
        DateRange r = mapper.readerFor(DateRange.class).readValue(example);
        assertThat(r.getStart().get().toEpochMilli()).isEqualTo(1470866400000L);

    }


    @Test
    public void xml() throws Exception {
        JAXBTestUtil.roundTripAndSimilar(new DateRange(Instant.ofEpochMilli(100), Instant.ofEpochMilli(200)), "<local:dateRange xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:s=\"urn:vpro:media:search:2012\" xmlns:update=\"urn:vpro:media:update:2009\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:local=\"uri:local\">\n" +
            "    <s:start>1970-01-01T01:00:00.100+01:00</s:start>\n" +
            "    <s:stop>1970-01-01T01:00:00.200+01:00</s:stop>\n" +
            "</local:dateRange>");

    }

}
