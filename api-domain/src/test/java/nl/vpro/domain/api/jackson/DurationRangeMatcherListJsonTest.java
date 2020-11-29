package nl.vpro.domain.api.jackson;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.Duration;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.DurationRangeMatcherList;
import nl.vpro.domain.api.Match;
import nl.vpro.domain.api.media.DurationRangeMatcher;
import nl.vpro.jackson2.Jackson2Mapper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class DurationRangeMatcherListJsonTest {

    @Test
    public void testGetValueJson() throws Exception {
        DurationRangeMatcher in = new DurationRangeMatcher(Duration.ZERO, Duration.ofHours(1), false, Match.NOT);
        DurationRangeMatcherList list = new DurationRangeMatcherList(in);

        StringWriter writer = new StringWriter();
        Jackson2Mapper.INSTANCE.writeValue(writer, list);

        assertThat(writer.toString()).isEqualTo("[{\"begin\":0,\"end\":3600000,\"inclusiveEnd\":false,\"match\":\"NOT\"}]");

        DurationRangeMatcherList out = Jackson2Mapper.INSTANCE.readValue(new StringReader(writer.toString()), DurationRangeMatcherList.class);
        assertThat(out.asList()).containsExactly(new DurationRangeMatcher(Duration.ZERO, Duration.ofHours(1), false, Match.NOT));
        assertThat(out.getMatch()).isEqualTo(Match.MUST);
    }

    @Test
    public void testGetValueFromJson() throws Exception {
        DurationRangeMatcherList matcher = Jackson2Mapper.INSTANCE.readValue("[{\"begin\":0,\"end\":3600000,\"match\":\"NOT\",\"inclusiveEnd\":false}]", DurationRangeMatcherList.class);

        assertThat(matcher.size()).isEqualTo(1);
        assertThat(matcher.iterator().next()).isEqualTo(new DurationRangeMatcher(Duration.ZERO,  Duration.ofHours(1), false, Match.NOT));
    }


    @Test
    public void testGetValueFromJsonSingular() throws Exception {
        DurationRangeMatcherList matcher = Jackson2Mapper.INSTANCE.readValue("{\"begin\":0,\"end\":3600000,\"match\":\"NOT\",\"inclusiveEnd\":false}", DurationRangeMatcherList.class);

        assertThat(matcher.size()).isEqualTo(1);
        assertThat(matcher.iterator().next()).isEqualTo(new DurationRangeMatcher(Duration.ZERO, Duration.ofHours(1), false, Match.NOT));

    }

    @Test
    public void testGetValueJsonInverse() throws Exception {
        DurationRangeMatcher in = new DurationRangeMatcher(Duration.ZERO, Duration.ofHours(1), false, Match.NOT);
        DurationRangeMatcherList list = new DurationRangeMatcherList(Collections.singletonList(in), Match.NOT);

        StringWriter writer = new StringWriter();
        Jackson2Mapper.INSTANCE.writeValue(writer, list);

        assertThat(writer.toString()).isEqualTo("{\"value\":[{\"begin\":0,\"end\":3600000,\"inclusiveEnd\":false,\"match\":\"NOT\"}],\"match\":\"NOT\"}");

        DurationRangeMatcherList out = Jackson2Mapper.INSTANCE.readValue(new StringReader(writer.toString()), DurationRangeMatcherList.class);
        assertThat(out.asList()).containsExactly(new DurationRangeMatcher(Duration.ZERO, Duration.ofHours(1), false, Match.NOT));
        assertThat(out.getMatch()).isEqualTo(Match.NOT);
    }

    @Test
    public void testGetValueFromJsonInverse() throws Exception {
        DurationRangeMatcherList matcher = Jackson2Mapper.INSTANCE.readValue("{\"value\":[{\"begin\":0,\"end\":3600000,\"match\":\"NOT\",\"inclusiveEnd\":false}],\"match\":\"not\"}", DurationRangeMatcherList.class);

        assertThat(matcher.size()).isEqualTo(1);
        assertThat(matcher.iterator().next()).isEqualTo(new DurationRangeMatcher(Duration.ZERO, Duration.ofHours(1), false, Match.NOT));
    }

}
