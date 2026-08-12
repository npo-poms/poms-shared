package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import nl.vpro.i18n.Locales;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.logging.simple.Slf4jSimpleLogger;
import nl.vpro.test.jupiter.TimingExtension;
import nl.vpro.util.FileCachingInputStream;

import static nl.vpro.util.FileCachingInputStream.throttle;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
@Disabled("Actual uploading, needs local file")
@ExtendWith(TimingExtension.class)
public class NEPFTPSUploadServiceImplITest {

    SimpleLogger simpleLogger = Slf4jSimpleLogger.of(log);

    private NEPFTPSUploadServiceImpl impl;

    Instant start = Instant.now();


    private final String[] files = new String[] {"/Users/michiel/samples/portrait.mp4"};
    //, "/Users/michiel/npo/media/huge2.mp4"};


    @BeforeEach
    public void init() {
        impl = new NEPFTPSUploadServiceImpl(NEPTest.PROPERTIES);
        Locales.setDefault(Locales.DUTCH);
    }




    @Test
    public void upload() throws Exception {
        byte[] example = new byte[]{1, 2, 3, 4};
        String filename = "npoweb-vpro/test.1235";
        impl.upload(new Slf4jSimpleLogger(log), filename, (long) example.length, new ByteArrayInputStream(example), true);
    }


    @Test
    public void uploadHuge() throws Exception {
        File file = new File(files[0]);
        String filename = "test.1235";
        InputStream fileInputStream = Files.newInputStream(file.toPath());
        impl.upload(simpleLogger, filename, file.length(), fileInputStream, true);
        log.info("Took {}", Duration.between(start, Instant.now()));
    }

    @Test
    public void uploadHugeWithFile() throws Exception {
        File file = new File(files[0]);
        String filename = "test.1235";
        InputStream fileInputStream = Files.newInputStream(file.toPath());

        try (FileCachingInputStream in = FileCachingInputStream.builder()
            .input(fileInputStream)
            .downloadFirst(false)
            .batchSize(impl.getBatchSize())
            .progressLoggingBatch(50)
            .logger(log)
            //.batchSize(5000)
            .batchConsumer(throttle(Duration.ofMillis(10)))
            .build()) {
            impl.upload(simpleLogger, filename, file.length(), in.getTempFile(), true);
        }
        log.info("Took {}", Duration.between(start, Instant.now()));
    }

    @Test
    public void uploadHugeWithCaching() throws Exception {
        Locales.setDefault(Locales.DUTCH);
        File file = new File(files[0]);
        String filename = "test.1235";
        InputStream fileInputStream = Files.newInputStream(file.toPath());
        try (FileCachingInputStream in = FileCachingInputStream.builder()
            .input(fileInputStream)
            .downloadFirst(false)
            .batchSize(impl.getBatchSize())
            .progressLoggingBatch(50)
            .logger(log)
            //.batchSize(5000)
            .batchConsumer(throttle(Duration.ofMillis(10)))
            .build()) {
            impl.upload(simpleLogger, filename, file.length(), in, true);
        }
        log.info("Took {}", Duration.between(start, Instant.now()));
    }




}
