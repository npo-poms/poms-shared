package nl.vpro.nep.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.i18n.Locales;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.logging.simple.Slf4jSimpleLogger;
import nl.vpro.util.FileCachingInputStream;

import static nl.vpro.util.FileCachingInputStream.throttle;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
//@Ignore("Actuall uploading")
public class NEPSSHJUploadServiceImplITest {

    SimpleLogger simpleLogger = Slf4jSimpleLogger.of(log);

    private NEPSSHJUploadServiceImpl impl;

    Instant start = Instant.now();


    private String[] files = new String[] {"/Users/mihxil/WO_NTR_16270855_2020-08-25T172847803_dolleminas01.mp4"};
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
        InputStream fileInputStream = new FileInputStream(file);
        impl.upload(simpleLogger, filename, file.length(), fileInputStream, true);
        log.info("Took {}", Duration.between(start, Instant.now()));
    }

    @Test
    public void uploadHugeWithCaching() throws Exception {
        Locales.setDefault(Locales.DUTCH);
        File file = new File(files[0]);
        String filename = "test.1235";
        InputStream fileInputStream = new FileInputStream(file);
        try (FileCachingInputStream in = FileCachingInputStream.builder()
            .input(fileInputStream)
            .downloadFirst(false)
            .progressLoggingBatch(50)
            .logger(log)
            //.batchSize(5000)
            .batchConsumer(throttle(Duration.ofMillis(10)))
            .build()) {
            impl.upload(simpleLogger, filename, file.length(), in, true);
        }
        log.info("Took {}", Duration.between(start, Instant.now()));
    }

    @Test
    @SneakyThrows
    public void async() {
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
