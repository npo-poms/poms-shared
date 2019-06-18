package nl.vpro.domain.api.jackson;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import org.junit.Test;

import nl.vpro.domain.api.DateRangeMatcher;
import nl.vpro.domain.api.DateRangeMatcherList;
import nl.vpro.domain.api.Match;
import nl.vpro.jackson2.Jackson2Mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
public class InstantRangeMatcherListJsonTest {

    @Test
    public void testGetValueJson() throws Exception {
        DateRangeMatcher in = new DateRangeMatcher(Instant.EPOCH, Instant.ofEpochMilli(3600000), false,  Match.NOT);
        DateRangeMatcherList list = new DateRangeMatcherList(in);

        StringWriter writer = new StringWriter();
        Jackson2Mapper.INSTANCE.writeValue(writer, list);

        assertThat(writer.toString()).isEqualTo("[{\"begin\":0,\"end\":3600000,\"inclusiveEnd\":false,\"match\":\"NOT\"}]");

        DateRangeMatcherList out = Jackson2Mapper.INSTANCE.readValue(new StringReader(writer.toString()), DateRangeMatcherList.class);
        assertThat(out.asList()).containsExactly(new DateRangeMatcher(Instant.EPOCH, Instant.ofEpochMilli(3600000), false, Match.NOT));
        assertThat(out.getMatch()).isEqualTo(Match.MUST);
    }

    @Test
    public void testGetValueFromJson() throws Exception {
        DateRangeMatcherList matcher = Jackson2Mapper.INSTANCE.readValue("[{\"begin\":0,\"end\":3600000,\"match\":\"NOT\",\"inclusiveEnd\":false}]", DateRangeMatcherList.class);

        assertThat(matcher.size()).isEqualTo(1);
        assertThat(matcher.iterator().next()).isEqualTo(new DateRangeMatcher(
            Instant.EPOCH, Instant.ofEpochMilli(3600000), false, Match.NOT));
    }


    @Test
    public void testGetValueFromJsonNatty() throws Exception {
        DateRangeMatcherList matcher = Jackson2Mapper.INSTANCE.readValue("[{\"begin\":0,\"end\":\"now\",\"match\":\"NOT\",\"inclusiveEnd\":false}]", DateRangeMatcherList.class);

        assertThat(matcher.size()).isEqualTo(1);

        assertThat(matcher.iterator().next().getEnd()).isCloseTo(Instant.now(), within(10, ChronoUnit.SECONDS));
    }


    @Test
    public void testGetValueFromJsonSingular() throws Exception {
        DateRangeMatcherList matcher = Jackson2Mapper.INSTANCE.readValue("{\"begin\":0,\"end\":3600000,\"match\":\"NOT\",\"inclusiveEnd\":false}", DateRangeMatcherList.class);

        assertThat(matcher.size()).isEqualTo(1);
        assertThat(matcher.iterator().next()).isEqualTo(new DateRangeMatcher(
            Instant.EPOCH, Instant.ofEpochMilli(3600000), false, Match.NOT));
    }

    @Test
    public void testGetValueJsonInverse() throws Exception {
        DateRangeMatcher in = new DateRangeMatcher(Instant.EPOCH, Instant.ofEpochMilli(3600000), false, Match.NOT);
        DateRangeMatcherList list = new DateRangeMatcherList(Collections.singletonList(in), Match.NOT);

        StringWriter writer = new StringWriter();
        Jackson2Mapper.INSTANCE.writeValue(writer, list);

        assertThat(writer.toString()).isEqualTo("{\"value\":[{\"begin\":0,\"end\":3600000,\"inclusiveEnd\":false,\"match\":\"NOT\"}],\"match\":\"NOT\"}");

        DateRangeMatcherList out = Jackson2Mapper.INSTANCE.readValue(new StringReader(writer.toString()), DateRangeMatcherList.class);
        assertThat(out.asList()).containsExactly(new DateRangeMatcher(Instant.EPOCH, Instant.ofEpochMilli(3600000), false, Match.NOT));
        assertThat(out.getMatch()).isEqualTo(Match.NOT);
    }

    @Test
    public void testGetValueFromJsonInverse() throws Exception {
        DateRangeMatcherList matcher = Jackson2Mapper.INSTANCE.readValue("{\"value\":[{\"begin\":0,\"end\":3600000,\"match\":\"NOT\",\"inclusiveEnd\":false}],\"match\":\"not\"}", DateRangeMatcherList.class);

        assertThat(matcher.size()).isEqualTo(1);
        assertThat(matcher.iterator().next()).isEqualTo(new DateRangeMatcher(Instant.EPOCH, Instant.ofEpochMilli(3600000), false, Match.NOT));
    }
}
