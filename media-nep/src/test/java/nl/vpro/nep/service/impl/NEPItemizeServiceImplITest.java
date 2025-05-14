package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import nl.vpro.nep.domain.ItemizerStatusResponse;
import nl.vpro.nep.domain.NEPItemizeResponse;
import nl.vpro.nep.domain.workflow.WorkflowExecution;
import nl.vpro.nep.service.NEPDownloadService;
import nl.vpro.nep.service.exception.ItemizerStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assumptions.assumeThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Timeout(value = 10, unit = TimeUnit.MINUTES)
public class NEPItemizeServiceImplITest {

    final static String MID = "TELEA_1044063";
    //String MID = "AT_2073522";
    @Test
    @Order(1)
    public void itemize() throws Exception {
        Instant start = Instant.now();
        try (NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(NEPTest.PROPERTIES)) {
            NEPItemizeResponse response = itemizer.itemizeMid(
                MID,
                Duration.ZERO,
                Duration.ofMinutes(2).plusSeconds(21).plusMillis(151),
                null
            );
            log.info("response: {} {}", response, start);
            try (NEPGatekeeperServiceImpl gatekeeperService = new NEPGatekeeperServiceImpl(NEPTest.PROPERTIES)) {
                Optional<WorkflowExecution> workflowExecution = gatekeeperService.getTranscodeStatus(response.getId());
                log.info("{}", workflowExecution);

                NEPDownloadService downloadService = new NEPScpDownloadServiceImpl(NEPTest.PROPERTIES);
                File dest = new File("/tmp", "dest.mp4");
                downloadService.download("", response.getOutput_filename(), () -> {
                    try {
                        return Files.newOutputStream(dest.toPath());
                    } catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                }, Duration.ofMinutes(10), (fm) -> {
                    log.info("Found {}", fm);
                    return NEPDownloadService.Proceed.TRUE;
                });
                log.info("Found {} bytes", dest.length());
            }
        }
    }

    static  NEPItemizeResponse response;
    @SuppressWarnings("BusyWait")
    @Test
    @Order(10)
    @Tag("dvr")
    public void itemizeDvr() throws Exception {
        Instant start = Instant.now();
        try (NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(NEPTest.PROPERTIES)) {
            response = itemizer.itemizeLive("npo-1dvr", Instant.now().minusSeconds(300), Instant.now().minusSeconds(60), null);
            log.info("response: {} {}", response, start);
            while (true) {
                ItemizerStatusResponse jobs = itemizer.getLiveItemizerJobStatus(response.getId());
                log.info("response: {}", jobs);
                if (jobs.getStatus().isEndStatus()) {
                    break;
                }
                Thread.sleep(1000);
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
                return Files.newOutputStream(dest.toPath());
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }, Duration.ofMinutes(10), (fm) -> {
            log.info("Found {}", fm);
            return NEPDownloadService.Proceed.TRUE;
        });
        log.info("Found {} bytes", dest.length());


    }

    @Order(20)
    @RepeatedTest(3)
    public void grabScreen() throws Exception {
        try (NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(NEPTest.PROPERTIES)) {
            File out = File.createTempFile("test", ".jpg");
            Map<String, String> headers = new HashMap<>();
            itemizer.grabScreenLive("npo-1dvr", Instant.now().truncatedTo(ChronoUnit.SECONDS).minus(Duration.ofMinutes(2)), headers::put, Files.newOutputStream(out.toPath()));
            log.info("Created {} bytes {} (found headers {})", out.length(), out, headers);
        }
    }


    @ParameterizedTest
    @Order(30)
    @ValueSource( strings= {"VPWON_1344256", MID, "TELEA_1044063"})
    public void grabScreenMid(String mid) throws Exception {
        try (NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(NEPTest.PROPERTIES)) {
            File out = File.createTempFile("test", ".jpg");
            Map<String, String> headers = new HashMap<>();
            itemizer.grabScreenMid(mid, Duration.ofSeconds(10), headers::put, Files.newOutputStream(out.toPath()));
            log.info("Created {} bytes {} (headers: {})", out.length(), out, headers);
        }
    }


    @Test
    public void getJobsStatus404() {
        ItemizerStatusException foobar = catchThrowableOfType(ItemizerStatusException.class, () -> {
            try (NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(NEPTest.PROPERTIES)) {
                ItemizerStatusResponse jobs = itemizer.getLiveItemizerJobStatus("foobar");
            }
        });
        assertThat(foobar).isInstanceOf(ItemizerStatusException.class);
        assertThat(foobar.getStatusCode()).isEqualTo(404);
        assertThat(foobar.getResponse()).isNotNull();
        log.info("{}", foobar.toString());
    }

}
