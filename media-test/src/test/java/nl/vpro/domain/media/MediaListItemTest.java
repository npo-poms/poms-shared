package nl.vpro.domain.media;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.search.MediaListItem;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 */
class MediaListItemTest {

    @Test
    void xml() throws IOException {
        Program program = MediaTestDataBuilder.program().withEverything().build();
        MediaListItem item = new MediaListItem(program);

        JAXBTestUtil.roundTripAndSimilar(item,
            getClass().getResourceAsStream("/media-listitem-with-everything.xml")
        );
    }
}
