package nl.vpro.sourcingservice;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.user.UserService;
import nl.vpro.logging.simple.Log4j2SimpleLogger;

import static org.mockito.Mockito.mock;

@Log4j2
class SourcingServiceImplTest {

    public static final Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(new FileInputStream(
                new File(System.getProperty("user.home"), "conf" + File.separator + "sourcingservice.properties")));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    SourcingServiceImpl impl;
    {
        /*ApiClient apiClient = new ApiClient();
        impl =  new SourcingServiceImpl(apiClient);
        apiClient.setBasePath("https://test.sourcing-audio.cdn.npoaudio.nl/");
        ((HttpBearerAuth) apiClient.getAuthentication("bearerAuth")).setBearerToken(PROPERTIES.getProperty("token"));
*/
        impl = new SourcingServiceImpl(
            "https://test.sourcing-audio.cdn.npoaudio.nl/",
            "https://test.sourcing-video.cdn.npoaudio.nl/",
            PROPERTIES.getProperty("sourcingservice.callbackBaseUrl"),
            PROPERTIES.getProperty("sourcingservice.token"),
            mock(UserService.class),
            100_000_000,
            "m.meeuwissen.vpro@gmail.com"
        );
    }

    @Test
    @Disabled("This does actual stuff, need actual token. Add wiremock version to test our part isolated, as soon as we understand how it should react")
    public void uploadAudio() throws IOException, InterruptedException {
        Instant start = Instant.now();
        Path file = Paths.get(System.getProperty("user.home") , "samples", "sample.mp3");

        impl.uploadAudio(Log4j2SimpleLogger.simple(log), "WO_VPRO_A20017042", Files.size(file), Files.newInputStream(file));
        log.info("Took {}", Duration.between(start, Instant.now()));
    }

     @Test
    @Disabled("This does actual stuff, need actual token. Add wiremock version to test our part isolated, as soon as we understand how it should react")
    public void uploadVideo() throws IOException, InterruptedException {
        Instant start = Instant.now();
        Path file = Paths.get(System.getProperty("user.home") , "samples", "test.mp4");

        impl.uploadAudio(Log4j2SimpleLogger.simple(log), "WO_VPRO_A20017042", Files.size(file), Files.newInputStream(file));
        log.info("Took {}", Duration.between(start, Instant.now()));
    }


}
