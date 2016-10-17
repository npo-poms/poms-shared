/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import nl.vpro.domain.media.support.Description;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Title;

import static nl.vpro.domain.media.MediaObjects.findOwnersForTextFields;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 1.4
 */
public class MediaObjectsTest {

    @Test
    public void testFindOwnersForTextFieldsOnOrdering() throws Exception {
        final Program program = MediaBuilder.program()
            .titles(new Title("1", OwnerType.CERES, TextualType.MAIN))
            .descriptions(new Description("1", OwnerType.BROADCASTER, TextualType.EPISODE))
            .build();

        assertThat(findOwnersForTextFields(program).length).isEqualTo(2);
        assertThat(findOwnersForTextFields(program)[0]).isEqualTo(OwnerType.BROADCASTER);
    }

    @Test
    public void testFindOwnersForTextFieldsOnDuplicates() throws Exception {
        final Program program = MediaBuilder.program().titles(
            new Title("1", OwnerType.CERES, TextualType.MAIN),
            new Title("1", OwnerType.CERES, TextualType.EPISODE)
        ).build();

        assertThat(findOwnersForTextFields(program).length).isEqualTo(1);
    }


    @Test
    public void sortDate() {
        Program program = new Program();
        assertThat(Math.abs(MediaObjects.getSortDate(program).getTime() - System.currentTimeMillis())).isLessThan(10000);
        Date publishDate = new Date(1344043500362L);
        program.setPublishStart(publishDate);
        assertThat(MediaObjects.getSortDate(program)).isEqualTo(publishDate);
        ScheduleEvent se = new ScheduleEvent();
        se.setStart(new Date(1444043500362L));
        program.addScheduleEvent(se);
        assertThat(MediaObjects.getSortDate(program)).isEqualTo(se.getStart());
        Segment segment = new Segment();
        program.addSegment(segment);
        assertThat(MediaObjects.getSortDate(segment)).isEqualTo(se.getStart());
    }

    @Test
    public void testSortDateWithScheduleEvents() throws Exception {
        final Program program = MediaBuilder.program()
            .creationDate(new Date(1))
            .publishStart(new Date(2))
            .scheduleEvents(
                new ScheduleEvent(Channel.NED2, new Date(13), new Date(10)),
                new ScheduleEvent(Channel.NED1, new Date(3), new Date(10))
            )
            .build();

        assertThat(MediaObjects.getSortDate(program)).isEqualTo(new Date(3));
    }

    @Test
    public void testSortDateWithPublishStart() throws Exception {
        final Program program = MediaBuilder.program()
            .creationDate(new Date(1))
            .publishStart(new Date(2))
            .build();

        assertThat(MediaObjects.getSortDate(program)).isEqualTo(new Date(2));
    }

    @Test
    public void testSortDateWithCreationDate() throws Exception {
        final Program program = MediaBuilder.program()
            .creationDate(new Date(1))
            .build();

        assertThat(MediaObjects.getSortDate(program)).isEqualTo(new Date(1));
    }

    @Test
    public void testSync() throws Exception {
        Website a = new Website("a");
        a.setId(1L);
        Website b = new Website("b");
        b.setId(2L);
        Website c = new Website("c");
        c.setId(3L);
        Website d = new Website("d"); // new

        List<Website> existing = new ArrayList<>(Arrays.asList(new Website[]{a, b, c}));
        List<Website> updates = new ArrayList<>(Arrays.asList(new Website[]{b, d, a}));
        MediaObjects.integrate(existing, updates);

        assertThat(existing).containsSequence(b, d, a);
    }

    @Test
    public void testFindScheduleEventHonoringOffset() throws Exception {
        final Program program = MediaBuilder.program()
            .scheduleEvents(new ScheduleEvent(Channel.NED1, new Date(100), new Date(100)))
            .build();

        final ScheduleEvent mismatch = new ScheduleEvent(Channel.NED1, new Date(90), new Date(100));
        mismatch.setOffset(Duration.ofMillis(9));
        assertThat(MediaObjects.findScheduleEventHonoringOffset(program, mismatch)).isNull();

        final ScheduleEvent match = new ScheduleEvent(Channel.NED1, new Date(90), new Date(100));
        match.setOffset(Duration.ofMillis(10));
        assertThat(MediaObjects.findScheduleEventHonoringOffset(program, match)).isNotNull();
    }
}
