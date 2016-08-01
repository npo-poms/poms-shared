package nl.vpro.domain.media.search;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.ScheduleEvent;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduleFormTest {

    @Test
    public void testTest() {
        ScheduleForm form = new ScheduleForm(new Pager(), new DateRange(new Date(100), new Date(200)));

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
        e.setStart(new Date(start));
        e.setChannel(c);
        return e;

    }
}
