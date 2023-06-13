package nl.vpro.domain.media.update;

import org.junit.jupiter.api.Test;

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
        JAXBTestUtil.roundTripAndSimilar(request, """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <transcode mid='MID_123' xmlns="urn:vpro:media:update:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:media="urn:vpro:media:2009">
                <fileName>vpro/test.m4v</fileName>
                <encryption>DRM</encryption>
                <priority>NORMAL</priority>
            </transcode>""");
    }

}
