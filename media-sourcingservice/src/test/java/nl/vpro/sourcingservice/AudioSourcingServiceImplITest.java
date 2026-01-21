package nl.vpro.sourcingservice;

import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.update.UploadResponse;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.util.FileCachingInputStream;

import static nl.vpro.logging.simple.Log4j2SimpleLogger.simple;
import static nl.vpro.poms.shared.UploadUtils.loggingConsumer;


@Log4j2
@Disabled("This does actual stuff, need actual token.")
class AudioSourcingServiceImplITest {

    public static final Properties PROPERTIES = new Properties();

    private static final String MID = "WO_VPRO_A20036163";

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
    public void uploadAudio() throws IOException, InterruptedException, ExecutionException {
        final Instant start = Instant.now();
        final Path file = Paths.get(System.getProperty("user.home") , "samples", "sample.wav");

        final SimpleLogger logger = simple(log);
        final FileCachingInputStream cachingInputStream = FileCachingInputStream.builder()
            .input(Files.newInputStream(file))
            .batchSize(50 * 1024 * 1024)
            .outputBuffer(50 * 1024 * 1024)
            .simpleLogger(simple(log))
            .filePrefix("gui-upload-")
            .deleteTempFile(false)
            .startImmediately(true)
            .batchConsumer(loggingConsumer(logger, "audio"))
            .build();
        final UploadResponse upload = impl.upload(logger, MID,
            Files.size(file),
            "audio/mpeg",
            cachingInputStream,
            null,
            "m.meeuwissen.vpro@gmail.com"
        ).get();
        log.info("Took {} {}", Duration.between(start, Instant.now()), upload);
    }

    @Test
    public void status() throws IOException, InterruptedException {
        StatusResponse status = impl.status(MID).orElseThrow();
        log.info("Status {}", status);
    }

    @Test
    public void delete() throws IOException, InterruptedException {
        DeleteResponse status = impl.delete(MID, 1);
        log.info("Status {}", status);
    }

}
