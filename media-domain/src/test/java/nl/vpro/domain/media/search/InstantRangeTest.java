package nl.vpro.domain.media.search;

import java.io.IOException;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import nl.vpro.jackson2.DateModule;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class InstantRangeTest {

    @Test
    public void testTest1() {
        InstantRange range = new InstantRange(Instant.ofEpochMilli(100), Instant.ofEpochMilli(200));
        assertThat(range.test(Instant.ofEpochMilli(99))).isFalse();
        assertThat(range.test(Instant.ofEpochMilli(100))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(150))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(199))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(200))).isFalse();
        assertThat(range.test(Instant.ofEpochMilli(201))).isFalse();
    }

    @Test
    public void testTest11() {
        InstantRange range = new InstantRange(InstantRange.Value.of(Instant.ofEpochMilli(100)),  InstantRange.Value.builder().value(Instant.ofEpochMilli(200)).inclusive(false).build());
        assertThat(range.test(Instant.ofEpochMilli(99))).isFalse();
        assertThat(range.test(Instant.ofEpochMilli(100))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(150))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(200))).isFalse();
        assertThat(range.test(Instant.ofEpochMilli(201))).isFalse();
    }

    @Test
    public void testTest2() {
        InstantRange range = new InstantRange(null, Instant.ofEpochMilli(200));
        assertThat(range.test(Instant.ofEpochMilli(99))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(100))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(150))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(199))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(200))).isFalse();
        assertThat(range.test(Instant.ofEpochMilli(201))).isFalse();
    }

    @Test
    public void testTest21() {
        InstantRange range = new InstantRange(null, InstantRange.Value.builder().value(Instant.ofEpochMilli(200)).inclusive(false).build());
        assertThat(range.test(Instant.ofEpochMilli(99))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(100))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(150))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(200))).isFalse();
        assertThat(range.test(Instant.ofEpochMilli(201))).isFalse();
    }

    @Test
    public void testTest3() {
        InstantRange range = new InstantRange(Instant.ofEpochMilli(100), null);
        assertThat(range.test(Instant.ofEpochMilli(99))).isFalse();
        assertThat(range.test(Instant.ofEpochMilli(100))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(150))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(200))).isTrue();
        assertThat(range.test(Instant.ofEpochMilli(201))).isTrue();
    }

    @Test
    public void unmarshallBackwards() throws IOException {
        InstantRange range = Jackson2Mapper.getInstance().readValue("""
            {
              "start" : 100,
              "stop" : 200
            }""", InstantRange.class);

        assertThat(range.getStart().getValue().toEpochMilli()).isEqualTo(100L);
        assertThat(range.getStop().getValue().toEpochMilli()).isEqualTo(200L);
    }

    @Test
    public void json() {
        Jackson2TestUtil.roundTripAndSimilar(
            new InstantRange(
                InstantRange.Value.builder().value(Instant.ofEpochMilli(100)).build(),
                InstantRange.Value.builder().value(Instant.ofEpochMilli(200)).inclusive(false).build()), """
                {
                  "start" : {
                    "value" : 100
                  },
                  "stop" : {
                  "inclusive" :false,
                    "value" : 200
                  }
                }""");


    }

    @Test
    public void jsonString() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        mapper.registerModule(javaTimeModule);
        mapper.registerModule(new DateModule());

        String example = """
            {
              "start" :"2016-08-10T22:00:00.000Z",
              "stop" : 200
            }""";
        InstantRange r = mapper.readerFor(InstantRange.class).readValue(example);
        assertThat(r.getStart().get().toEpochMilli()).isEqualTo(1470866400000L);
    }


    @Test
    public void xml() {
        JAXBTestUtil.roundTripAndSimilar(
            new InstantRange(Instant.ofEpochMilli(100), Instant.ofEpochMilli(200)), """
                <local:instantRange xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:s="urn:vpro:media:search:2012" xmlns:update="urn:vpro:media:update:2009" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:local="uri:local">
                    <s:start>1970-01-01T01:00:00.100+01:00</s:start>
                    <s:stop inclusive="false">1970-01-01T01:00:00.200+01:00</s:stop>
                </local:instantRange>""");
    }

}
