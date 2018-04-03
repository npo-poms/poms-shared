package nl.vpro.domain.media;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXB;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class StreamingStatusTest {


    @Test
    public void unmarshal() {
        String xml =
            "<streamingstatus withDrm=\"ONLINE\"/>";

        StringReader reader = new StringReader(xml);

        StreamingStatus status = JAXB.unmarshal(reader, StreamingStatus.class);

        assertThat(status.getWithDrm()).isEqualTo(StreamingStatus.Value.ONLINE);

    }


    @Test
    public void testMarshalToXml() throws IOException, SAXException {
        StreamingStatus status = new StreamingStatus();
        status.setWithDrm(StreamingStatus.Value.ONLINE);

        JAXBTestUtil.roundTripAndSimilar(status,
            "<streamingStatus withDrm=\"ONLINE\" withoutDrm=\"UNSET\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"/>\n");
    }


}
