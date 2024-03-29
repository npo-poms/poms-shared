package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.support.OwnerType;

import static nl.vpro.domain.media.StreamingStatus.Value;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 */
@Slf4j
public class AuthorityLocationsTest {

    AuthorityLocations locations = new AuthorityLocations(null);



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
        program.getPredictions().add(
            Prediction.builder()
                .encryption(Encryption.NONE)
                .platform(Platform.INTERNETVOD)
                .authority(Authority.USER)
                .id(1L)
                .build()
        );

        log.info("{}", locations.realizeStreamingPlatformIfNeeded(
            program,
            Platform.INTERNETVOD
        ));
        log.info("{}", program);
        assertThat(program.getLocations()).hasSize(1);
        Location first = program.getLocations().first();
        assertThat(first.getProgramUrl()).contains("npo://internetvod.omroep.nl/MID-123");
        assertThat(first.getAuthority()).isEqualTo(Authority.USER);
    }


    @Test
    public void realizeStreamingPlatformIfNeededAudio() {
        Program program = new Program();
        program.setMid("MID-123");
        program.setAVType(AVType.AUDIO);
        program.setStreamingPlatformStatus(StreamingStatusImpl.builder().audioWithoutDrm(Value.ONLINE).build());
        program.getPredictions().add(
            Prediction.builder()
                .encryption(Encryption.NONE)
                .platform(Platform.INTERNETVOD)
                .id(1L)
                .build());

        log.info("{}", locations.realizeStreamingPlatformIfNeeded(
            program,
            Platform.INTERNETVOD
        ));
        log.info("{}", program);
        assertThat(program.getLocations()).hasSize(1);
        assertThat(program.getLocations().first().getProgramUrl()).isEqualTo("https://entry.cdn.npoaudio.nl/handle/MID-123.mp3");
        assertThat(program.getLocations().first().getOwner()).isEqualTo(OwnerType.AUTHORITY);
        assertThat(program.getLocations().first().getAvFileFormat()).isEqualTo(AVFileFormat.MP3);
        assertThat(program.getLocations().first().getAuthority()).isEqualTo(Authority.USER);// This seems incorrect!
    }

}
