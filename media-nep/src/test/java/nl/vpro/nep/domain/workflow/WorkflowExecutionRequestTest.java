package nl.vpro.nep.domain.workflow;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class WorkflowExecutionRequestTest {

    @Test
    public void testWorkflowExecutionRequest() throws Exception{
        List<String> platforms = new ArrayList<>();
        platforms.add("internetvod");
        platforms.add("tvvod");

        WorkflowExecutionRequest workflowExecutionRequest = WorkflowExecutionRequest.builder()
                .mid("AB_123132")
                .fileName("bla.mp3")
                .encryption(EncryptionType.DRM)
                .priority(PriorityType.NORMAL)
                .type(Type.VIDEO)
                .platforms(platforms)
                .build();

        WorkflowExecutionRequest rounded = Jackson2TestUtil.roundTripAndSimilar(workflowExecutionRequest,
                "{\n" +
                        "  \"mid\" : \"AB_123132\",\n" +
                        "  \"fileName\" : \"bla.mp3\",\n" +
                        "  \"encryption\" : \"DRM\",\n" +
                        "  \"priority\" : \"NORMAL\",\n" +
                        "  \"type\" : \"VIDEO\",\n" +
                        "  \"platforms\" : [ \"internetvod\", \"tvvod\" ]\n" +
                        "}");
    }
}