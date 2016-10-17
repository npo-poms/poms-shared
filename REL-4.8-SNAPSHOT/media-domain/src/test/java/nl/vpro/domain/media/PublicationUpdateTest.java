package nl.vpro.domain.media;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * @author Michiel Meeuwissen
 * @since 2.1
 */
public class PublicationUpdateTest {


    @Test
    public void testQueue() throws ParseException {
        PublicationUpdate _1990 = new PublicationUpdate(PublicationUpdate.Action.PUBLISH, "MID_7", 7L, LocalDate.of(1990, 12, 15).atTime(17, 47).atZone(Schedule.ZONE_ID).toInstant());
        PublicationUpdate _2100 = new PublicationUpdate(PublicationUpdate.Action.PUBLISH, "MID_7", 7L, LocalDate.of(2100, 12, 25).atTime(17, 47).atZone(Schedule.ZONE_ID).toInstant());
        PublicationUpdate NOW = new PublicationUpdate(PublicationUpdate.Action.PUBLISH, "MID_8", 8L, Instant.now());

        PublicationUpdate REPUBLISH = new PublicationUpdate(PublicationUpdate.Action.PUBLISH, "test", "MID_9", 9L, true, LocalDate.of(2000, 12, 25).atTime(17, 47).atZone(Schedule.ZONE_ID).toInstant(), null);

        BlockingQueue<PublicationUpdate> queue = new DelayQueue<>();
        queue.clear();
        queue.offer(NOW);
        queue.offer(_1990);
        queue.offer(_2100);
        queue.offer(REPUBLISH);

        System.out.println("" + queue);

        assertSame(_1990, queue.peek());
        assertSame(_1990, queue.poll());
        assertSame(NOW, queue.peek());
        assertSame(NOW, queue.poll());
        assertSame(REPUBLISH, queue.peek());
        assertSame(REPUBLISH, queue.poll());
        assertSame(_2100, queue.peek());
        assertNull(queue.poll());


        assertThat(_1990.getDelay(TimeUnit.NANOSECONDS)).isLessThan(0);
        assertThat(_2100.getDelay(TimeUnit.NANOSECONDS)).isGreaterThan(0);
        assertThat(NOW.getDelay(TimeUnit.NANOSECONDS)).isLessThanOrEqualTo(0);
    }

    @Test
    public void testOverdue() {
        Instant now = LocalDate.of(2016, 5, 24).atTime(12, 0).atZone(Schedule.ZONE_ID).toInstant();
        Program program = MediaBuilder.program(ProgramType.CLIP)
            .creationDate(LocalDate.of(2016, 5, 24).atTime(11, 50))
            .lastModified(LocalDate.of(2016, 5, 24).atTime(11, 50))
            .build();

        PublicationUpdate publicationUpdate = PublicationUpdate.publish("test", program);

        Duration overdue = publicationUpdate.getOverdue(now);
        assertThat(overdue).isEqualTo(Duration.ofMinutes(10));

    }


}
