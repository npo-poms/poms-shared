package nl.vpro.domain.api.jackson;

import nl.vpro.jackson2.Jackson2Mapper;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.ExtendedTextMatcher;
import nl.vpro.domain.api.ExtendedTextMatcherList;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.6
 */
public class ExtendedTextMatcherListJsonTest {

    @Test
    public void testGetValueJson() throws Exception {
        ExtendedTextMatcherList in = new ExtendedTextMatcherList(new ExtendedTextMatcher("title"));

        StringWriter writer = new StringWriter();
        Jackson2Mapper.getInstance().writeValue(writer, in);

        assertThat(writer.toString()).isEqualTo("\"title\"");
    }

    @Test
    public void testGetValueJsonIgnoreCase() {
        ExtendedTextMatcherList in = new ExtendedTextMatcherList(new ExtendedTextMatcher("title", false));

        ExtendedTextMatcherList out = Jackson2TestUtil.roundTripAndSimilar(in, "{\n" +
            "  \"value\" : \"title\",\n" +
            "  \"caseSensitive\" : false\n" +
            "}");

        assertThat(out.get(0).isCaseSensitive()).isFalse();
    }

}
