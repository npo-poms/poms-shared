package nl.vpro.domain.api;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

import javax.xml.bind.JAXB;

import org.junit.Test;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class IdListTest {

    @Test
    public void toxml() {
        IdList list = new IdList("a", "b");
        JAXB.marshal(list, System.out);

    }
    @Test
    public void fromXml() {
        IdList list = JAXB.unmarshal(new StringReader("<idList xmlns=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <id>a</id>\n" +
            "    <id>b</id>\n" +
            "</idList>"), IdList.class);
        assertThat(list).isEqualTo(Arrays.asList("a", "b"));
    }

    @Test
    public void json() {
        IdList list = new IdList("a", "b");
        Jackson2TestUtil.roundTripAndSimilar(list, "[\"a\",\"b\"]");

    }

    @Test
    public void xml() {
        IdList list = new IdList("a", "b");
        JAXBTestUtil.roundTripAndSimilar(list, "<api:idList xmlns:api=\"urn:vpro:api:2013\" >\n" +
            "    <api:id>a</api:id>\n" +
            "    <api:id>b</api:id>\n" +
            "</api:idList>");

    }

    @Test
    public void fromJson() throws IOException {
        IdList list = Jackson2Mapper.getInstance().readValue(new StringReader("[\"a\",\"b\"]" +
            "    <id>a</id>\n" +
            "    <id>b</id>\n" +
            "</idList>"), IdList.class);
        assertThat(list).isEqualTo(Arrays.asList("a", "b"));
    }
}
