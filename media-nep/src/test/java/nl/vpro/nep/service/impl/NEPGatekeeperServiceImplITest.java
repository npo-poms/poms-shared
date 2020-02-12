package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import nl.vpro.nep.domain.workflow.WorkflowExecution;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
@Slf4j
public class NEPGatekeeperServiceImplITest {

    NEPGatekeeperServiceImpl gatekeeperService = new NEPGatekeeperServiceImpl(NEPTest.PROPERTIES);


       @Test
       public void testGet() {
           WorkflowExecution transcodeStatus = gatekeeperService.getTranscodeStatus("e51b3502-f653-4ac7-a504-2853122add77").get();
           log.info("{}", transcodeStatus);
       }


    @Test
       public void test() {
           @NonNull Iterator<WorkflowExecution> i = gatekeeperService.getTranscodeStatuses(null, null, Instant.now().minus(Duration.ofHours(2)), 200L);
           while (i.hasNext()) {
               log.info("{}", i.next());
           }

       }



}
