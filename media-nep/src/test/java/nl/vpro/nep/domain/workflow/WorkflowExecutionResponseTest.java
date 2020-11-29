package nl.vpro.nep.domain.workflow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.util.DateUtils;

public class WorkflowExecutionResponseTest {

    @Test
    public void json() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

        WorkflowExecution workflowExecution = WorkflowExecution.builder()
                .workflowId("b337aa4e-bb95-401a-93e7-b9aea6ac327c")
                .status(StatusType.FAILED)
                .statusMessage("Packaging failed")
                .workflowType("npo_webonly_drm")
                .customerMetadata(CustomerMetadata.builder().mid("VPWON_1267474").broadcaster("VPRO").build())
                .startTime(DateUtils.toInstant(formatter.parse("2018-02-08T12:43:57Z")))
                .updateTime(DateUtils.toInstant(formatter.parse("2018-02-08T12:45:27Z")))
                .endTime(DateUtils.toInstant(formatter.parse("2018-02-08T12:45:27Z"))).build();

        List<WorkflowExecution> workflowExecutions = new ArrayList<>();
        workflowExecutions.add(workflowExecution);

        WorkflowExecutionResponse workflowExecutionResponse = WorkflowExecutionResponse.builder()
                .workflowExecutions(workflowExecutions)
                .build();

        WorkflowExecutionResponse rounded = Jackson2TestUtil.roundTripAndSimilar(workflowExecutionResponse,
                "{\n" +
                    "  \"workflowExecutions\" : [ {\n" +
                    "    \"workflowId\" : \"b337aa4e-bb95-401a-93e7-b9aea6ac327c\",\n" +
                    "    \"status\" : \"FAILED\",\n" +
                    "    \"statusMessage\" : \"Packaging failed\",\n" +
                    "    \"workflowType\" : \"npo_webonly_drm\",\n" +
                    "    \"customerMetadata\" : {\n" +
                    "      \"mid\" : \"VPWON_1267474\",\n" +
                    "      \"broadcaster\" : \"VPRO\"\n" +
                    "    },\n" +
                    "    \"startTime\" : 1518093837000,\n" +
                    "    \"updateTime\" : 1518093927000,\n" +
                    "    \"endTime\" : 1518093927000\n" +
                    "  } ]\n" +
                    "}");

    }
}
