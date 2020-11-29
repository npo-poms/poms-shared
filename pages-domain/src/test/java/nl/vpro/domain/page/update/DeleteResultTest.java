package nl.vpro.domain.page.update;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.logging.simple.StringBuilderSimpleLogger;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 */
@Slf4j
public class DeleteResultTest {

    @Test
    public void json() {
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
    public void xml() {
        DeleteResult result = DeleteResult.builder()
            .future(CompletableFuture.completedFuture("bla"))
            .count(100)
            .build();

        JAXBTestUtil.roundTripAndSimilar(result, "<pageUpdate:deleteresult count=\"100\" notallowedCount=\"0\" success=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:page=\"urn:vpro:pages:2013\" xmlns:pageUpdate=\"urn:vpro:pages:update:2013\"/>");
    }

    @Test
    public void and() throws ExecutionException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        SimpleLogger logger = StringBuilderSimpleLogger.builder().stringBuilder(sb).build().chain(SimpleLogger.slfj4(log));
        DeleteResult r1 = DeleteResult
            .builder()
            .future(CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(1000);
                    logger.info("Ready a");
                    return "a";
                } catch (InterruptedException iae) {
                    log.error(iae.getMessage());
                    return "a";
                }
            }))
            .build();


        DeleteResult r2 = DeleteResult
            .builder()
            .future(CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(2000);
                    logger.info("Ready b");
                    return "b";
                } catch (InterruptedException iae) {
                    log.error(iae.getMessage());
                    return "b";
                }
            }))
            .build();

        r1.and(r2).getFuture().get();
        logger.info("Ready a & b");
        assertThat(sb.toString()).isEqualTo("INFO Ready a\n" +
            "INFO Ready b\n" +
            "INFO Ready a & b");

    }

}
