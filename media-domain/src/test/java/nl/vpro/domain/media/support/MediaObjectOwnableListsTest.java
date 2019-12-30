package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.Test;

import nl.vpro.domain.media.*;

import static nl.vpro.domain.media.support.OwnerType.BROADCASTER;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MediaObjectOwnableListsTest {

    /**
     * The values for the Owner with higher rank should be used for expansion
     */
    @Test
    public void expandOwnedLists() {

        List<GeoLocation> geoLocation1 = Arrays.asList(
                GeoLocation.builder().name("Amsterdam").scopeNote("City").gtaaUri("test/123").role(GeoRoleType.RECORDED_IN).build()
        );
        List<GeoLocation> geoLocation2 = Arrays.asList(
                GeoLocation.builder().name("Utrecht").scopeNote("City").gtaaUri("test/123").role(GeoRoleType.RECORDED_IN).build()
        );
        GeoLocations g1 = GeoLocations.builder().owner(OwnerType.MIS).values(geoLocation1).build();
        GeoLocations g2 = GeoLocations.builder().owner(OwnerType.WHATS_ON).values(geoLocation2).build();
        SortedSet<GeoLocations> set = new TreeSet<>(Arrays.asList(g2, g1));

        final SortedSet<GeoLocations> result = MediaObjectOwnableLists.expandOwnedList(set,
                (owner, values) -> GeoLocations.builder().values(values).owner(owner).build(),
                OwnerType.ENTRIES
        );
        assertThat(result.size()).isEqualTo(4);
        assertThat(result.stream().map(v -> v.getOwner() + ":" + v.getValues().get(0).getName()).collect(Collectors.toList()))
                .isEqualTo(Arrays.asList("BROADCASTER:Amsterdam", "NPO:Amsterdam", "MIS:Amsterdam", "WHATS_ON:Utrecht"));

        for (GeoLocations value : result) {
            log.info(value.toString());
        }
    }

    @Test
    public void removeGeoLocations() {
        Program program = MediaBuilder.program().mid("VPRO-1").titles(Title.main("Test 1")).build();
        List<GeoLocation> geoLocation1 = Arrays.asList(
                GeoLocation.builder().id(1L).name("Amsterdam").scopeNote("City").gtaaUri("test/123").role(GeoRoleType.RECORDED_IN).build()
        );
        List<GeoLocation> geoLocation2 = Arrays.asList(
                GeoLocation.builder().id(2L).name("Utrecht").scopeNote("City").gtaaUri("test/123").role(GeoRoleType.RECORDED_IN).build()
        );
        GeoLocations g1 = GeoLocations.builder().owner(OwnerType.MIS).values(geoLocation1).build();
        GeoLocations g2 = GeoLocations.builder().owner(OwnerType.WHATS_ON).values(geoLocation2).build();
        SortedSet<GeoLocations> set = new TreeSet<>(Arrays.asList(g2, g1));
        program.setGeoLocations(set);

        final SortedSet<GeoLocations> programGeoLocations = program.getGeoLocations();
        assertThat(programGeoLocations.size()).isEqualTo(2);

        MediaObjectOwnableLists.remove(programGeoLocations, OwnerType.MIS);

        assertThat(programGeoLocations.size()).isEqualTo(1);
    }

    @Test
    public void addOrUpdateOwnableList() {
        Program program = MediaBuilder.program().build();


        GeoLocations geoLocations1 = GeoLocations.builder()
                .owner(BROADCASTER).value(
                GeoLocation.builder().name("Africa").scopeNote("Continent")
                        .gtaaUri("test/123").role(GeoRoleType.SUBJECT).build()
            )
            .build();
        GeoLocations geoLocations3 =  GeoLocations.builder()
            .owner(BROADCASTER).value(
                GeoLocation.builder().name("England").gtaaUri("https://wikipedia/lll")
                        .role(GeoRoleType.RECORDED_IN).build()

            )
            .build();


        program.getGeoLocations().add(geoLocations1);
        MediaObjectOwnableLists.addOrUpdateOwnableList(program, program.getGeoLocations(), geoLocations3);

        //I expect the second item to override the previous one
        assertThat(program.getGeoLocations()).hasSize(1);
        assertThat(program.getGeoLocations().first().getValues()).hasSize(1);
        assertThat(program.getGeoLocations().first().getValues().get(0).getGtaaUri()).isEqualTo(URI.create("https://wikipedia/lll"));

        // but actually the object should not have been changed
        assertThat(program.getGeoLocations().first() == geoLocations1).isTrue();
        assertThat(program.getGeoLocations().first() == geoLocations3).isFalse();

    }
}
