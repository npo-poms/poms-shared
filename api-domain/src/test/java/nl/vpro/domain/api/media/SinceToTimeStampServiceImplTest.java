package nl.vpro.domain.api.media;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.Schedule;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 */
public class SinceToTimeStampServiceImplTest {

    private final SinceToTimeStampServiceImpl impl = new SinceToTimeStampServiceImpl();

    public SinceToTimeStampServiceImplTest() {
    }

    @Test
    public void getInstance() {
        assertThat(impl.getInstance(0L)).isEqualTo(Instant.EPOCH);

        long now = System.currentTimeMillis();
        assertThat(impl.getInstance(now).toEpochMilli()).isEqualTo(now);
    }

    @Test
    public void getInstance2() {
        assertThat(impl.getInstance(17019615L).truncatedTo(ChronoUnit.MINUTES)).isEqualTo(LocalDateTime.of(2015, 7 , 1, 15, 24, 0).atZone(Schedule.ZONE_ID).toInstant());


    }


    @Test
    public void getInstance3() {
        assertThat(impl.getInstance(25387000L)
            .truncatedTo(ChronoUnit.MINUTES)).isEqualTo(LocalDateTime.of(2016, 11, 1, 12, 0, 0).atZone(Schedule.ZONE_ID).toInstant());


    }


    @Test
    public void getInstance4() {

    }

    @Test
    public void getSince() {

    }

}
