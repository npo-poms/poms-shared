package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;

import nl.vpro.nep.domain.workflow.WorkflowExecution;

import static org.junit.Assert.*;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
@Slf4j
public class NEPGatekeeperServiceImplITest {

    NEPGatekeeperServiceImpl gatekeeperService = new NEPGatekeeperServiceImpl(NEPTest.PROPERTIES);


       @Test
       public void testGet() {
           WorkflowExecution transcodeStatus = gatekeeperService.getTranscodeStatus("d2263e54-158d-4e8c-958c-b38885bd3fb8").get();
           log.info("{}", transcodeStatus);
       }


}
