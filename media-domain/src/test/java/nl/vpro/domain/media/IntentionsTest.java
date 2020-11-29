package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.support.MediaObjectOwnableLists;

import static nl.vpro.domain.media.IntentionType.ENTERTAINMENT_INFORMATIVE;
import static nl.vpro.domain.media.IntentionType.INFORM_INDEPTH;
import static nl.vpro.domain.media.support.OwnerType.BROADCASTER;
import static nl.vpro.domain.media.support.OwnerType.NPO;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *  TODO This class does not test GeoLocations but mainly {@link MediaObjectOwnableLists}
 */
@Slf4j
public class IntentionsTest {


    @Test
    public void testAddSetWitDuplicateOwnerIntention() {
        //given a program
        Intentions intentions1 = Intentions.builder().owner(BROADCASTER)
                .values(Arrays.asList(ENTERTAINMENT_INFORMATIVE)).build();

        Intentions intentions2 = Intentions.builder().owner(BROADCASTER)
                .values(Arrays.asList(INFORM_INDEPTH)).build();

        Program program = MediaBuilder.program().build();

        assertThat(program).isNotNull();
        assertThat((Object) intentions1).isNotNull();
        assertThat((Object) intentions2).isNotNull();

        //when I set intentions with duplicate owner I expect an exception to be raise
        SortedSet<Intentions> newIntentions = new TreeSet<>(Arrays.asList(intentions1, intentions2));

        assertThat(newIntentions).hasSize(1); // The two intensions are equals

        program.setIntentions(newIntentions);

        assertThat(program.getIntentions().first().getValues()).containsExactly(new Intention(ENTERTAINMENT_INFORMATIVE));

    }

    @Test
    public void testRemoveIntention() {
        //given a program with Intentions
        Intentions intentions1 = Intentions.builder().owner(BROADCASTER).values(Arrays.asList(ENTERTAINMENT_INFORMATIVE)).build();
        Intentions intentions2 = Intentions.builder().owner(BROADCASTER).values(Arrays.asList(INFORM_INDEPTH)).build();
        Intentions intentions3 = Intentions.builder().owner(NPO).values(Arrays.asList(INFORM_INDEPTH)).build();

        Program program = MediaBuilder.program()
                .intentions(intentions1, intentions2, intentions3)
                .build();

        log.info("Program:" + program);
        assertThat(program.getIntentions()).hasSize(2); // BROADCASTER was duplicate

        //when I remove an intention
        program.getIntentions().remove(intentions1);

        //I expect not to find it anymore
        assertThat(program.intentions).doesNotContain(intentions1);



    }

}
