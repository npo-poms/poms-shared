package nl.vpro.domain.api.media;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class RedirectListTest {

    private final RedirectList instance;

    {
        Map<String, String> redirects = new LinkedHashMap<>();
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
            """
                {
                  "lastUpdate" : "1970-01-01T01:00:00+01:00",
                  "map" : {
                    "a" : "b",
                    "x" : "y",
                    "y" : "z",
                    "source1" : "target_but_source",
                    "z" : "x",
                    "source" : "target",
                    "anothersource" : "target",
                    "target_but_source" : "ultimate_target"
                  }
                }""");
        assertThat(rounded.getList()).hasSize(8);
        assertThat(rounded.getList().get(0).getFrom()).isEqualTo("a");
        assertThat(rounded.getList().get(0).getTo()).isEqualTo("b");
        assertThat(rounded.getMap()).hasSize(8);
        assertThat(rounded.getMap().get("a")).isEqualTo("b");
    }

    @Test
    public void jaxb() {
        RedirectList rounded = JAXBTestUtil.roundTripAndSimilarAndEquals(instance,
            """
                <?xml version="1.0" encoding="UTF-8"?><redirects xmlns="urn:vpro:api:2013" lastUpdate="1970-01-01T01:00:00+01:00" xmlns:shared="urn:vpro:shared:2009" xmlns:media="urn:vpro:media:2009">
                    <entry from="a" to="b" ultimate="b"/>
                    <entry from="source" to="target" ultimate="target"/>
                    <entry from="anothersource" to="target" ultimate="target"/>
                    <entry from="source1" to="target_but_source" ultimate="ultimate_target"/>
                    <entry from="target_but_source" to="ultimate_target" ultimate="ultimate_target"/>
                    <entry from="x" to="y" circular="true"/>
                    <entry from="y" to="z" circular="true"/>
                    <entry from="z" to="x" circular="true"/>
                </redirects>""");
        assertThat(rounded.getList()).hasSize(8);
        assertThat(rounded.getList().get(0).getFrom()).isEqualTo("a");
        assertThat(rounded.getList().get(0).getTo()).isEqualTo("b");
        assertThat(rounded.getMap()).hasSize(8);
        assertThat(rounded.getMap().get("a")).isEqualTo("b");


    }

}
