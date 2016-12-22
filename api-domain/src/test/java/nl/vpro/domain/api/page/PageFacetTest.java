package nl.vpro.domain.api.page;

import org.junit.Test;

import nl.vpro.domain.api.Match;
import nl.vpro.domain.api.SimpleTextMatcher;
import nl.vpro.domain.api.StandardMatchType;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class PageFacetTest {

    @Test
    public void testGetPageSearchFromFacetXml() throws Exception {
        PageSearch search = new PageSearch();
        search.setText(new SimpleTextMatcher("find me"));

        PageFacet facet = new PageFacet();
        facet.setFilter(search);

        PageFacet out = JAXBTestUtil.roundTrip(facet,
            "<api:filter>\n" +
                "        <api:text>find me</api:text>\n" +
                "    </api:filter>"
        );
        assertThat(out.getFilter()).isNotNull();
    }

    @Test
    public void testGetPageSearchFromFacetJson() throws Exception {
        PageSearch search = new PageSearch();
        search.setText(new SimpleTextMatcher("find me"));

        PageFacet facet = new PageFacet();
        facet.setFilter(search);

        PageFacet out = Jackson2TestUtil.roundTripAndSimilar(facet,
            "{\"sort\":\"VALUE_ASC\", \"max\" : 24,\"filter\":{\"text\":\"find me\"}}"
        );
        assertThat(out.getFilter()).isInstanceOf(PageSearch.class);
        assertThat(out.getFilter().getText().getValue()).isEqualTo("find me");
        assertThat(out.getFilter().getText().getMatch()).isEqualTo(Match.MUST);
        assertThat(out.getFilter().getText().getMatchType().getName()).isEqualTo(StandardMatchType.TEXT.getName());

    }
}
