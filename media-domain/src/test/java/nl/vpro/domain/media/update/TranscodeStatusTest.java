package nl.vpro.domain.media.update;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public class TranscodeStatusTest {


    @Test
    public void xml() {
        TranscodeStatus status = TranscodeStatus
            .builder()
            .mid("mid_123")
            .broadcasters(Arrays.asList("VPRO", "EO"))
            .build();

        JAXBTestUtil.roundTripAndSimilar(status, """
            <transcodeStatus mid="mid_123" xmlns="urn:vpro:media:update:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:media="urn:vpro:media:2009">
                <broadcasters>
                    <broadcaster>VPRO</broadcaster>
                    <broadcaster>EO</broadcaster>
                </broadcasters>
            </transcodeStatus>""");
    }

}
