package nl.vpro.domain.media;

import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class AgeRatingTest {

    @Test
    void getIcon() {
        Arrays.stream(AgeRating.values()).forEach(a -> {
            assertNotNull(a.getIcon());
            log.info("{} -> {}", a, a.getIcon());
        });
    }
}
