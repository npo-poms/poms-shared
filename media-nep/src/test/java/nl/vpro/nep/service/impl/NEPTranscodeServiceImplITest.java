package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;

import nl.vpro.nep.domain.workflow.EncryptionType;
import nl.vpro.nep.domain.workflow.PriorityType;
import nl.vpro.nep.domain.workflow.WorkflowExecution;
import nl.vpro.nep.domain.workflow.WorkflowExecutionRequest;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
public class NEPTranscodeServiceImplITest {

    private String apiUser = "webonly-user";
    private String ftpUser = "npoweb-vpro";
    NEPGatekeeperServiceImpl nepService =
        new NEPGatekeeperServiceImpl("https://npo-webonly-gatekeeper.nepworldwide.nl", apiUser,
            System.getProperty("password"),
            Duration.ofMillis(100).toString(), "", "", 200,  ftpUser);

    String mid = "WO_VPRO_783763";

    @Before
    public void setup(){
        nepService.init();
    }

    @Test
    public void transcode() {
        WorkflowExecutionRequest request = WorkflowExecutionRequest.builder()
            .mid("WO_VPRO_783763")
            .encryption(EncryptionType.NONE)
            .priority(PriorityType.LOW)
            .platforms(Arrays.asList("internetvod"))
            .file(ftpUser, "WO_VPRO_783763_2018-11-12T133004122_geldig.mp4")
            .build()
            ;
        try {
            WorkflowExecution result = nepService.transcode(request);
            log.info("Result {}", result);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void getStatus() {
        nepService.getTranscodeStatuses(mid, null, null, null).forEachRemaining((we) -> {
            log.info("{}", we);
        });
    }

    @Test
    public void getStatuses() throws IOException {
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
        nepService.getTranscodeStatuses(null, null, null,  null).forEachRemaining((we)
            -> {
            log.info("{} {}", count.incrementAndGet(),  we);
            if (mids.contains(we.getMid())) {
                log.info("{}", we);
            }
            }
        );
    }
}
