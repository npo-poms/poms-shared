package nl.vpro.nep.service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import nl.vpro.nep.domain.workflow.EncryptionType;
import nl.vpro.nep.domain.workflow.PriorityType;
import nl.vpro.nep.domain.workflow.WorkflowExecution;
import nl.vpro.nep.domain.workflow.WorkflowExecutionRequest;
import nl.vpro.nep.service.impl.NEPServiceImpl;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
public class WorkflowExecutionServiceImplITest {
    //WorkflowExecutionServiceImpl nepService =
//        new WorkflowExecutionServiceImpl("http://npo-gatekeeper-acc.cdn1.usvc.nepworldwide.nl", "user", "***REMOVED***");


    NEPServiceImpl nepService =
        new NEPServiceImpl("https://npo-webonly-gatekeeper.nepworldwide.nl/", "user", "***REMOVED***");

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
            WorkflowExecution result = nepService.execute(request);
            log.info("Result {}", result);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void getStatuses() {
        Iterator<WorkflowExecution> statuses = nepService.getStatuses(null, null, Instant.now().minus(Duration.ofDays(10)),  100L);
        int count = 0;
        while(statuses.hasNext()) {
            log.info("{}: {}", count++, statuses.next());
        }
    }
}
