package nl.vpro.nep.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import nl.vpro.i18n.Locales;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.logging.simple.Slf4jSimpleLogger;
import nl.vpro.util.FileCachingInputStream;

import static nl.vpro.util.FileCachingInputStream.throttle;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
@Disabled("Actual uploading, needs local file")
public class NEPSSHJUploadServiceImplITest {

    SimpleLogger simpleLogger = Slf4jSimpleLogger.of(log);

    private NEPSSHJUploadServiceImpl impl;

    Instant start = Instant.now();


    private final String[] files = new String[] {"/Users/michiel/samples/PieterWinsemius_nieuw.mp3"};
    //, "/Users/michiel/npo/media/huge2.mp4"};


    @BeforeEach
    public void init() {
        impl = new NEPSSHJUploadServiceImpl(NEPTest.PROPERTIES);
        Locales.setDefault(Locales.DUTCH);
    }

    @Test
    public void client() throws IOException {
        log.info("{}", impl);
        try (SSHClient client = impl.createClient().get()) {
            log.info("Client {}", client);
            try (final SFTPClient sftp = client.newSFTPClient()) {

                for (RemoteResourceInfo s : sftp.ls("/")) {
                    log.info("{}", s);

                }
            }
        }
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


    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void uploadHugeSshjSftpFileTransfer(boolean preserveAttributes) throws Exception {
          final File file = new File(files[0]);
          FileCachingInputStream in = FileCachingInputStream.builder()
              .input(new FileInputStream(file))
              .downloadFirst(false)
              .batchSize(impl.getBatchSize())
              .progressLoggingBatch(50)
              .logger(log)
              .build();
          String filename = "test.1235";
        try (SSHClient ssh = impl.createClient().get()) {
            var scp = ssh.newSFTPClient();
            var filet = scp.getFileTransfer();
            filet.setPreserveAttributes(preserveAttributes);
            filet.setTransferListener(impl.new Listener(Slf4jSimpleLogger.of(log), file.length()));
            filet.upload(
                new FileSystemFile(in.getTempFile().toFile()), "/" + filename
            );
        }
    }

    @Test
    @SneakyThrows
    public void async() {
        assumeTrue(files.length % 2 == 0);
        List<ForkJoinTask<?>> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(ForkJoinPool.commonPool().submit(upload(new File(files[i % 2]), "test." + i)));

        }
        for (ForkJoinTask<?> task : tasks) {
            task.get();
        }
        tasks.clear();
        for (int i = 0; i < 10; i++) {
            tasks.add(ForkJoinPool.commonPool().submit(upload(new File(files[i % 2]), "test." + i)));
        }
        for (ForkJoinTask<?> t : tasks) {
            t.get();
        }
    }


    public Runnable upload(File from, String to) {
        return () -> {
            Instant start = Instant.now();
            File file = from;
            String filename = to;
            try {
                impl.upload(new Slf4jSimpleLogger(log), filename, file.length(), new FileInputStream(file), true);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            log.info("Took {}", Duration.between(start, Instant.now()));
        };
    }


}
