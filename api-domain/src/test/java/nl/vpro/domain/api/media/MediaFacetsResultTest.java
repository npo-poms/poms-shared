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


        JAXBTestUtil.roundTripAndSimilar(result, """
            <local:mediaFacetsResult xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009" xmlns:local="uri:local">
                <api:titles>
                    <api:count>10</api:count>
                    <api:id>a</api:id>
                    <api:value>a</api:value>
                </api:titles>
                <api:titles>
                    <api:count>20</api:count>
                    <api:id>b</api:id>
                    <api:value>b</api:value>
                </api:titles>
            </local:mediaFacetsResult>
            """);
    }


}
