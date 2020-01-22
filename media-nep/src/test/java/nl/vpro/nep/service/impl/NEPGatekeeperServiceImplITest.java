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
           WorkflowExecution transcodeStatus = gatekeeperService.getTranscodeStatus("544c3368-519d-425e-be1f-d2940697d7b4").get();
           log.info("{}", transcodeStatus);
       }


}
