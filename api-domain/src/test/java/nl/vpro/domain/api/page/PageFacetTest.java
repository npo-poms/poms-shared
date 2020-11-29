package nl.vpro.domain.api.page;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.api.Match;
import nl.vpro.domain.api.SimpleTextMatcher;
import nl.vpro.domain.api.StandardMatchType;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class PageFacetTest {

    @Test
    public void testGetPageSearchFromFacetXml() {
        PageSearch search = new PageSearch();
        search.setText(new SimpleTextMatcher("find me"));

        PageFacet facet = new PageFacet();
        facet.setFilter(search);

        PageFacet out = JAXBTestUtil.roundTripAndSimilar(facet, "<local:pageFacet sort=\"VALUE_ASC\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
            "    <api:max>24</api:max>\n" +
            "    <api:filter>\n" +
            "        <api:text>find me</api:text>\n" +
            "    </api:filter>\n" +
            "</local:pageFacet>");

        assertThat(out.getFilter()).isNotNull();
    }

    @Test
    public void testGetPageSearchFromFacetJson() {
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
