package nl.vpro.domain.media;

import java.util.Arrays;

import org.junit.Test;

import static nl.vpro.domain.media.support.OwnerType.BROADCASTER;
import static nl.vpro.domain.media.support.OwnerType.NPO;
import static org.assertj.core.api.Assertions.assertThat;

public class TargetGroupsTest {


    @Test
    public void testAddTargetGroups() {
        //given a program with no TargetGroups
        TargetGroups targetGroups1 = TargetGroups.builder()
                .owner(BROADCASTER)
                .values(Arrays.asList(TargetGroupType.KIDS_12, TargetGroupType.ADULTS))
                .build();
        TargetGroups targetGroups2 = TargetGroups.builder()
                .owner(NPO)
                .values(Arrays.asList(TargetGroupType.ADULTS))
                .build();
        Program program = MediaBuilder.program().targetGroups().build();

        assertThat(program).isNotNull();
        assertThat((Object) targetGroups1).isNotNull();
        assertThat((Object) targetGroups2).isNotNull();

        //when I add targetGroups
        MediaObject newProgram = program.addTargetGroups(targetGroups1);
        newProgram = newProgram.addTargetGroups(targetGroups2);

        //I expect to find them
        assertThat(newProgram.getTargetGroups()).contains(targetGroups1);
        assertThat(newProgram.getTargetGroups()).contains(targetGroups2);

    }

    @Test
    public void testAddDuplicateOwnerIntention() {
        //given a program with no TargetGroups
        TargetGroups targetGroups1 = TargetGroups.builder()
                .owner(BROADCASTER)
                .values(Arrays.asList(TargetGroupType.KIDS_12, TargetGroupType.ADULTS))
                .build();
        TargetGroups targetGroups2 = TargetGroups.builder()
                .owner(BROADCASTER)
                .values(Arrays.asList(TargetGroupType.ADULTS))
                .build();
        Program program = MediaBuilder.program().targetGroups().build();

        assertThat(program).isNotNull();
        assertThat((Object) targetGroups1).isNotNull();
        assertThat((Object) targetGroups2).isNotNull();

        //when I add targetGroups with same owner
        MediaObject newProgram = program.addTargetGroups(targetGroups1);
        newProgram = newProgram.addTargetGroups(targetGroups2);

        //I expect the second intention to override the previous one
        assertThat(newProgram.getTargetGroups()).doesNotContain(targetGroups1);
        assertThat(newProgram.getTargetGroups()).contains(targetGroups2);

    }

}
