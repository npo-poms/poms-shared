package nl.vpro.domain.api.schedule;

import java.time.LocalDate;

import org.junit.Test;

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
    public void applyNet() throws Exception {

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

}
