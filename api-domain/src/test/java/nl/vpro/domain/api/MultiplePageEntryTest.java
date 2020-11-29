package nl.vpro.domain.api;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.page.Page;
import nl.vpro.domain.page.PageType;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class MultiplePageEntryTest {


    @Test
    public void json() {
        MultiplePageEntry entry = new MultiplePageEntry("bla", Page.builder().type(PageType.HOME).url("http://www.vpro.nl/bla").build());

        Jackson2TestUtil.roundTripAndSimilar(entry, "{\n" +
            "  \"id\" : \"bla\",\n" +
            "  \"result\" : {\n" +
            "    \"objectType\" : \"page\",\n" +
            "    \"type\" : \"HOME\",\n" +
            "    \"url\" : \"http://www.vpro.nl/bla\"\n" +
            "  }\n" +
            "}");
    }

}
