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

import nl.vpro.domain.media.update.UploadResponse;
import nl.vpro.logging.simple.Log4j2SimpleLogger;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.util.FileCachingInputStream;

import static nl.vpro.sourcingservice.SourcingService.loggingConsumer;

@Log4j2
class AudioSourcingServiceImplTest {

    public static final Properties PROPERTIES = new Properties();

    private static final String MID = "WO_VPRO_A20036107";

    static {
        try {
            PROPERTIES.load(new FileInputStream(
                new File(System.getProperty("user.home"), "conf" + File.separator + "sourcingservice.properties")));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    AudioSourcingServiceImpl impl;
    {
        /*ApiClient apiClient = new ApiClient();
        impl =  new SourcingServiceImpl(apiClient);
        apiClient.setBasePath("https://test.sourcing-audio.cdn.npoaudio.nl/");
        ((HttpBearerAuth) apiClient.getAuthentication("bearerAuth")).setBearerToken(PROPERTIES.getProperty("token"));
*/
        Configuration configuration = new Configuration(
            PROPERTIES.getProperty("sourcingservice.audio.baseUrl", "https://sourcing-service.acc.metadata.bijnpo.nl/"),
            PROPERTIES.getProperty("sourcingservice.callbackBaseUrl"),
            PROPERTIES.getProperty("sourcingservice.audio.token", "<token>"),
            50 * 1000 * 1024,
            "m.meeuwissen.vpro@gmail.com",
            Integer.parseInt(PROPERTIES.getProperty("sourcingservice.version", "2"))
        );
        impl = new AudioSourcingServiceImpl(
            () -> configuration,
            new LoggingMeterRegistry()
        );
    }

    @Test
    @Disabled("This does actual stuff, need actual token. Add wiremock version to test our part isolated, as soon as we understand how it should react")
    public void uploadAudio() throws IOException, InterruptedException {
        final Instant start = Instant.now();
        final Path file = Paths.get(System.getProperty("user.home") , "samples", "sample.mp3");

        final SimpleLogger logger = Log4j2SimpleLogger.simple(log);
        final FileCachingInputStream cachingInputStream = FileCachingInputStream.builder()
            .input(Files.newInputStream(file))
            .noProgressLogging()
            .batchSize(1024 * 1024)
            .startImmediately(false)
            .batchConsumer(loggingConsumer(logger, "audio"))
            .build();
        final UploadResponse upload = impl.upload(logger, MID, null,
            Files.size(file),
            "audio/mp3",
            cachingInputStream,
            "m.meeuwissen.vpro@gmail.com"
        );
        log.info("Took {} {}", Duration.between(start, Instant.now()), upload);
    }

    @Test
    @Disabled("This does actual stuff, need actual token. Add wiremock version to test our part isolated, as soon as we understand how it should react")
    public void status() throws IOException, InterruptedException {
        StatusResponse status = impl.status(MID).orElseThrow();
        log.info("Status {}", status);
    }

    @Test
    @Disabled
    public void delete() throws IOException, InterruptedException {
        DeleteResponse status = impl.delete(MID, 0);
        log.info("Status {}", status);
    }

}
