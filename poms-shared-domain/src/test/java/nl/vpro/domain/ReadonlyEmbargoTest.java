package nl.vpro.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 5.6.1
 */
public class ReadonlyEmbargoTest {


    public static class ReadonlyEmbargoTestClass implements ReadonlyEmbargo {
        private final Instant start;

        private final Instant stop;

        public ReadonlyEmbargoTestClass(Instant start, Instant stop) {
            this.start = start;
            this.stop = stop;
        }

        @Override
        public Instant getPublishStartInstant() {
            return start;
        }

        @Override
        public Instant getPublishStopInstant() {
            return stop;
        }
    }

    @Test
    public void test() {

        Instant start = LocalDateTime.of(2018, 3, 5, 11, 55).atZone(ZoneId.of("Europe/Amsterdam")).toInstant();
        Instant stop = LocalDateTime.of(2018, 10, 20, 11, 55).atZone(ZoneId.of("Europe/Amsterdam")).toInstant();
        Instant between = LocalDateTime.of(2018, 4, 6, 11, 55).atZone(ZoneId.of("Europe/Amsterdam")).toInstant();

        ReadonlyEmbargo embargo = new ReadonlyEmbargoTestClass(start, stop);

        assertThat(embargo.asRange().contains(between)).isTrue();
        assertThat(embargo.asRange().contains(start)).isTrue();
        assertThat(embargo.asRange().contains(stop)).isFalse();

        assertThat(embargo.inPublicationWindow(between)).isTrue();
        assertThat(embargo.inPublicationWindow(start)).isTrue();
        assertThat(embargo.inPublicationWindow(stop)).isFalse();



    }


}
