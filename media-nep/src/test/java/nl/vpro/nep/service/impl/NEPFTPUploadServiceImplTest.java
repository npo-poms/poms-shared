package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.time.Duration;
import java.time.Instant;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import nl.vpro.logging.simple.Slf4jSimpleLogger;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class NEPFTPUploadServiceImplTest {

    private NEPFTPUploadServiceImpl impl;


    @Before
    public void init() {

        impl = new NEPFTPUploadServiceImpl(
            "ftp.nepworldwide.nl",
            "npoweb-vpro",
            "***REMOVED***",
            "9b:b4:4c:54:d1:7a:aa:63:71:e0:ef:cb:78:22:73:83");
    }

    @Test
    @Ignore("This actually does something")

    public void upload() throws Exception {
        byte[] example = new byte[]{1, 2, 3, 4};
        String filename = "npoweb-vpro/test.1235";
        impl.upload(new Slf4jSimpleLogger(log), filename, (long) example.length, new ByteArrayInputStream(example));
    }


    @Test
    @Ignore("This actually does something")
    public void uploadHuge() throws Exception {
        Instant start = Instant.now();
        File file = new File("/Users/michiel/Downloads/npo-1dvr__2018-03-16T062112392-2018-03-16T084607989.mp4");
        String filename = "test.1235";
        impl.upload(new Slf4jSimpleLogger(log), filename, file.length(), new FileInputStream(file));
        log.info("Took {}", Duration.between(start, Instant.now()));
    }
}
