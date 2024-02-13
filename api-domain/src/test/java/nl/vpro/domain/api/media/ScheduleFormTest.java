package nl.vpro.domain.api.media;

import java.io.IOException;
import java.io.StringReader;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.Test;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */
public class ScheduleFormTest {

    @Test
    public void adapter() {
        ScheduleForm form = new ScheduleForm();
        JAXBTestUtil.roundTripAndSimilar(form, "<api:scheduleForm xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\"/>");
    }

    @Test
    public void json() throws IOException {
        String example = "<api:scheduleForm xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:pages=\"urn:vpro:pages:2013\"><api:searches><api:genres match=\"MUST\"><api:matcher match=\"SHOULD\">3.0.1.2</api:matcher><api:matcher matchType=\"WILDCARD\" match=\"SHOULD\">3.0.1.2.*</api:matcher></api:genres><api:scheduleEvents><api:begin>2018-01-23T06:00:00+01:00</api:begin><api:end>2018-01-24T06:00:00+01:00</api:end><api:channel>NDR3</api:channel></api:scheduleEvents></api:searches></api:scheduleForm>";
        Jackson2Mapper.getLenientInstance().writeValue(System.out, JAXB.unmarshal(new StringReader(example), ScheduleForm.class));
    }

}
