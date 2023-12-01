package nl.vpro.sourcingservice;

import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import nl.vpro.logging.simple.Log4j2SimpleLogger;

@Log4j2
@Disabled("This does actual stuff, need actual token. Furthermore, we don't use this")
class VideoSourcingServiceImplITest {

    public static final Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(new FileInputStream(
                new File(System.getProperty("user.home"), "conf" + File.separator + "sourcingservice.properties")));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    VideoSourcingServiceImpl impl;
    {
        /*ApiClient apiClient = new ApiClient();
        impl =  new SourcingServiceImpl(apiClient);
        apiClient.setBasePath("https://test.sourcing-audio.cdn.npoaudio.nl/");
        ((HttpBearerAuth) apiClient.getAuthentication("bearerAuth")).setBearerToken(PROPERTIES.getProperty("token"));
*/
        impl = new VideoSourcingServiceImpl(
            "https://test.sourcing-video.cdn.npoaudio.nl/",
            PROPERTIES.getProperty("sourcingservice.callbackBaseUrl"),
            PROPERTIES.getProperty("sourcingservice.video.token"),
            100_000_000,
            "m.meeuwissen.vpro@gmail.com",
            2,
            new LoggingMeterRegistry()
        );
    }

    @Test
    public void uploadVideo() throws IOException, InterruptedException {
        Instant start = Instant.now();
        Path file = Paths.get(System.getProperty("user.home") , "samples", "test.mp4");

        impl.upload(Log4j2SimpleLogger.simple(log), "WO_VPRO_20057921", null, Files.size(file), null, null, Files.newInputStream(file), null,   (p) -> {
            log.info("Phase {}", p);
        });
        log.info("Took {}", Duration.between(start, Instant.now()));
    }


    @Test
    public void status() throws IOException, InterruptedException {

        Object status = impl.status("WO_VPRO_20057921");
        log.info("Status {}", status);
    }

}
