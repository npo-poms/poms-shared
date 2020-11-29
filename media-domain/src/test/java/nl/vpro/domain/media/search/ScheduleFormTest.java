package nl.vpro.domain.media.search;

import java.time.Instant;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.ScheduleEvent;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduleFormTest {

    @Test
    public void testTest() {
        ScheduleForm form = ScheduleForm.builder().pager(new SchedulePager()).startRange(new InstantRange(Instant.ofEpochMilli(100), Instant.ofEpochMilli(200))).build();

        assertThat(form.test(ev(null, 150))).isTrue();
        assertThat(form.test(ev(null, 80))).isFalse();

        form.setChannels(Arrays.asList(Channel.BBC1));
        assertThat(form.test(ev(Channel.BBC1, 150))).isTrue();
        assertThat(form.test(ev(Channel.BBC2, 150))).isFalse();
        form.setChannels(Arrays.asList(Channel.BBC1, Channel.BBC2));
        assertThat(form.test(ev(Channel.BBC1, 150))).isTrue();
    }

    private ScheduleEvent ev(Channel c, long start) {
        ScheduleEvent e = new ScheduleEvent();
        e.setStartInstant(Instant.ofEpochMilli(start));
        e.setChannel(c);
        return e;

    }
}
