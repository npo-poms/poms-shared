package nl.vpro.domain.api.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import lombok.extern.log4j.Log4j2;
import nl.vpro.domain.api.*;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import org.junit.jupiter.api.Test;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
@Log4j2
public class InstantRangeMatcherListJsonTest {

    @Test
    public void testGetValueJson() throws Exception {
        DateRangeMatcher in = new DateRangeMatcher(Instant.EPOCH, Instant.ofEpochMilli(3600000), false,  Match.NOT);
        DateRangeMatcherList list = new DateRangeMatcherList(in);

        StringWriter writer = new StringWriter();
        Jackson2Mapper.getInstance().writeValue(writer, list);

        assertThat(writer.toString()).isEqualTo("[{\"begin\":0,\"end\":3600000,\"inclusiveEnd\":false,\"match\":\"NOT\"}]");

        DateRangeMatcherList out = Jackson2Mapper.getInstance().readValue(new StringReader(writer.toString()), DateRangeMatcherList.class);
        assertThat(out.asList()).containsExactly(new DateRangeMatcher(Instant.EPOCH, Instant.ofEpochMilli(3600000), false, Match.NOT));
        assertThat(out.getMatch()).isEqualTo(Match.MUST);
    }

    @Test
    public void testGetValueFromJson() throws Exception {
        DateRangeMatcherList matcher = Jackson2Mapper.getInstance().readValue("[{\"begin\":0,\"end\":3600000,\"match\":\"NOT\",\"inclusiveEnd\":false}]", DateRangeMatcherList.class);

        assertThat(matcher.size()).isEqualTo(1);
        assertThat(matcher.iterator().next()).isEqualTo(new DateRangeMatcher(
            Instant.EPOCH, Instant.ofEpochMilli(3600000), false, Match.NOT));
    }


    @Test
    public void testGetValueFromJsonNatty() throws Exception {
        DateRangeMatcherList matcher = Jackson2Mapper.getInstance().readValue("[{\"begin\":0,\"end\":\"now\",\"match\":\"NOT\",\"inclusiveEnd\":false}]", DateRangeMatcherList.class);

        assertThat(matcher.size()).isEqualTo(1);

        assertThat(matcher.iterator().next().getEnd()).isCloseTo(Instant.now(), within(10, ChronoUnit.SECONDS));
    }


    @Test
    public void testGetValueFromJsonSingular() throws Exception {
        DateRangeMatcherList matcher = Jackson2Mapper.getInstance().readValue("{\"begin\":0,\"end\":3600000,\"match\":\"NOT\",\"inclusiveEnd\":false}", DateRangeMatcherList.class);

        assertThat(matcher.size()).isEqualTo(1);
        assertThat(matcher.iterator().next()).isEqualTo(new DateRangeMatcher(
            Instant.EPOCH, Instant.ofEpochMilli(3600000), false, Match.NOT));
    }

    @Test
    public void testGetValueJsonInverse() throws Exception {
        DateRangeMatcher in = new DateRangeMatcher(Instant.EPOCH, Instant.ofEpochMilli(3600000), false, Match.NOT);
        DateRangeMatcherList list = new DateRangeMatcherList(Collections.singletonList(in), Match.NOT);

        StringWriter writer = new StringWriter();
        Jackson2Mapper.getInstance().writeValue(writer, list);

        assertThat(writer.toString()).isEqualTo("{\"value\":[{\"begin\":0,\"end\":3600000,\"inclusiveEnd\":false,\"match\":\"NOT\"}],\"match\":\"NOT\"}");

        DateRangeMatcherList out = Jackson2Mapper.getInstance().readValue(new StringReader(writer.toString()), DateRangeMatcherList.class);
        assertThat(out.asList()).containsExactly(new DateRangeMatcher(Instant.EPOCH, Instant.ofEpochMilli(3600000), false, Match.NOT));
        assertThat(out.getMatch()).isEqualTo(Match.NOT);
    }

    @Test
    public void testGetValueFromJsonInverse() throws Exception {
        DateRangeMatcherList matcher = Jackson2Mapper.getInstance().readValue("{\"value\":[{\"begin\":0,\"end\":3600000,\"match\":\"NOT\",\"inclusiveEnd\":false}],\"match\":\"not\"}", DateRangeMatcherList.class);

        assertThat(matcher.size()).isEqualTo(1);
        assertThat(matcher.iterator().next()).isEqualTo(new DateRangeMatcher(Instant.EPOCH, Instant.ofEpochMilli(3600000), false, Match.NOT));
    }
    
    public record B(String b) {
        
    }
    public static class BDerializer extends JsonDeserializer<B> {
        @Override
        public B deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            JsonNode jsonNode = jp.readValueAsTree();
            return new B(jsonNode.get("b").asText());
        }
    }
    public record A(String a,
                    @JsonDeserialize(using = BDerializer.class)
                    B b
    ) {
        
    }
    
    @Test
    public void test() throws JsonProcessingException {
        String example = """
               {
                   "a" : "a",
                   "b" : {
                     "b" : "b"
                   }
                 }
            """;
        Jackson2TestUtil.roundTripAndSimilar(new A("a", new B("b")), example);
        
        A a = Jackson2Mapper.getInstance().readValue(example, A.class);
        log.info("{}", a);
        
    }
}
