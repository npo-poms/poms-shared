package nl.vpro.sourcingservice;

import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import static nl.vpro.logging.simple.Log4j2SimpleLogger.simple;

@Log4j2
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
        Configuration configuration = new Configuration(
            PROPERTIES.getProperty("sourcingservice.video.baseUrl", "https://sourcing-service.acc.metadata.bijnpo.nl/"),
            PROPERTIES.getProperty("sourcingservice.callbackBaseUrl"),
            PROPERTIES.getProperty("sourcingservice.video.token"),
            100_000_000,
            "m.meeuwissen.vpro@gmail.com",
            2
        );
        impl = new VideoSourcingServiceImpl(
            () -> configuration,
            new LoggingMeterRegistry()
        );
    }

    @Test
    public void uploadVideo() throws IOException, InterruptedException {
        Instant start = Instant.now();
        Path file = Paths.get(System.getProperty("user.home") , "samples", "portrait.mp4");

        impl.upload(
            simple(log),
            "WO_VPRO_20286719",
            Files.size(file),
            "video/mp4",
            Files.newInputStream(file),
            null,
            "michiel.meeuwissen@gmail.com"
        );
        log.info("Took {}", Duration.between(start, Instant.now()));
    }


    @Test
    public void status() throws IOException, InterruptedException {

        Object status = impl.status("WO_VPRO_20286719");
        log.info("Status {}", status);
    }

}
