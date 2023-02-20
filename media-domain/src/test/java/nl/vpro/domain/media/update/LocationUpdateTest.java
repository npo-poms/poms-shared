package nl.vpro.domain.media.update;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.AVFileFormat;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.junit.jupiter.api.Assertions.*;

class LocationUpdateTest {


    @Test
    public void xml() {
        JAXBTestUtil.roundTripAndSimilar(
            LocationUpdate.builder()
                .width(640)
                .height(320)
                .format(AVFileFormat.MP4)
                .duration(Duration.ofMinutes(5))
                .programUrl("https://www.vpro.nl/test.mp4")
                .build()
            , "<location xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <programUrl>https://www.vpro.nl/test.mp4</programUrl>\n" +
                "    <avAttributes>\n" +
                "        <avFileFormat>MP4</avFileFormat>\n" +
                "        <videoAttributes width=\"640\" height=\"320\"/>\n" +
                "    </avAttributes>\n" +
                "    <duration>P0DT0H5M0.000S</duration>\n" +
                "</location>"
        );
    }
}
