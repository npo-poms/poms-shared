package nl.vpro.domain.media.update;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.media.Encryption;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public class TranscodeRequestTest {


    @Test
    public void xml() {
        TranscodeRequest request = TranscodeRequest.builder()
            .mid("MID_123")
            .encryption(Encryption.DRM)
            .priority(TranscodeRequest.Priority.NORMAL)
            .fileName("vpro/test.m4v")
            .build();
        JAXBTestUtil.roundTripAndSimilar(request, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<transcode mid='MID_123' xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <fileName>vpro/test.m4v</fileName>\n" +
            "    <encryption>DRM</encryption>\n" +
            "    <priority>NORMAL</priority>\n" +
            "</transcode>");
    }

}
