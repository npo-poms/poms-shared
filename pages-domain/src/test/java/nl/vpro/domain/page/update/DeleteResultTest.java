package nl.vpro.domain.page.update;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.logging.simple.StringBuilderSimpleLogger;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static nl.vpro.logging.simple.Slf4jSimpleLogger.slf4j;
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
            .duration(Duration.ofMillis(123))
            .build();

        Jackson2TestUtil.roundTripAndSimilar(result, """
            {
                "count" : 100,
                "notallowedCount" : 0,
                "success" : true,
                "duration" : "P0DT0H0M0.123S"
              }""");
    }


    @Test
    public void xml() {
        DeleteResult result = DeleteResult.builder()
            .future(CompletableFuture.completedFuture("bla"))
            .count(100)
            .duration(Duration.ofMillis(123))
            .build();

        JAXBTestUtil.roundTripAndSimilar(result, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<pageUpdate:deleteresult xmlns:pageUpdate=\"urn:vpro:pages:update:2013\" count=\"100\" notallowedCount=\"0\"  success=\"true\" duration=\"P0DT0H0M0.123S\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:page=\"urn:vpro:pages:2013\"/>");
    }

    @Test
    public void xmlWithUnCompleteFuture() {
        DeleteResult result = DeleteResult.builder()
            .count(100)
            .duration(Duration.ofMillis(123))
            .build();

        JAXBTestUtil.roundTripAndSimilar(result, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<pageUpdate:deleteresult xmlns:pageUpdate=\"urn:vpro:pages:update:2013\" count=\"100\" notallowedCount=\"0\"  success=\"true\" duration=\"P0DT0H0M0.123S\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:page=\"urn:vpro:pages:2013\"/>");
    }

    @Test
    public void and() throws ExecutionException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        SimpleLogger logger = StringBuilderSimpleLogger.builder().stringBuilder(sb).build().chain(slf4j(log));
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
        assertThat(sb.toString()).isEqualTo("""
            INFO Ready a
            INFO Ready b
            INFO Ready a & b""");

    }

}
