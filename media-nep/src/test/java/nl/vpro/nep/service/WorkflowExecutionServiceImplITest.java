package nl.vpro.nep.service;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;

import org.junit.Test;

import nl.vpro.nep.domain.workflow.WorkflowExecution;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
public class WorkflowExecutionServiceImplITest {

     WorkflowExecutionServiceImpl nepService = new WorkflowExecutionServiceImpl();

    {
        nepService.init();
    }

    @Test
    public void transcode() {
        // TODO
        //nepService.execute()
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
