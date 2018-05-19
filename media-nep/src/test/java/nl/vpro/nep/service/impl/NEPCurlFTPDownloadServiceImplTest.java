package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;

import org.junit.Test;

import nl.vpro.nep.service.NEPDownloadService;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Slf4j
public class NEPCurlFTPDownloadServiceImplTest {

    @Test
    public void test() throws IOException {
        NEPDownloadService impl = new NEPCurlFTPDownloadServiceImpl( "sftp-itemizer.nepworldwide.nl",
            "npo",
            "V5pJULnIqxoBWnT");
        FileOutputStream outputStream = new FileOutputStream("/tmp/test.mp4");
        impl.download("AT_2100854__000000000-005329000.mp4", outputStream, Duration.ofSeconds(10), (fd) -> {
            log.info("{}", fd);
            return true;}
            );

    }

}
