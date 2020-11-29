package nl.vpro.domain.api.media;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.TermFacetResultItem;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class MediaFacetsResultTest {


    @Test
    public void getTitles() {

        MediaFacetsResult result = new MediaFacetsResult();
        result.setTitles(new ArrayList<>());

        result.getTitles().add(new TermFacetResultItem("a", "a", 10));
        result.getTitles().add(new TermFacetResultItem("b", "b", 20));


        JAXBTestUtil.roundTripAndSimilar(result, "<local:mediaFacetsResult xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
            "    <api:titles>\n" +
            "        <api:count>10</api:count>\n" +
            "        <api:id>a</api:id>\n" +
            "        <api:value>a</api:value>\n" +
            "    </api:titles>\n" +
            "    <api:titles>\n" +
            "        <api:count>20</api:count>\n" +
            "        <api:id>b</api:id>\n" +
            "        <api:value>b</api:value>\n" +
            "    </api:titles>\n" +
            "</local:mediaFacetsResult>\n");
    }


}
