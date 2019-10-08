package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.Test;

import nl.vpro.domain.media.*;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MediaObjectOwnableListsTest {

    /**
     * The values for the Owner with higher rank should be used for expansion
     */
    @Test
    public void expandGeoLocation() {

        List<GeoLocation> geoLocation1 = Arrays.asList(
                GeoLocation.builder().name("Amsterdam").scopeNote("City").gtaaUri("test/123").role(GeoRoleType.RECORDED_IN).build()
        );
        List<GeoLocation> geoLocation2 = Arrays.asList(
                GeoLocation.builder().name("Utrecht").scopeNote("City").gtaaUri("test/123").role(GeoRoleType.RECORDED_IN).build()
        );
        GeoLocations g1 = GeoLocations.builder().owner(OwnerType.MIS).values(geoLocation1).build();
        GeoLocations g2 = GeoLocations.builder().owner(OwnerType.WHATS_ON).values(geoLocation2).build();
        SortedSet<GeoLocations> set = new TreeSet<>();
        set.add(g2);
        set.add(g1);


        final SortedSet<GeoLocations> result = MediaObjectOwnableLists.expandOwnedList(set,
                (owner, values) -> GeoLocations.builder().values(values).owner(owner).build(),
                OwnerType.ENTRIES
        );
        assertThat(result.size()).isEqualTo(4);
        assertThat(result.stream().map(v -> v.getOwner() + ":" + ((GeoLocation) v.getValues().get(0)).getName()).collect(Collectors.toList()))
                .isEqualTo(Arrays.asList("BROADCASTER:Amsterdam", "NPO:Amsterdam", "MIS:Amsterdam", "WHATS_ON:Utrecht"));

        for (GeoLocations value : result) {
            log.info(value.toString());
        }
    }

    @Test
    public <OL extends AbstractMediaObjectOwnableList> void removeGeoLocations() {
        Program program = MediaBuilder.program().mid("VPRO-1").titles(Title.main("Test 1")).build();
        List<GeoLocation> geoLocation1 = Arrays.asList(
                GeoLocation.builder().id(1L).name("Amsterdam").scopeNote("City").gtaaUri("test/123").role(GeoRoleType.RECORDED_IN).build()
        );
        List<GeoLocation> geoLocation2 = Arrays.asList(
                GeoLocation.builder().id(2L).name("Utrecht").scopeNote("City").gtaaUri("test/123").role(GeoRoleType.RECORDED_IN).build()
        );
        GeoLocations g1 = GeoLocations.builder().owner(OwnerType.MIS).values(geoLocation1).build();
        GeoLocations g2 = GeoLocations.builder().owner(OwnerType.WHATS_ON).values(geoLocation2).build();
        SortedSet<GeoLocations> set = new TreeSet<>();
        set.add(g2);
        set.add(g1);
        program.setGeoLocations(set);

        final SortedSet<GeoLocations> programGeoLocations = program.getGeoLocations();
        assertThat(programGeoLocations.size()).isEqualTo(2);

        final boolean result = MediaObjectOwnableLists.remove(programGeoLocations,
                OwnerType.MIS
        );

        assertThat(programGeoLocations.size()).isEqualTo(1);
    }
}
