package nl.vpro.domain.api.media;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class RedirectListTest {

    private RedirectList instance;

    {
        Map<String, String> redirects = new HashMap<>();
        redirects.put("a", "b");
        redirects.put("source", "target");
        redirects.put("anothersource", "target");
        redirects.put("source1", "target_but_source");
        redirects.put("target_but_source", "ultimate_target");

        redirects.put("x", "y");
        redirects.put("y", "z");
        redirects.put("z", "x");

        instance = new RedirectList(Instant.EPOCH, redirects);
    }

    @Test
    public void json() {
        RedirectList rounded = Jackson2TestUtil.roundTripAndSimilarAndEquals(instance,
            "{\n" +
                "  \"lastUpdate\" : \"1970-01-01T01:00:00+01:00\",\n" +
                "  \"map\" : {\n" +
                "    \"a\" : \"b\",\n" +
                "    \"x\" : \"y\",\n" +
                "    \"y\" : \"z\",\n" +
                "    \"source1\" : \"target_but_source\",\n" +
                "    \"z\" : \"x\",\n" +
                "    \"source\" : \"target\",\n" +
                "    \"anothersource\" : \"target\",\n" +
                "    \"target_but_source\" : \"ultimate_target\"\n" +
                "  }\n" +
                "}");
        assertThat(rounded.getList()).hasSize(8);
        assertThat(rounded.getList().get(0).getFrom()).isEqualTo("a");
        assertThat(rounded.getList().get(0).getTo()).isEqualTo("b");
        assertThat(rounded.getMap()).hasSize(8);
        assertThat(rounded.getMap().get("a")).isEqualTo("b");
    }

    @Test
    public void jaxb() {
        RedirectList rounded = JAXBTestUtil.roundTripAndSimilarAndEquals(instance,
            "<redirects lastUpdate=\"1970-01-01T01:00:00+01:00\" xmlns=\"urn:vpro:api:2013\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <entry from=\"a\" to=\"b\" ultimate=\"b\"/>\n" +
                "    <entry from=\"x\" to=\"y\" ultimate=\"x\"/>\n" +
                "    <entry from=\"y\" to=\"z\" ultimate=\"y\"/>\n" +
                "    <entry from=\"source1\" to=\"target_but_source\" ultimate=\"ultimate_target\"/>\n" +
                "    <entry from=\"z\" to=\"x\" ultimate=\"z\"/>\n" +
                "    <entry from=\"source\" to=\"target\" ultimate=\"target\"/>\n" +
                "    <entry from=\"anothersource\" to=\"target\" ultimate=\"target\"/>\n" +
                "    <entry from=\"target_but_source\" to=\"ultimate_target\" ultimate=\"ultimate_target\"/>\n" +
                "</redirects>");
        assertThat(rounded.getList()).hasSize(8);
        assertThat(rounded.getList().get(0).getFrom()).isEqualTo("a");
        assertThat(rounded.getList().get(0).getTo()).isEqualTo("b");
        assertThat(rounded.getMap()).hasSize(8);
        assertThat(rounded.getMap().get("a")).isEqualTo("b");


    }

}
