package nl.vpro.domain.api.media;

import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */
public class ScheduleFormTest {

    @Test
    public void adapter() throws IOException, SAXException {
        ScheduleForm form = new ScheduleForm();
        JAXBTestUtil.roundTripAndSimilar(form, "<api:scheduleForm xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\"/>");
    }

}
