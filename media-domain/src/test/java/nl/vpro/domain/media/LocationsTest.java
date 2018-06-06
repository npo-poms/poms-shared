package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.logging.simple.StringBuilderSimpleLogger;

import static nl.vpro.domain.media.StreamingStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 */
@Slf4j
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


    @Test
    public void createWebOnlyPredictionIfNeeded() {
        Program program = new Program();
        program.setMid("mid_1234");
        Instant stop1 = LocalDateTime.of(2018, 4, 19, 16, 42).atZone(Schedule.ZONE_ID).toInstant();
        Instant stop2 = LocalDateTime.of(2017, 4, 19, 16, 42).atZone(Schedule.ZONE_ID).toInstant();
        program.getLocations().add(Location.builder().owner(OwnerType.BROADCASTER).programUrl("http://www.vpro.nl/1").platform(Platform.INTERNETVOD).publishStop(stop2).build());
        program.getLocations().add(Location.builder().owner(OwnerType.BROADCASTER).programUrl("http://www.vpro.nl/2").platform(null).publishStop(stop1).build());
        program.getLocations().add(Location.builder().owner(OwnerType.BROADCASTER).programUrl("http://www.vpro.nl/3").platform(Platform.PLUSVOD).publishStop((Instant) null).build());

        Locations.createWebOnlyPredictionIfNeeded(program);

        Prediction prediction = program.getPrediction(Platform.INTERNETVOD);

        assertThat(prediction).isNotNull();
        assertThat(prediction.getPublishStartInstant()).isNull();
        assertThat(prediction.getPublishStopInstant()).isEqualTo(stop1);
    }


    @Test
    @Ignore("Fails but now time yet to fix.")
    public void createWebOnlyPredictionIfNeeded2() {
        Program program = new Program();
        program.setMid("mid_1234");
        Instant stop1 = LocalDateTime.of(2018, 4, 19, 16, 42).atZone(Schedule.ZONE_ID).toInstant();
        Instant stop2 = LocalDateTime.of(2017, 4, 19, 16, 42).atZone(Schedule.ZONE_ID).toInstant();
        program.addLocation(Location.builder().owner(OwnerType.BROADCASTER).programUrl("http://www.vpro.nl/1").platform(Platform.INTERNETVOD).publishStop(stop2).build());
        program.addLocation(Location.builder().owner(OwnerType.BROADCASTER).programUrl("http://www.vpro.nl/2").platform(null).publishStop(stop1).build());
        program.addLocation(Location.builder().owner(OwnerType.BROADCASTER).programUrl("http://www.vpro.nl/3").platform(Platform.PLUSVOD).publishStop((Instant) null).build());

        Locations.createWebOnlyPredictionIfNeeded(program);

        Prediction prediction = program.getPrediction(Platform.INTERNETVOD);

        assertThat(prediction).isNotNull();
        assertThat(prediction.getPublishStartInstant()).isNull();
        assertThat(prediction.getPublishStopInstant()).isEqualTo(stop1);
    }


    // TODO, see http://wiki.publiekeomroep.nl/display/poms/Locations+and+predictions#Locationsandpredictions-Locations,streamingplatformandpredictions

    private static Object[][] expected = {
        {Value.OFFLINE, Value.OFFLINE, Encryption.DRM, ""},
        {Value.OFFLINE, Value.OFFLINE, Encryption.NONE, ""},
        {Value.OFFLINE, Value.OFFLINE, null, ""},

        {Value.OFFLINE, Value.ONLINE, Encryption.DRM, ""},
        {Value.OFFLINE, Value.ONLINE, Encryption.NONE, "npo"},
        {Value.OFFLINE, Value.ONLINE, null, "npo"},

        {Value.ONLINE, Value.OFFLINE, Encryption.DRM, "npo+drm"},
        {Value.ONLINE, Value.OFFLINE, Encryption.NONE, "npo+drm"}, // /MSE-4048
        {Value.ONLINE, Value.OFFLINE, null, "npo+drm"},

        {Value.ONLINE, Value.ONLINE, Encryption.DRM, "npo+drm"},
        {Value.ONLINE, Value.ONLINE, Encryption.NONE, "npo+drm,npo"},
        {Value.ONLINE, Value.ONLINE, null, "npo+drm,npo"}


    };

    static String asExpected(Collection<? extends Location> locations) {
        return locations.stream().map(l -> URI.create(l.getProgramUrl()).getScheme()).collect(Collectors.joining(","));
    }

    static String getExpected(Value withDrm, Value withoutDrm, Encryption encryption) {
        for (Object[] expect : expected) {
            if (expect[0] == withDrm && expect[1] == withoutDrm && expect[2] == encryption) {
                return (String) expect[3];
            }
        }
        throw new IllegalArgumentException();
    }

    @Test
    public void realizeStrea5mingPlatformIfNeeded() {
        // Try all permutations

        StreamingStatus.Value[] streamStatusesWithDrm = {Value.OFFLINE, Value.ONLINE};
        StreamingStatus.Value[] streamStatusesWithoutDrm = {Value.OFFLINE, Value.ONLINE};// StreamingStatus.Value.values(); UNSET is no different from OFFLINE

        Encryption[] predictionEncryptions = {Encryption.DRM, Encryption.NONE, null};
        StringBuilderSimpleLogger logger = StringBuilderSimpleLogger.builder()
            .prefix(l -> "")
            .build();
        for (StreamingStatus.Value streamStatusWithDrm : streamStatusesWithDrm) {
            for (StreamingStatus.Value streamStatusWithoutDrm : streamStatusesWithoutDrm) {
                for (Encryption predictionEncryption : predictionEncryptions) {
                    StreamingStatus streamingStatus = StreamingStatus.builder()
                        .withDrm(streamStatusWithDrm)
                        .withoutDrm(streamStatusWithoutDrm)
                        .build();
                    Prediction prediction = Prediction.builder()
                        .plannedAvailability(true)
                        .encryption(predictionEncryption)
                        .platform(Platform.INTERNETVOD)
                        .build();
                    Program program = new Program();
                    program.setStreamingPlatformStatus(streamingStatus);
                    program.setPredictions(Arrays.asList(prediction));
                    Locations.realizeAndRevokeLocationsIfNeeded(program, Platform.INTERNETVOD);

                    String expected = getExpected(streamStatusWithDrm, streamStatusWithoutDrm, predictionEncryption);
                    String result = asExpected(program.getLocations());
                    assertThat(result).withFailMessage(streamingStatus + "," + predictionEncryption + " !-> " + expected + " but " + result).isEqualTo(expected);
                    logger.info("{}\t{}\t{}\t{}", streamingStatus.withDrm, streamingStatus.withoutDrm, prediction.getEncryption(),
                        program.getLocations().stream().map(l -> URI.create(l.getProgramUrl()).getScheme()).collect(Collectors.joining(",")));

                    // ok, now change in every othe combination:
                    for (StreamingStatus.Value streamStatusWithDrm2 : streamStatusesWithDrm) {
                        for (StreamingStatus.Value streamStatusWithoutDrm2 : streamStatusesWithoutDrm) {
                            StreamingStatus streamingStatus2 = StreamingStatus.builder()
                                .withDrm(streamStatusWithDrm2)
                                .withoutDrm(streamStatusWithoutDrm2)
                                .build();
                            program.setStreamingPlatformStatus(streamingStatus);

                            Locations.realizeAndRevokeLocationsIfNeeded(program, Platform.INTERNETVOD);
                            assertThat(asExpected(program.getLocations())).isEqualTo(getExpected(streamStatusWithDrm, streamStatusWithoutDrm, predictionEncryption));
                        }
                    }
                }
            }
            log.info(logger.getStringBuilder().toString());

        }
    }


}
