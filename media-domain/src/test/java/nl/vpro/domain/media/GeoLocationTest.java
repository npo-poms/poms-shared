package nl.vpro.domain.media;

import org.junit.Test;

import nl.vpro.domain.media.support.MediaObjectOwnableLists;

import static nl.vpro.domain.media.support.OwnerType.BROADCASTER;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * TODO This class does not test GeoLocations but mainly {@link MediaObjectOwnableLists}
 */
public class GeoLocationTest {


    @Test
    public void testAddGeoLocation() {
        //given a program with GeoLocations
        GeoLocations geoLocations1 = geoLocations1();
        GeoLocations geoLocations2 = geoLocations2();

        Program program = MediaBuilder.program()
                .geoLocations(geoLocations1)
                .build();


        //when I add GeoLocations
        MediaObjectOwnableLists.addOrUpdateOwnableList(program, program.getGeoLocations(), geoLocations1);
        MediaObjectOwnableLists.addOrUpdateOwnableList(program, program.getGeoLocations(), geoLocations2);

        //I expect to find them
        assertThat(program.getGeoLocations()).contains(geoLocations1);
        assertThat(program.getGeoLocations()).contains(geoLocations2);

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
        program.getGeoLocations().remove(geoLocations1);

        //I expect not to find it anymore
        assertThat(program.geoLocations).doesNotContain(geoLocations1);

    }

    private GeoLocations geoLocations1() {
        return GeoLocations.builder()
                .owner(BROADCASTER).value(
                GeoLocation.builder().name("Africa").scopeNote("Continent")
                        .gtaaUri("test/123").role(GeoRoleType.SUBJECT).build()
            )
            .build();
    }

    private GeoLocations geoLocations2() {
        return GeoLocations.builder()
            .owner(BROADCASTER)
            .value(
                GeoLocation.builder().name("Africa").scopeNote("Continent").gtaaUri("test/123")
                        .role(GeoRoleType.SUBJECT).build()
            )
            .build();
    }

    private GeoLocations geoLocations3() {
        return GeoLocations.builder()
            .owner(BROADCASTER).value(
                GeoLocation.builder().name("England").gtaaUri("https://wikipedia/lll")
                        .role(GeoRoleType.RECORDED_IN).build()

            )
            .build();
    }
}
