package nl.vpro.domain.api.media;

import java.time.*;
import java.util.List;
import java.util.NoSuchElementException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import nl.vpro.domain.api.MediaChange;
import nl.vpro.domain.api.profile.ProfileDefinition;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.media.support.Workflow;

import static java.util.Arrays.asList;
import static nl.vpro.domain.media.MediaBuilder.clip;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Michiel Meeuwissen
 */
public class MarkSkippedChangeIteratorTest {

    static Instant INSTANT = of(2021, 3, 1, 12, 0);
    @SuppressWarnings("unchecked")
    private final ProfileDefinition<MediaObject> even =
        mock(ProfileDefinition.class);

    @SuppressWarnings("unchecked")
    private final ProfileDefinition<MediaObject> odd =
        mock(ProfileDefinition.class);

    {
        Answer<Boolean> idIsEven = invocationOnMock -> {
            MediaObject mo = invocationOnMock.getArgument(0);
            return mo.getId() % 2 == 0;
        };
        Answer<Boolean> idIsOdd = invocationOnMock -> {
            MediaObject mo = invocationOnMock.getArgument(0);
            return mo.getId() % 2 == 1;
        };
        when(even.test(any(MediaObject.class))).thenAnswer(idIsEven);
        when(odd.test(any(MediaObject.class))).thenAnswer(idIsOdd);
    }

    private final List<@NonNull MediaChange> fourChanges = asList(
        change(1, 1)
            .deleted(true)
            .build(),
        change(2, 1)
            .deleted(true)
            .build(),
        change(3, 2)
            .deleted(false)
            .build(),
        change(4, 2)
            .deleted(false)
            .build());


    @Test
    public void withoutProfile() throws Exception {

        try(MarkSkippedChangeIterator test = MarkSkippedChangeIterator.builder()
            .iterator(fourChanges.iterator())
            .since(INSTANT)
            .profile(null)
            .logBatch(2)
            .build()) {
            assertThat(test.getPublishDate()).isNull();
            test(test.next(), "2021-03-01T11:01:00Z", "mid_1", true);
            assertThat(test.getPublishDate()).isEqualTo("2021-03-01T11:01:00Z");
            assertThat(test.getSequence()).isEqualTo(1614596460000L);

            test(test.peek(), "2021-03-01T11:01:00Z", "mid_2", true);
            test(test.peekNext().get(), "2021-03-01T11:02:00Z", "mid_3", false);


            test(test.next(), "2021-03-01T11:01:00Z", "mid_2", true);
            test(test.next(), "2021-03-01T11:02:00Z", "mid_3", false);
            //assertThat(test.peek().getMid()).isEqualTo("mid_4");
            assertThat(test.peekNext()).isEmpty();
            test(test.next(), "2021-03-01T11:02:00Z", "mid_4", false);

            assertThat(test.hasNext()).isFalse();
            assertThatThrownBy(test::next).isInstanceOf(NoSuchElementException.class);
        }
    }


    @Test
    public void withCurrentProfile() throws Exception {

        try(MarkSkippedChangeIterator test = MarkSkippedChangeIterator.builder()
            .iterator(fourChanges.iterator())
            .since(INSTANT)
            .profile(even)
            .logBatch(2)
            .build()) {
            test(test.next(), "2021-03-01T11:01:00Z", "mid_1", true);
            test(test.next(), "2021-03-01T11:01:00Z", "mid_2", true);
            test(test.next(), "2021-03-01T11:02:00Z", "mid_3", true);
            test(test.next(), "2021-03-01T11:02:00Z", "mid_4", false);

            assertThat(test.hasNext()).isFalse();
        }
    }

    @Test
    public void sinceTooLate() throws Exception {
        try(MarkSkippedChangeIterator test = MarkSkippedChangeIterator.builder()
            .iterator(fourChanges.iterator())
            .since(INSTANT.plus(Duration.ofHours(1)))
            .profile(even)
            //.keepAliveNull(3L)
            .logBatch(2)
            .build()) {
            //  mid_3 is not currently in the profile it since is before requested one
            // so we are supposing that a delete must be issued.
            assertThat(test.next().isSkipped()).isTrue();
            assertThat(test.next().isSkipped()).isTrue();
            test(test.next(), "2021-03-01T11:02:00Z", "mid_3", true);
            assertThat(test.next().isSkipped()).isTrue();
            assertThat(test.hasNext()).isFalse();
        }
    }

    @Test
    public void withCurrentAndPreviousProfile() throws Exception {

        try(MarkSkippedChangeIterator test = MarkSkippedChangeIterator.builder()
            .iterator(fourChanges.iterator())
            .since(INSTANT)
            .profile(even)
            .build()) {
            assertThat(test.next().isSkipped()).isTrue();
            test(test.next(), "2021-03-01T11:01:00Z", "mid_2", true);
            test(test.next(), "2021-03-01T11:02:00Z", "mid_3", true);
            test(test.next(), "2021-03-01T11:02:00Z", "mid_4", false);

            assertThat(test.hasNext()).isFalse();
        }
    }

    @Test
    public void keepAlive() throws Exception {
        try(MarkSkippedChangeIterator test = MarkSkippedChangeIterator.builder()
            .iterator(asList(
                change(1, 1) // not in profile
                    .deleted(true)
                    .build(),
                change(3, 1) // not in profile
                    .deleted(true)
                    .build(),
                change(5, 2) // not in profile
                    .deleted(true)
                    .build(),
                change(7, 2) // not in profile, but also not deleted, so
                    .deleted(false)
                    .build()
            ).iterator())
            .since(INSTANT)
            .profile(even)
            .build()) {
            assertThat(test.next().isSkipped()).isTrue();
            assertThat(test.next().isSkipped()).isTrue();
            assertThat(test.getDeletesSkipped()).isEqualTo(2);
            assertThat(test.next().isSkipped()).isTrue();
            test(test.next(), "2021-03-01T11:02:00Z", "mid_7", true);

            assertThat(test.hasNext()).isFalse();
        }
    }

    private void test(MediaChange c, String change, String mid, boolean deleted) {
        assertThat(c.getMid()).isEqualTo(mid);
        assertThat(c.getPublishDate()).isEqualTo(change);
        assertThat(c.isDeleted()).isEqualTo(deleted);
    }

    @SuppressWarnings("SameParameterValue")
    static Instant of(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute).atZone(Schedule.ZONE_ID).toInstant();
    }

    /**
     * Returns a change with mid {@code mid_<number>}}, and publish date {@code minute}s after 2021-03-01T12:00.
     */
    MediaChange.Builder change(int number, int minute) {
        return MediaChange.builder()
            .publishDate(INSTANT.plus(Duration.ofMinutes(minute)))
            .mid("mid_" + number)
            .media(
                clip()
                    .id((long)number)
                    .workflow(Workflow.PUBLISHED)
                    .mid("mid_" + number)
                    .build()
            );
    }

}
