package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import nl.vpro.nep.domain.workflow.*;
import nl.vpro.nep.service.exception.NEPException;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
@Slf4j
public class NEPGatekeeperServiceImplITest {

    NEPGatekeeperServiceImpl gatekeeperService = new NEPGatekeeperServiceImpl(NEPTest.PROPERTIES);


    @Test
    public void testGet() throws NEPException {
        WorkflowExecution transcodeStatus = gatekeeperService.getTranscodeStatus("ce4e4e6a-4467-4a95-b9d7-fe509d2658f8").get();
        log.info("{}", transcodeStatus);
    }


    @Test
    public void test() throws NEPException {
        @NonNull Iterator<WorkflowExecution> i = gatekeeperService.getTranscodeStatuses(null, null, Instant.now().minus(Duration.ofHours(20)), 200L);
        while (i.hasNext()) {
            log.info("{}", i.next());
        }

    }

    String mid = "WO_VPRO_352116";

    @Test
    public void transcode() {
        WorkflowExecutionRequest request = WorkflowExecutionRequest.builder()
            .mid(mid)
            .encryption(EncryptionType.NONE)
            .priority(PriorityType.LOW)
            .platforms(Collections.singletonList("internetvod"))
            .file(gatekeeperService.getFtpUserName(), "WO_VPRO_783763_2018-11-12T133004122_geldig.mp4")
            .build()
            ;

        try {
            WorkflowExecution result = gatekeeperService.transcode(request);
            log.info("Result {}", result);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    @Disabled
    public void retranscode() {
        WorkflowExecutionRequest request = WorkflowExecutionRequest.builder()
            .mid("POMS_BV_16593726")
            .encryption(EncryptionType.DRM)
            .priority(PriorityType.LOW)
            .platforms(Collections.singletonList("internetvod"))
            .file(null, "/npoweb-bnnvara/20210809-blend-aflevering8-hq.mp4")
            .build()
            ;

        try {
            WorkflowExecution result = gatekeeperService.transcode(request);
            log.info("Result {}", result);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void getStatus() throws NEPException {
        gatekeeperService.getTranscodeStatuses(mid, null, null, null).forEachRemaining((we) -> {
            log.info("{}", we);
        });
    }

    @Test
    public void getStatuses() throws IOException, NEPException {
        File file = new File("/Users/michiel/npo/media/trunk/player7.404");
        List<String> mids = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line = reader.readLine();
            while (line != null) {
                String[] split = line.split("\t");
                mids.add(split[0]);
                line = reader.readLine();
            }
        }


        AtomicLong count = new AtomicLong(0);
        gatekeeperService.getTranscodeStatuses(null, null, null,  null).forEachRemaining((we)
            -> {
            log.info("{} {}", count.incrementAndGet(),  we);
            if (mids.contains(we.getMid())) {
                log.info("{}", we);
            }
            }
        );
    }


    @Disabled
    @Test
    public void hackTranscode() {
        // This is a broadcast, cannot be done via poms SYS-1178
        WorkflowExecutionRequest request = WorkflowExecutionRequest.builder()
            .mid("NCRV_1410751")
            .filename("npoweb-vpro/digitaal@vpro.nl/NCRV_1410751.mp4")
            .encryption(EncryptionType.NONE)
            .priority(PriorityType.NORMAL)
            .platforms(Collections.singletonList("internetvod"))
            .build();
        try {
            WorkflowExecution result = gatekeeperService.transcode(request);
            log.info("Result {}", result);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }

}
