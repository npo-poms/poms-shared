package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.*;

import nl.vpro.nep.domain.NEPItemizeRequest;
import nl.vpro.nep.domain.NEPItemizeResponse;
import nl.vpro.nep.service.NEPDownloadService;

import static org.assertj.core.api.Assumptions.assumeThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NEPItemizeServiceImplITest {

    @Test
    @Order(1)
    public void itemize() throws IOException {
        Instant start = Instant.now();
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(NEPTest.PROPERTIES);
        NEPItemizeRequest request = new NEPItemizeRequest();
        request.setIdentifier("AT_2073522");
        request.setStarttime(NEPItemizeRequest.fromDuration(Duration.ZERO).orElseThrow(IllegalArgumentException::new));
        request.setEndtime(NEPItemizeRequest.fromDuration(Duration.ofMinutes(2).plusSeconds(21).plusMillis(151)).orElseThrow(IllegalAccessError::new));
        NEPItemizeResponse response = itemizer.itemize(request);
        log.info("response: {} {}", response, start);

        NEPDownloadService downloadService = new NEPScpDownloadServiceImpl(NEPTest.PROPERTIES);
        File dest = new File("/tmp", "dest.mp4");
        downloadService.download(response.getOutput_filename(), () -> {
            try {
                return new FileOutputStream(dest);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }, Duration.ofMinutes(10), (fm) -> {
            log.info("Found {}", fm);
            return NEPDownloadService.Proceed.TRUE;
        });
        log.info("Found {} bytes", dest.length());
    }

    static  NEPItemizeResponse response;
    @Test
    @Order(10)
    @Tag("dvr")
    public void itemizeDvr() throws IOException {
        Instant start = Instant.now();
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(NEPTest.PROPERTIES);
        NEPItemizeRequest request = new NEPItemizeRequest();
        request.setIdentifier("npo-1dvr");
        request.setStarttime(NEPItemizeRequest.fromInstant(Instant.now().minusSeconds(300)).orElseThrow(IllegalArgumentException::new));
        request.setEndtime(NEPItemizeRequest.fromInstant(Instant.now().minusSeconds(60)).orElseThrow(IllegalArgumentException::new));
        response = itemizer.itemize(request);
        log.info("response: {} {}", response, start);

    }
    @Test
    @Order(11)
    @Tag("dvr")
    public void itemizeDvrDownload() throws IOException {
        assumeThat(response).isNotNull();

        NEPDownloadService downloadService = new NEPScpDownloadServiceImpl(NEPTest.PROPERTIES);
        File dest = new File("/tmp", "dest.mp4");
        downloadService.download(response.getOutput_filename(), () -> {
            try {
                return new FileOutputStream(dest);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }, Duration.ofMinutes(10), (fm) -> {
            log.info("Found {}", fm);
            return NEPDownloadService.Proceed.TRUE;
        });
        log.info("Found {} bytes", dest.length());


    }

    @Test
    @Order(20)
    public void grabScreen() throws IOException {
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(NEPTest.PROPERTIES);
        File out = File.createTempFile("test", ".jpg");
        itemizer.grabScreen("npo-1dvr", Instant.now().minus(Duration.ofMinutes(1)), new FileOutputStream(out));
        log.info("Created {} bytes {}", out.length(), out);


    }


    @Test
    public void grabScreenMid() throws IOException {
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(NEPTest.PROPERTIES);
        File out = File.createTempFile("test", ".jpg");
        itemizer.grabScreen("POW_00683426", Duration.ZERO, new FileOutputStream(out));
        log.info("Created {} bytes {}", out.length(), out);


    }
}
