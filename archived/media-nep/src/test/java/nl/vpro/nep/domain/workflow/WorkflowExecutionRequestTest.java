package nl.vpro.nep.domain.workflow;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;


public class WorkflowExecutionRequestTest {

    @Test
    public void testWorkflowExecutionRequest() {
        List<String> platforms = new ArrayList<>();
        platforms.add("internetvod");
        platforms.add("tvvod");

        WorkflowExecutionRequest workflowExecutionRequest = WorkflowExecutionRequest.builder()
                .mid("AB_123132")
                .filename("bla.mp3")
                .encryption(EncryptionType.DRM)
                .priority(PriorityType.NORMAL)
                .type(Type.VIDEO)
                .platforms(platforms)
                .build();

        WorkflowExecutionRequest rounded = Jackson2TestUtil.roundTripAndSimilar(workflowExecutionRequest,
            """
                {
                  "mid" : "AB_123132",
                  "filename" : "bla.mp3",
                  "encryption" : "DRM",
                  "priority" : "NORMAL",
                  "type" : "VIDEO",
                  "platforms" : [ "internetvod", "tvvod" ]
                }""");
    }
}
