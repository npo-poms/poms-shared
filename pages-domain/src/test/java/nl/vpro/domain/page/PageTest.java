package nl.vpro.domain.page;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class PageTest {

    @Test
    public void withEverything() throws Exception {
        Page page = Page.builder()
            .url("http://www.npo.nl/1")
            .alternativeUrls("http://npo.nl/1")
            .broadcasters("VPRO", "HUMAN")
            .crids("crid://npo/1")
            .keywords("foo", "bar")
            .subtitle("sub title")
            .embeds(MediaTestDataBuilder.program().withEverything().build())
            .build();
        Page rounded = Jackson2TestUtil.roundTripAndSimilar(Jackson2Mapper.getPublisherInstance(), page, getClass().getResourceAsStream("/page-with-everything.json"));
    }

}
