package nl.vpro.domain.api.schedule;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.MediaBuilder;
import nl.vpro.domain.media.Net;
import nl.vpro.domain.media.ScheduleEvent;
import nl.vpro.domain.media.search.SchedulePager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.9
 */
public class ExtendedScheduleFormTest {


    @Test
    public void applyNet() {

        ScheduleEvent eventWithNet = new ScheduleEvent();
        eventWithNet.setNet(new Net("ZAPP"));

        ScheduleEvent eventWithoutNet = new ScheduleEvent();
        eventWithoutNet.setNet(null);

        ScheduleEvent eventWithDifferentNet = new ScheduleEvent();
        eventWithDifferentNet.setNet(new Net("foobar"));



        ExtendedScheduleForm form = new ExtendedScheduleForm(new SchedulePager(), (LocalDate) null);
        form.setNet("ZAPP");

       // assertThat(form.test(eventWithNet)).isTrue();
        assertThat(form.test(eventWithoutNet)).isFalse();
        assertThat(form.test(eventWithDifferentNet)).isFalse();



    }

    @Test
    public void applyDescendantOf() {

        ScheduleEvent event = new ScheduleEvent();
        event.setParent(MediaBuilder.program().descendantOf("mid123").build());


        ExtendedScheduleForm form = new ExtendedScheduleForm(new SchedulePager(), (LocalDate) null);
        form.setDescendantOf(Arrays.asList("mid123"));

        assertThat(form.test(event)).isTrue();

        form.setDescendantOf(Arrays.asList("mid456"));

        assertThat(form.test(event)).isFalse();

        form.setDescendantOf(null);

        assertThat(form.test(event)).isTrue();


    }

}
