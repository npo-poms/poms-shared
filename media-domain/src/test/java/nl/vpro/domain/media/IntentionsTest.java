package nl.vpro.domain.media;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import static nl.vpro.domain.media.support.OwnerType.BROADCASTER;
import static nl.vpro.domain.media.support.OwnerType.NPO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class IntentionsTest {


    @Test
    public void testAddIntention() {
        //given a program with Intentions
        Intentions intentions1 = Intentions.builder()
                .owner(BROADCASTER).values(Arrays.asList(
                        IntentionType.ENTERTAINMENT_INFORMATIVE,
                        IntentionType.INFORM_INDEPTH))
                .build();
        Intentions intentions2 = Intentions.builder()
                .owner(NPO).values(Arrays.asList(
                        IntentionType.INFORM_INDEPTH))
                .build();
        Program program = MediaBuilder.program().intentions(intentions1).build();

        assertThat(program).isNotNull();
        assertThat((Object) intentions1).isNotNull();
        assertThat((Object) intentions2).isNotNull();

        //when I add intentions
        MediaObject newProgram = program.addIntention(intentions1);
        newProgram = newProgram.addIntention(intentions2);

        //I expect to find them
        assertThat(newProgram.getIntentions()).contains(intentions1);
        assertThat(newProgram.getIntentions()).contains(intentions2);

    }

    @Test
    public void testAddDuplicateOwnerIntention() {
        //given a program with no Intentions
        Intentions intentions1 = Intentions.builder().owner(BROADCASTER).values(Arrays.asList(IntentionType.ENTERTAINMENT_INFORMATIVE)).build();
        Intentions intentions2 = Intentions.builder().owner(BROADCASTER).values(Arrays.asList(IntentionType.INFORM_INDEPTH)).build();
        Program program = MediaBuilder.program().intentions().build();

        assertThat(program).isNotNull();
        assertThat((Object) intentions1).isNotNull();
        assertThat((Object) intentions2).isNotNull();

        //when I add intentions with same owner
        MediaObject newProgram = program.addIntention(intentions1);
        newProgram = newProgram.addIntention(intentions2);

        //I expect the second intention to override the previous one
        assertThat(newProgram.getIntentions()).doesNotContain(intentions1);
        assertThat(newProgram.getIntentions()).contains(intentions2);

    }

    @Test
    public void testAddSetWitDuplicateOwnerIntention() {
        //given a program
        Intentions intentions1 = Intentions.builder().owner(BROADCASTER)
                .values(Arrays.asList(IntentionType.ENTERTAINMENT_INFORMATIVE)).build();

        Intentions intentions2 = Intentions.builder().owner(BROADCASTER)
                .values(Arrays.asList(IntentionType.INFORM_INDEPTH)).build();

        Program program = MediaBuilder.program().build();

        assertThat(program).isNotNull();
        assertThat((Object) intentions1).isNotNull();
        assertThat((Object) intentions2).isNotNull();

        //when I set intentions with duplicate owner I expect an exception to be raise
        SortedSet<Intentions> newIntentions = new TreeSet<>(Arrays.asList(intentions1, intentions2));

        assertThatIllegalArgumentException().isThrownBy(() -> {
            program.setIntentions(newIntentions);
        });
    }

    @Test
    public void testRemoveIntention() {
        //given a program with Intentions
        Intentions intentions1 = Intentions.builder().owner(BROADCASTER).values(Arrays.asList(IntentionType.ENTERTAINMENT_INFORMATIVE)).build();
        Intentions intentions2 = Intentions.builder().owner(BROADCASTER).values(Arrays.asList(IntentionType.INFORM_INDEPTH)).build();
        Intentions intentions3 = Intentions.builder().owner(NPO).values(Arrays.asList(IntentionType.INFORM_INDEPTH)).build();

        Program program = MediaBuilder.program()
                .intentions(intentions1, intentions2, intentions3)
                .build();

        System.out.println("Program:" + program);

        //when I remove an intention
        program.removeIntention(intentions1);

        //I expect not to find it anymore
        assertThat(program.intentions).doesNotContain(intentions1);

    }

}
