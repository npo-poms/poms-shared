package nl.vpro.domain.npo.mediastatus;

import java.io.StringReader;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MediaStatusTest {

    String example = """
<?xml version="1.0" encoding="UTF-8"?>
<mediastatus timestamp="2025-09-01T09:00:26">
    <duration>
       00:07:18:18
    </duration>
    <framerate>
        25
    </framerate>
    <width>
        1920
    </width>
    <mid>
        WO_NTR_20288291
    </mid>
    <streamCount>
        <video>
            1
        </video>
        <audio>
            1
        </audio>
    </streamCount>
    <height>
        1080
    </height>
    </mediastatus>
        """;

    @Test
    public void xml() {
        MediaStatus status = JAXB.unmarshal(new StringReader(example), MediaStatus.class);

        assertThat(status.getMid()).isEqualTo("WO_NTR_20288291");

        //JAXBTestUtil.roundTripAndSimilar(example, MediaStatus.class);

    }

}
