package nl.vpro.domain.media;

import org.junit.Test;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import static nl.vpro.domain.media.support.OwnerType.BROADCASTER;
import static nl.vpro.domain.media.support.OwnerType.NPO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class GeoNameTest {


    @Test
    public void testAddGeoName() {
        //given a program with GeoNames
        GeoNames geoNames1 = geoNames1();

        GeoNames geoNames2 = geoNames2();

        Program program = MediaBuilder.program()
                .geoNames(geoNames1)
                .build();

        assertThat(program).isNotNull();
        assertThat(geoNames1).isNotNull();
        assertThat(geoNames2).isNotNull();

        //when I add intentions
        MediaObject newProgram = program.addGeoNames(geoNames1);
        newProgram = newProgram.addGeoNames(geoNames2);

        //I expect to find them
        assertThat(newProgram.getGeoNames()).contains(geoNames1);
        assertThat(newProgram.getGeoNames()).contains(geoNames2);

    }

    @Test
    public void testAddDuplicateOwnerGeoName() {
        //given a program with no Names
        GeoNames geoNames1 = geoNames1();
        GeoNames geoNames3 = geoNames3();

        Program program = MediaBuilder.program().build();

        assertThat(program).isNotNull();
        assertThat(geoNames1).isNotNull();
        assertThat(geoNames3).isNotNull();

        //when I add something with same owner
        MediaObject newProgram = program.addGeoNames(geoNames1);
        newProgram = newProgram.addGeoNames(geoNames3);

        //I expect the second item to override the previous one
        assertThat(newProgram.getGeoNames()).doesNotContain(geoNames1);
        assertThat(newProgram.getGeoNames()).contains(geoNames3);

    }

    @Test
    public void testAddSetWitDuplicateOwnerGeoNames() {
        //given a program
        GeoNames geoNames1 = geoNames1();
        GeoNames geoNames3 = geoNames3();

        Program program = MediaBuilder.program().build();

        assertThat(program).isNotNull();
        assertThat(geoNames1).isNotNull();
        assertThat(geoNames3).isNotNull();

        //when I set geoNames with duplicate owner I expect an exception to be raise
        SortedSet<GeoNames> newGeoNames = new TreeSet<>(Arrays.asList(geoNames1, geoNames3));

        assertThatIllegalArgumentException().isThrownBy(() -> {
            program.setGeoNames(newGeoNames);
        });
    }

    @Test
    public void testRemoveGeoNames() {
        //given a program with Intentions
        GeoNames geoNames1 = geoNames1();
        GeoNames geoNames2 = geoNames2();
        GeoNames geoNames3 = geoNames3();
        Program program = MediaBuilder.program()
                .geoNames(geoNames1, geoNames2, geoNames3)
                .build();

        System.out.println("Program:" + program);

        //when I remove an intention
        program.removeGeoNames(geoNames1);

        //I expect not to find it anymore
        assertThat(program.geoNames).doesNotContain(geoNames1);

    }

    private GeoNames geoNames1() {
        return GeoNames.builder()
                .owner(BROADCASTER).values(
                        Arrays.asList(
                                GeoName.builder().name("Africa").relationType(GeoRelationType.SUBJECT)
                                        .description("Continent").build()
                        )
                )
                .build();
    }

    private GeoNames geoNames2() {
        return GeoNames.builder()
                .owner(BROADCASTER).values(
                        Arrays.asList(
                                GeoName.builder().name("Africa").relationType(GeoRelationType.SUBJECT)
                                        .description("Continent").build()
                        )
                )
                .build();
    }

    private GeoNames geoNames3() {
        return GeoNames.builder()
                .owner(BROADCASTER).values(
                        Arrays.asList(
                                GeoName.builder().name("England").relationType(GeoRelationType.RECORDED_IN)
                                        .gtaaUri("https://wikipedia/lll").build()
                        )
                )
                .build();
    }
}
