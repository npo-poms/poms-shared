package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.junit.jupiter.api.*;

import nl.vpro.nep.domain.ItemizerStatusResponse;
import nl.vpro.nep.domain.NEPItemizeResponse;
import nl.vpro.nep.domain.workflow.WorkflowExecution;
import nl.vpro.nep.service.NEPDownloadService;
import nl.vpro.nep.service.exception.NEPException;

import static org.assertj.core.api.Assumptions.assumeThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NEPItemizeServiceImplITest {

    String MID = "POW_04505213";
    //String MID = "AT_2073522";
    @Test
    @Order(1)
    public void itemize() throws IOException, NEPException {
        Instant start = Instant.now();
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(NEPTest.PROPERTIES);
        NEPItemizeResponse response = itemizer.itemizeMid(
            MID,
            Duration.ZERO,
            Duration.ofMinutes(2).plusSeconds(21).plusMillis(151),
            null
        );
        log.info("response: {} {}", response, start);
        NEPGatekeeperServiceImpl gatekeeperService = new NEPGatekeeperServiceImpl(NEPTest.PROPERTIES);
        Optional<WorkflowExecution> workflowExecution = gatekeeperService.getTranscodeStatus(response.getId());
        log.info("{}", workflowExecution);

        NEPDownloadService downloadService = new NEPScpDownloadServiceImpl(NEPTest.PROPERTIES);
        File dest = new File("/tmp", "dest.mp4");
        downloadService.download("", response.getOutput_filename(), () -> {
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
    public void itemizeDvr() throws NEPException, InterruptedException {
        Instant start = Instant.now();
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(NEPTest.PROPERTIES);
        response = itemizer.itemizeLive("npo-1dvr", Instant.now().minusSeconds(300), Instant.now().minusSeconds(60), null);
        log.info("response: {} {}", response, start);
        while(true) {
            ItemizerStatusResponse jobs = itemizer.getItemizerJobStatus(response.getId());
            Thread.sleep(1000);
            log.info("response: {}", jobs);
            if (jobs.getStatus().isEndStatus()) {
                break;
            }
        }


    }
    @Test
    @Order(11)
    @Tag("dvr")
    public void itemizeDvrDownload() throws IOException {
        assumeThat(response).isNotNull();

        NEPDownloadService downloadService = new NEPScpDownloadServiceImpl(NEPTest.PROPERTIES);
        File dest = new File("/tmp", "dest.mp4");
        downloadService.download("", response.getOutput_filename(), () -> {
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
    public void grabScreen() throws IOException, NEPException {
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(NEPTest.PROPERTIES);
        File out = File.createTempFile("test", ".jpg");
        Map<String, String> headers = new HashMap<>();
        itemizer.grabScreenLive("npo-1dvr", Instant.now().truncatedTo(ChronoUnit.SECONDS).minus(Duration.ofMinutes(1)), headers::put, new FileOutputStream(out));
        log.info("Created {} bytes {} (found headers {})", out.length(), out, headers);
    }


    @Test
    @Order(30)

    public void grabScreenMid() throws IOException, NEPException {
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(NEPTest.PROPERTIES);
        File out = File.createTempFile("test", ".jpg");
        Map<String, String> headers = new HashMap<>();
        itemizer.grabScreenMid(MID, Duration.ZERO,  headers::put, new FileOutputStream(out));
        log.info("Created {} bytes {} (headers: {})", out.length(), out, headers);
    }


    @Test
    public void getJobsStatus404() {
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(NEPTest.PROPERTIES);

        ItemizerStatusResponse jobs = itemizer.getItemizerJobStatus("foobar");
        log.info("{}", jobs);
    }

}
