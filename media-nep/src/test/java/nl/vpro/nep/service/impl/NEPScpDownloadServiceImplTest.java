package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Ignore;
import org.junit.Test;

import nl.vpro.nep.service.NEPDownloadService;
import nl.vpro.util.FileSizeFormatter;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Slf4j
@Ignore("Download huge files")
public class NEPScpDownloadServiceImplTest {



    NEPDownloadService impl = new NEPScpDownloadServiceImpl(
        "sftp-itemizer.nepworldwide.nl",
        "npo",
        "",
        "",
        //"94:06:26:d5:e4:f5:18:b5:52:a9:19:b1:97:db:94:9e"
        Arrays.asList("/local/bin/scp", "/usr/bin/scp"),
        Arrays.asList("/usr/bin/sshpass", "/opt/local/bin/sshpass")


    );
    @Test
    public void test() throws IOException {

        Instant start = Instant.now();

        log.info("using {}", impl);
        FileOutputStream outputStream = new FileOutputStream("/tmp/test.mp4");
        final AtomicLong size  = new AtomicLong(-1);
        final AtomicLong count = new AtomicLong(0);
        impl.download(NEPSSHJDownloadServiceImplTest.fileName,
            () -> outputStream,
            Duration.ofSeconds(10), (fd) -> {
            log.info("{}", fd);
            size.set(fd.getSize());
            if (count.incrementAndGet() < 5) {
                log.info("Testing retry feature a few times");
                return NEPDownloadService.Proceed.RETRY;
            } else {
                return NEPDownloadService.Proceed.TRUE;
            }
        }
        );
        log.info("Duration {} ({})", Duration.between(start, Instant.now()), FileSizeFormatter.DEFAULT.formatSpeed(size.get(), start));


    }

    @Test
    public void testTimeout() throws IOException {

        Instant start = Instant.now();

        log.info("using {}", impl);
        FileOutputStream outputStream = new FileOutputStream("/tmp/test.mp4");
        final long size[] = {-1L};
        impl.download("bestaathelemaalniet.mp4",
            () -> outputStream,
            Duration.ofSeconds(1), (fd) -> {
            log.info("{}", fd);
            size[0] = fd.getSize();
            return NEPDownloadService.Proceed.TRUE;
        }
        );

        log.info("Duration {} ({})", Duration.between(start, Instant.now()), FileSizeFormatter.DEFAULT.formatSpeed(size[0], start));


    }
}
