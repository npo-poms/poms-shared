package nl.vpro.domain.media;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXB;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.media.support.StreamingStatus;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class StreamingStatusReportTest {


    @Test
    public void unmarshal() {
        String xml =
            "<streamingstatus status=\"NOT_AVAILABLE\"/>";

        StringReader reader = new StringReader(xml);

        StreamingStatusReport status = JAXB.unmarshal(reader, StreamingStatusReport.class);

        assertThat(status.getStatus()).isEqualTo(StreamingStatus.NOT_AVAILABLE);

    }


    @Test
    public void testMarshalToXml() throws IOException, SAXException {
        StreamingStatusReport status = new StreamingStatusReport();
        status.setStatus(StreamingStatus.NOT_AVAILABLE);

        JAXBTestUtil.roundTripAndSimilar(status,
            "<streamingstatus status=\"NOT_AVAILABLE\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"/>\n");
    }


}
