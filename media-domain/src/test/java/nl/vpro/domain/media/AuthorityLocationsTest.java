package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import nl.vpro.domain.media.support.OwnerType;

import static nl.vpro.domain.media.StreamingStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 */
@Slf4j
public class AuthorityLocationsTest {

    AuthorityLocations locations = new AuthorityLocations(null);


    @Test
    public void realizeDRM() {
        Program program = new Program();
        program.setMid("mid_1234");
        program.setStreamingPlatformStatus(withDrm(unset()));

        locations.realize(program, Platform.INTERNETVOD, "nep", OwnerType.BROADCASTER, new HashSet<>());

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
        locations.realize(program, Platform.PLUSVOD, "nep", OwnerType.BROADCASTER, new HashSet<>());

        assertThat(program.getStreamingPlatformStatus()).isEqualTo(withoutDrm(unset()));
        assertThat(program.getLocations()).isNotEmpty();
        assertThat(program.getLocations().first().getProgramUrl()).isEqualTo("npo://plusvod.omroep.nl/mid_1234");
        assertThat(program.getPrediction(Platform.PLUSVOD).getState()).isEqualTo(Prediction.State.REALIZED);
    }


    @Test
    public void createWebOnlyPredictionIfNeeded() {
        Program program = new Program();
        program.setMid("mid_1234");
        Instant stop1 = LocalDateTime.of(2018, 4, 19, 16, 42).atZone(Schedule.ZONE_ID).toInstant();
        Instant stop2 = LocalDateTime.of(2017, 4, 19, 16, 42).atZone(Schedule.ZONE_ID).toInstant();
        program.getLocations().add(Location.builder().owner(OwnerType.BROADCASTER).programUrl("http://www.vpro.nl/1").platform(Platform.INTERNETVOD).publishStop(stop2).build());
        program.getLocations().add(Location.builder().owner(OwnerType.BROADCASTER).programUrl("http://www.vpro.nl/2").platform(null).publishStop(stop1).build());
        program.getLocations().add(Location.builder().owner(OwnerType.BROADCASTER).programUrl("http://www.vpro.nl/3").platform(Platform.PLUSVOD).publishStop((Instant) null).build());

        locations.createWebOnlyPredictionIfNeeded(program);

        Prediction prediction = program.getPrediction(Platform.INTERNETVOD);

        assertThat(prediction).isNotNull();
        assertThat(prediction.getPublishStartInstant()).isNull();
        assertThat(prediction.getPublishStopInstant()).isEqualTo(stop1);
    }


    @Test
    @Disabled("Fails but no time yet to fix.")
    public void createWebOnlyPredictionIfNeeded2() {
        Program program = new Program();
        program.setMid("mid_1234");
        Instant stop1 = LocalDateTime.of(2018, 4, 19, 16, 42).atZone(Schedule.ZONE_ID).toInstant();
        Instant stop2 = LocalDateTime.of(2017, 4, 19, 16, 42).atZone(Schedule.ZONE_ID).toInstant();
        program.addLocation(Location.builder().owner(OwnerType.BROADCASTER).programUrl("http://www.vpro.nl/1").platform(Platform.INTERNETVOD).publishStop(stop2).build());
        program.addLocation(Location.builder().owner(OwnerType.BROADCASTER).programUrl("http://www.vpro.nl/2").platform(null).publishStop(stop1).build());
        program.addLocation(Location.builder().owner(OwnerType.BROADCASTER).programUrl("http://www.vpro.nl/3").platform(Platform.PLUSVOD).publishStop((Instant) null).build());

        locations.createWebOnlyPredictionIfNeeded(program);

        Prediction prediction = program.getPrediction(Platform.INTERNETVOD);

        assertThat(prediction).isNotNull();
        assertThat(prediction.getPublishStartInstant()).isNull();
        assertThat(prediction.getPublishStopInstant()).isEqualTo(stop1);
    }


    @Test
    public void realizeStreamingPlatformIfNeededVideo() {
        Program program = new Program();
        program.setMid("MID-123");
        program.setAVType(AVType.VIDEO);
        program.setStreamingPlatformStatus(StreamingStatusImpl.builder().withoutDrm(Value.ONLINE).build());
        program.getPredictions().add(Prediction.builder().encryption(Encryption.NONE).platform(Platform.INTERNETVOD).build());

        log.info("{}", locations.realizeStreamingPlatformIfNeeded(
            program,
            Platform.INTERNETVOD,
            (l) -> true,
            Instant.now()
        ));
        log.info("{}", program);
        assertThat(program.getLocations().stream().map(Location::getProgramUrl)).contains("npo://internetvod.omroep.nl/MID-123");
    }


    @Test
    public void realizeStreamingPlatformIfNeededAudio() {
        Program program = new Program();
        program.setMid("MID-123");
        program.setAVType(AVType.AUDIO);
        program.setStreamingPlatformStatus(StreamingStatusImpl.builder().withoutDrm(Value.ONLINE).build());
        program.getPredictions().add(Prediction.builder().encryption(Encryption.NONE).platform(Platform.INTERNETVOD).build());

        log.info("{}", locations.realizeStreamingPlatformIfNeeded(
            program,
            Platform.INTERNETAOD,
            (l) -> true,
            Instant.now()
        ));
        log.info("{}", program);
        assertThat(program.getLocations().first().getProgramUrl()).isEqualTo("https://entry.cdn.npoaudio.nl/handle/MID-123.mp3");
        assertThat(program.getLocations().first().getOwner()).isEqualTo(OwnerType.AUTHORITY);
        assertThat(program.getLocations().first().getAvFileFormat()).isEqualTo(AVFileFormat.MP3);
    }


    @ParameterizedTest
    @CsvSource(textBlock = """
        https://entry.cdn.npoaudio.nl/handle/WO_VPRO_20039564.mp3,  0
        """)
    public void size(String url, long size) {
        assertThat(AuthorityLocations.getBytesize(url).getAsLong()).isEqualTo(157808848L);
    }
}
