package nl.vpro.domain.media;

import java.io.IOException;
import java.util.Locale;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

public class AvailableSubtitleTest {

    @Test
    public void test() throws IOException, SAXException {
        JAXBTestUtil.roundTripAndSimilar(new AvailableSubtitles(Locale.ENGLISH, SubtitlesType.TRANSLATION), "<local:availableSubtitles language=\"en\" type=\"TRANSLATION\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:local=\"uri:local\"/>");
    }

}
