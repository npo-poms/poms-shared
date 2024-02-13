package nl.vpro.domain.media;

import java.io.StringReader;
import java.time.LocalDate;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class StreamingStatusTest {


    @Test
    public void unmarshal() {
        String xml =
            "<streamingstatus withDrm=\"ONLINE\"/>";

        StringReader reader = new StringReader(xml);

        StreamingStatusImpl status = JAXB.unmarshal(reader, StreamingStatusImpl.class);

        assertThat(status.getWithDrm()).isEqualTo(StreamingStatus.Value.ONLINE);

    }


    @Test
    public void testMarshalToXml() {
        StreamingStatusImpl status = new StreamingStatusImpl();
        status.setWithDrm(StreamingStatus.Value.ONLINE);

        JAXBTestUtil.roundTripAndSimilar(status,
            "<streamingStatus withDrm=\"ONLINE\" withoutDrm=\"UNSET\" audioWithoutDrm=\"UNSET\" xmlns=\"urn:vpro:media:2009\"  />\n");
    }


    @Test
    public void testMarshalJson() {
        StreamingStatusImpl status = new StreamingStatusImpl();
        status.setWithDrm(StreamingStatus.Value.ONLINE);

        Jackson2TestUtil.roundTripAndSimilar(status,
            """
                {
                    "withDrm" : "ONLINE",
                    "withoutDrm" : "UNSET",
                    "audioWithoutDrm" : "UNSET"
                  }
                """
            );
    }

    @Test
    /*  displayName
     *  The DRM status offline date is in the far future, the long display notation is expected
     */
    public void displayName() {
        StreamingStatus status = StreamingStatusImpl.builder()
            .withDrmOffline(LocalDate.of(2200, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant())
            .withDrm(StreamingStatus.Value.ONLINE)
            .build();

        assertThat(status.getDisplayName()).isEqualTo("Beschikbaar in DVR window tot 1 januari 2200 00:00");

    }


}
