package nl.vpro.domain.media.update;

import java.util.Arrays;
import java.util.List;

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
            .crids(List.of("crid://bnnvara.mema.media_item/26e81510-e430-40a4-8df1-964c92c86a72"))
            .transcodeService(TranscodeStatus.TranscodeService.NEP)
            .build();

        JAXBTestUtil.roundTripAndSimilar(status, """
            <transcodeStatus mid="mid_123" transcodeService="NEP" xmlns="urn:vpro:media:update:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:media="urn:vpro:media:2009">
                <broadcasters>
                    <broadcaster>VPRO</broadcaster>
                    <broadcaster>EO</broadcaster>
                </broadcasters>
                <crids>
                    <crids>crid://bnnvara.mema.media_item/26e81510-e430-40a4-8df1-964c92c86a72</crids>
                </crids>
            </transcodeStatus>""");
    }

}
