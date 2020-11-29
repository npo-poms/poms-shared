package nl.vpro.domain.media.update;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.MediaType;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

public class MidAndTypeTest {

    @Test
    public void toXml() {
        MediaIdentifiableImpl type = new MediaIdentifiableImpl("RBX_NOS_703601", MediaType.BROADCAST, Arrays.asList("crid://broadcast.radiobox2/309304"));
        JAXBTestUtil.roundTripAndSimilar(type,"<midAndType mid=\"RBX_NOS_703601\" type=\"BROADCAST\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <crid>crid://broadcast.radiobox2/309304</crid>\n" +
            "</midAndType>");



    }
}
