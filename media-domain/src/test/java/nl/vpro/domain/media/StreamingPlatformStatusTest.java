package nl.vpro.domain.media;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXB;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.media.StreamingPlatformStatus;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class StreamingPlatformStatusTest {


    @Test
    public void unmarshal() {
        String xml =
            "<streamingstatus status=\"NOT_AVAILABLE\"/>";

        StringReader reader = new StringReader(xml);

        StreamingPlatformStatus status = JAXB.unmarshal(reader, StreamingPlatformStatus.class);

        assertThat(status.getStatus()).isEqualTo("NOT_AVAILABLE");

    }


    @Test
    public void testMarshalToXml() throws IOException, SAXException {
        StreamingPlatformStatus status = new StreamingPlatformStatus();
        status.setStatus("NOT_AVAILABLE");

        JAXBTestUtil.roundTripAndSimilar(status,
            "<streamingstatus status=\"NOT_AVAILABLE\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"/>\n");
    }


}
