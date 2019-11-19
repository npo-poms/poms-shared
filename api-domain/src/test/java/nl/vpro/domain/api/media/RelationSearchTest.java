package nl.vpro.domain.api.media;

import org.junit.Test;

import nl.vpro.domain.api.TextMatcher;
import nl.vpro.domain.api.TextMatcherList;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 4.9
 */
public class RelationSearchTest {


    @Test
    public void json() {
        RelationSearch search = new RelationSearch();
        search.setBroadcasters(TextMatcherList.must(TextMatcher.not("VPRO")));
        Jackson2TestUtil.roundTripAndSimilarAndEquals(search, "{\n" +
            "  \"broadcasters\" : {\n" +
            "    \"value\" : \"VPRO\",\n" +
            "    \"match\" : \"NOT\"\n" +
            "  }\n" +
            "}");
    }

}
