package nl.vpro.domain.page.update;

import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class DeleteResultTest {

    @Test
    public void json() throws Exception {
        DeleteResult result = DeleteResult.builder()
            .future(CompletableFuture.completedFuture("bla"))
            .count(100)
            .build();

        Jackson2TestUtil.roundTripAndSimilar(result, "{\n" +
            "  \"count\" : 100,\n" +
            "  \"notallowedCount\" : 0,\n" +
            "  \"success\" : true\n" +
            "}");
    }


    @Test
    public void xml() throws Exception {
        DeleteResult result = DeleteResult.builder()
            .future(CompletableFuture.completedFuture("bla"))
            .count(100)
            .build();

        JAXBTestUtil.roundTripAndSimilar(result, "<pageUpdate:deleteresult count=\"100\" notallowedCount=\"0\" success=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:page=\"urn:vpro:pages:2013\" xmlns:pageUpdate=\"urn:vpro:pages:update:2013\"/>");
    }

}
