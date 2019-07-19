package nl.vpro.domain.media;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import static nl.vpro.domain.media.support.OwnerType.BROADCASTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class GeoLocationTest {


    @Test
    public void testAddGeoLocation() {
        //given a program with GeoLocations
        GeoLocations geoLocations1 = geoLocations1();

        GeoLocations geoLocations2 = geoLocations2();

        Program program = MediaBuilder.program()
                .geoLocations(geoLocations1)
                .build();

        assertThat(program).isNotNull();
        assertThat(geoLocations1).isNotNull();
        assertThat(geoLocations2).isNotNull();

        //when I add GeoLocations
        MediaObject newProgram = program.addGeoLocations(geoLocations1);
        newProgram = newProgram.addGeoLocations(geoLocations2);

        //I expect to find them
        assertThat(newProgram.getGeoLocations()).contains(geoLocations1);
        assertThat(newProgram.getGeoLocations()).contains(geoLocations2);

    }

    @Test
    public void testAddDuplicateOwnerGeoLocation() {
        //given a program with no Names
        GeoLocations geoLocations1 = geoLocations1();
        GeoLocations geoLocations3 = geoLocations3();

        Program program = MediaBuilder.program().build();

        assertThat(program).isNotNull();
        assertThat(geoLocations1).isNotNull();
        assertThat(geoLocations3).isNotNull();

        //when I add something with same owner
        MediaObject newProgram = program.addGeoLocations(geoLocations1);
        newProgram = newProgram.addGeoLocations(geoLocations3);

        //I expect the second item to override the previous one
        assertThat(newProgram.getGeoLocations()).doesNotContain(geoLocations1);
        assertThat(newProgram.getGeoLocations()).contains(geoLocations3);

    }

    @Test
    public void testAddSetWitDuplicateOwnerGeoLocations() {
        //given a program
        GeoLocations geoLocations1 = geoLocations1();
        GeoLocations geoLocations3 = geoLocations3();

        Program program = MediaBuilder.program().build();

        assertThat(program).isNotNull();
        assertThat(geoLocations1).isNotNull();
        assertThat(geoLocations3).isNotNull();

        //when I set geoLocations with duplicate owner I expect an exception to be raise
        SortedSet<GeoLocations> newGeoLocations = new TreeSet<>(Arrays.asList(geoLocations1, geoLocations3));

        assertThatIllegalArgumentException().isThrownBy(() -> {
            program.setGeoLocations(newGeoLocations);
        });
    }

    @Test
    public void testRemoveGeoLocations() {
        //given a program with Intentions
        GeoLocations geoLocations1 = geoLocations1();
        GeoLocations geoLocations2 = geoLocations2();
        GeoLocations geoLocations3 = geoLocations3();
        Program program = MediaBuilder.program()
                .geoLocations(geoLocations1, geoLocations2, geoLocations3)
                .build();

        System.out.println("Program:" + program);

        //when I remove an intention
        program.removeGeoLocations(geoLocations1);

        //I expect not to find it anymore
        assertThat(program.geoLocations).doesNotContain(geoLocations1);

    }

    private GeoLocations geoLocations1() {
        return GeoLocations.builder()
                .owner(BROADCASTER).values(
                        Arrays.asList(
                                GeoLocation.builder().name("Africa").role(GeoRoleType.SUBJECT)
                                        .description("Continent").build()
                        )
                )
                .build();
    }

    private GeoLocations geoLocations2() {
        return GeoLocations.builder()
                .owner(BROADCASTER).values(
                        Arrays.asList(
                                GeoLocation.builder().name("Africa").role(GeoRoleType.SUBJECT)
                                        .description("Continent").build()
                        )
                )
                .build();
    }

    private GeoLocations geoLocations3() {
        return GeoLocations.builder()
                .owner(BROADCASTER).values(
                        Arrays.asList(
                                GeoLocation.builder().name("England").role(GeoRoleType.RECORDED_IN)
                                        .gtaaUri("https://wikipedia/lll").build()
                        )
                )
                .build();
    }
}
