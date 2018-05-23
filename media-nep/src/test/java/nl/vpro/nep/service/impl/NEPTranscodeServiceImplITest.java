package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Ignore;
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
    NEPTranscodeServiceImpl nepService =
        new NEPTranscodeServiceImpl("http://npo-gatekeeper-acc.cdn1.usvc.nepworldwide.nl", "user", "secret");


    @Before
    public void setup(){
        nepService.init();
    }

    @Test
    public void transcode() {
        WorkflowExecutionRequest request = WorkflowExecutionRequest.builder()
            .mid("VPWON_1265965")
            .encryption(EncryptionType.NONE)
            .priority(PriorityType.LOW)
            .filename("/VPWON_1265965__000000000-000004299.mp4")
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
    @Ignore
    public void getStatuses() {
        AtomicLong count = new AtomicLong(0);
        nepService.getTranscodeStatuses(null, null, null,  null).forEachRemaining((we)
            -> {
            if (we.getMid() != null && we.getMid().equals("WO_NPO_3262544")) {
                log.info("{}: {}", count.incrementAndGet(), we);
            }
            }
        );
    }
}
