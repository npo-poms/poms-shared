package nl.vpro.domain.api.media;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import nl.vpro.domain.api.MediaChange;
import nl.vpro.domain.api.profile.ProfileDefinition;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.Workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Michiel Meeuwissen
 */
public class ChangeIteratorTest {


    @SuppressWarnings("unchecked")
    private final ProfileDefinition<MediaObject> current = mock(ProfileDefinition.class);

    @SuppressWarnings("unchecked")
    private final ProfileDefinition<MediaObject> previous = mock(ProfileDefinition.class);

    {
        Answer<Boolean> idIsEven = new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                MediaObject mo = invocationOnMock.getArgument(0);
                return mo.getId() % 2 == 0;
            }
        };
        when(current.test(any(MediaObject.class))).thenAnswer(idIsEven);
        when(previous.test(any(MediaObject.class))).thenAnswer(idIsEven);
    }


    @Test
    public void basicWithoutProfile() throws Exception {

        try(ChangeIterator test = ChangeIterator.builder()
            .iterator(Arrays.asList(
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
                    .build()
            ).iterator())
            .since(of(2021, 3, 1, 12, 0))
            .current(null)
            .previous(null)
            .logBatch(2)
            .build()) {
            assertThat(test.next().toString()).isEqualTo("2021-03-01T11:01:00Z:mid_1:Program{CLIP mid=\"mid_1\", title=<no title>}:DELETED");

            assertThat(test.next().toString()).isEqualTo("2021-03-01T11:01:00Z:mid_2:Program{CLIP mid=\"mid_2\", title=<no title>}:DELETED");
            assertThat(test.next().toString()).isEqualTo("2021-03-01T11:02:00Z:mid_3:Program{CLIP mid=\"mid_3\", title=<no title>}");
            assertThat(test.next().toString()).isEqualTo("2021-03-01T11:02:00Z:mid_4:Program{CLIP mid=\"mid_4\", title=<no title>}");

            assertThat(test.hasNext()).isFalse();
        }
    }

    @SuppressWarnings("SameParameterValue")
    Instant of(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute).atZone(Schedule.ZONE_ID).toInstant();
    }

    MediaChange.Builder change(int number, int minute) {
        return  MediaChange.builder()
                    .publishDate(of(2021, 3, 1, 12, minute))
                    .mid("mid_" + number)
                    .media(MediaBuilder.clip().id((long)number).workflow(Workflow.PUBLISHED).mid("mid_" + number).build());
    }

}
