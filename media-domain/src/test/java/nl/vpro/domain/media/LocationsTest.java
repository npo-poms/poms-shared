package nl.vpro.domain.media;

import java.util.HashSet;

import org.junit.Test;

import nl.vpro.domain.media.support.OwnerType;

import static nl.vpro.domain.media.StreamingStatus.unset;
import static nl.vpro.domain.media.StreamingStatus.withDrm;
import static nl.vpro.domain.media.StreamingStatus.withoutDrm;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class LocationsTest {


    @Test
    public void realizeDRM() {
        Program program = new Program();
        program.setMid("mid_1234");
        program.setStreamingPlatformStatus(withDrm(unset()));

        Locations.realize(program, Platform.INTERNETVOD, "nep", OwnerType.BROADCASTER, new HashSet<>());

        assertThat(program.getStreamingPlatformStatus()).isEqualTo(withDrm(unset()));
        assertThat(program.getLocations()).isNotEmpty();
        assertThat(program.getLocations().first().getProgramUrl()).isEqualTo("npo+drm://internetvod.omroep.nl/mid_1234");
        assertThat(program.getPrediction(Platform.INTERNETVOD).getState()).isEqualTo(Prediction.State.REALIZED);
    }

    @Test
    public void realizeNODRM() {
        Program program = new Program();
        program.setMid("mid_1234");
        program.setStreamingPlatformStatus(withoutDrm(unset()));
        Locations.realize(program, Platform.PLUSVOD, "nep", OwnerType.BROADCASTER, new HashSet<>());

        assertThat(program.getStreamingPlatformStatus()).isEqualTo(withoutDrm(unset()));
        assertThat(program.getLocations()).isNotEmpty();
        assertThat(program.getLocations().first().getProgramUrl()).isEqualTo("npo://plusvod.omroep.nl/mid_1234");
        assertThat(program.getPrediction(Platform.PLUSVOD).getState()).isEqualTo(Prediction.State.REALIZED);
    }

}
