package nl.vpro.rs.pages.update;

import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;

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


}
