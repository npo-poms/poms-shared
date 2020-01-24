package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

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


}
