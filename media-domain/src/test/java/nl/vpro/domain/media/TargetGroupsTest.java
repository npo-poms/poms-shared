package nl.vpro.domain.media;

import org.junit.Test;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import static nl.vpro.domain.media.support.OwnerType.BROADCASTER;
import static nl.vpro.domain.media.support.OwnerType.NPO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class TargetGroupsTest {


    @Test
    public void testAddTargetGroups() {
        //given a program with no TargetGroups
        TargetGroups targetGroups1 = TargetGroups.builder()
                .owner(BROADCASTER)
                .values(Arrays.asList(
                        new TargetGroup(TargetGroupType.KIDS_12),
                        new TargetGroup(TargetGroupType.ADULTS)))
                .build();
        TargetGroups targetGroups2 = TargetGroups.builder()
                .owner(NPO)
                .values(Arrays.asList(
                        new TargetGroup(TargetGroupType.ADULTS)))
                .build();
        Program program = MediaBuilder.program().targetGroups().build();

        assertThat(program).isNotNull();
        assertThat(targetGroups1).isNotNull();
        assertThat(targetGroups2).isNotNull();

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
                .values(Arrays.asList(
                        new TargetGroup(TargetGroupType.KIDS_12),
                        new TargetGroup(TargetGroupType.ADULTS)))
                .build();
        TargetGroups targetGroups2 = TargetGroups.builder()
                .owner(BROADCASTER)
                .values(Arrays.asList(
                        new TargetGroup(TargetGroupType.ADULTS)))
                .build();
        Program program = MediaBuilder.program().targetGroups().build();

        assertThat(program).isNotNull();
        assertThat(targetGroups1).isNotNull();
        assertThat(targetGroups2).isNotNull();

        //when I add targetGroups with same owner
        MediaObject newProgram = program.addTargetGroups(targetGroups1);
        newProgram = newProgram.addTargetGroups(targetGroups2);

        //I expect the second intention to override the previous one
        assertThat(newProgram.getTargetGroups()).doesNotContain(targetGroups1);
        assertThat(newProgram.getTargetGroups()).contains(targetGroups2);

    }

}
