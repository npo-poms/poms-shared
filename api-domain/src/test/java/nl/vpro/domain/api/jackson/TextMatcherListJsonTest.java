package nl.vpro.domain.api.jackson;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.*;
import nl.vpro.jackson2.Jackson2Mapper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class TextMatcherListJsonTest {

    Jackson2Mapper INSTANCE = Jackson2Mapper.getInstance();
    @Test
    public void testGetValueJson() throws Exception {
        TextMatcher in = new TextMatcher("title", Match.NOT);
        TextMatcherList list = new TextMatcherList(in);

        StringWriter writer = new StringWriter();
        Jackson2Mapper.getInstance().writeValue(writer, list);

        assertThat(writer.toString()).isEqualTo("{\"value\":\"title\",\"match\":\"NOT\"}");

        TextMatcherList out = INSTANCE.readValue(new StringReader(writer.toString()), TextMatcherList.class);
        assertThat(out.asList()).containsExactly(new TextMatcher("title", Match.NOT));
        assertThat(out.getMatch()).isEqualTo(Match.MUST);
    }

    @Test
    public void testGetValueFromJson() throws Exception {
        TextMatcherList matcher = INSTANCE.readValue("[{\"value\":\"title\",\"match\":\"not\"}]", TextMatcherList.class);

        assertThat(matcher.size()).isEqualTo(1);
        assertThat(matcher.iterator().next()).isEqualTo(new TextMatcher("title", Match.NOT));
    }

    @Test
    public void testGetValueJsonInverse() throws Exception {
        TextMatcher in = new TextMatcher("title", Match.NOT);
        TextMatcherList list = new TextMatcherList(Collections.singletonList(in), Match.NOT);

        StringWriter writer = new StringWriter();
        INSTANCE.writeValue(writer, list);

        assertThat(writer.toString()).isEqualTo("{\"value\":[{\"value\":\"title\",\"match\":\"NOT\"}],\"match\":\"NOT\"}");

        TextMatcherList out = INSTANCE.readValue(new StringReader(writer.toString()), TextMatcherList.class);
        assertThat(out.asList()).containsExactly(new TextMatcher("title", Match.NOT));
        assertThat(out.getMatch()).isEqualTo(Match.NOT);
    }

    @Test
    public void testGetValueFromJsonInverse() throws Exception {
        TextMatcherList matcher = INSTANCE.readValue("{\"value\":[{\"value\":\"title\",\"match\":\"not\"}],\"match\":\"not\"}", TextMatcherList.class);

        assertThat(matcher.iterator()).toIterable().hasSize(1);
        assertThat(matcher.iterator().next()).isEqualTo(new TextMatcher("title", Match.NOT));
    }
}
