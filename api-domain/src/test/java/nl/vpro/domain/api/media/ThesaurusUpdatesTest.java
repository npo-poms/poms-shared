package nl.vpro.domain.api.media;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class ThesaurusUpdatesTest {

    @Test
    public void json() throws Exception {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("a", "A");
        list.add(map);
        ThesaurusUpdates updates = new ThesaurusUpdates(list, 0);
        Jackson2TestUtil.roundTripAndSimilar(updates, "{\n" +
            "  \"offset\" : 0,\n" +
            "  \"items\" : [ {\n" +
            "    \"a\" : \"A\"\n" +
            "  } ]\n" +
            "}");

    }

    @Test
    // TODO Fails, XML of 'ThesaurusUpdates' does not work.
    public void xml() throws Exception {

        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("a", "A");
        list.add(map);
        ThesaurusUpdates updates = new ThesaurusUpdates(list, 0);
        JAXBTestUtil.roundTripAndSimilar(updates, "<a />");

    }

}
