package nl.vpro.domain.media;

import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.media.search.MediaListItem;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class MediaListItemTest {

    @Test
    public void xml() throws IOException, SAXException {
        Program program = MediaTestDataBuilder.program().withEverything().build();
        MediaListItem item = new MediaListItem(program);

        JAXBTestUtil.roundTripAndSimilar(item,
            getClass().getResourceAsStream("/media-listitem-with-everything.xml")
        );
    }
}
