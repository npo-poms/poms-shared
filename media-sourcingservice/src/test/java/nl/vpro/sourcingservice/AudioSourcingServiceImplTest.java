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

import nl.vpro.domain.media.GeoRestriction;
import nl.vpro.domain.media.Region;
import nl.vpro.logging.simple.Log4j2SimpleLogger;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.util.FileCachingInputStream;
import nl.vpro.util.FileSizeFormatter;

import static nl.vpro.i18n.MultiLanguageString.en;

@Log4j2
class AudioSourcingServiceImplTest {

    public static final Properties PROPERTIES = new Properties();

    private static final String MID = "WO_VPRO_A20025026";

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
        impl = new AudioSourcingServiceImpl(
            PROPERTIES.getProperty("sourcingservice.audio.baseUrl", "https://sourcing-service.acc.metadata.bijnpo.nl/"),
            PROPERTIES.getProperty("sourcingservice.callbackBaseUrl"),
            PROPERTIES.getProperty("sourcingservice.audio.token", "<token>"),
            5 * 1000 * 1024,
            "m.meeuwissen.vpro@gmail.com",
            new LoggingMeterRegistry()
        );
    }

    @Test
    @Disabled("This does actual stuff, need actual token. Add wiremock version to test our part isolated, as soon as we understand how it should react")
    public void uploadAudio() throws IOException, InterruptedException {
        Instant start = Instant.now();
        Path file = Paths.get(System.getProperty("user.home") , "samples", "sample.mp3");

        Restrictions restrictions = new Restrictions();
        restrictions.setGeoRestriction(GeoRestriction.builder().region(Region.NL).build());
        SimpleLogger logger = Log4j2SimpleLogger.simple(log);
        FileCachingInputStream cachingInputStream = FileCachingInputStream.builder()
            .input(Files.newInputStream(file))
            .noProgressLogging()
            .startImmediately(true)
            .batchConsumer(fci -> {
                if (fci.isReady()) {

                    if (fci.getException().isEmpty()) {
                        logger.info(en("Uploading ready ({} bytes)")
                            .nl("Uploaden klaar ({} bytes)")
                            .slf4jArgs(FileSizeFormatter.DEFAULT.format(fci.getCount())).build());
                    } else {
                        logger.warn(en("Upload error: {}")
                            .nl("Upload fout: {}")
                            .slf4jArgs(fci.getException().get().getMessage()).build());
                    }
                } else {
                    //logger.debug("Uploaded {}", c.getCount());
                }
            })
            .build();
        impl.upload(logger, MID, restrictions,
            Files.size(file),
            cachingInputStream,
            "m.meeuwissen.vpro@gmail.com"
        );
        log.info("Took {}", Duration.between(start, Instant.now()));
    }



    @Test
    @Disabled("This does actual stuff, need actual token. Add wiremock version to test our part isolated, as soon as we understand how it should react")
    public void status() throws IOException, InterruptedException {

        Object status = impl.status(MID);;
        log.info("Status {}", status);
    }

    @Test
    @Disabled
    public void delete() throws IOException, InterruptedException {

        Object status = impl.delete(MID, 0);
        log.info("Status {}", status);
    }

}
