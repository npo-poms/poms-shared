package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.vpro.nep.domain.workflow.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
public class NEPTranscodeServiceImplITest {

    static NEPGatekeeperServiceImpl nepService;

    String mid = "WO_VPRO_783763";

    @BeforeAll
    public static void init() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(System.getProperty("user.home") + "/conf/nep.properties")));
        nepService = new NEPGatekeeperServiceImpl(properties);
        nepService.init();
    }

    @Test
    public void transcode() {
        WorkflowExecutionRequest request = WorkflowExecutionRequest.builder()
            .mid(mid)
            .encryption(EncryptionType.NONE)
            .priority(PriorityType.LOW)
            .platforms(Arrays.asList("internetvod"))
            .file(nepService.getFtpUserName(), "WO_VPRO_783763_2018-11-12T133004122_geldig.mp4")
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
