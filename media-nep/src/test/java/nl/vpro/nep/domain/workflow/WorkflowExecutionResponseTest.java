package nl.vpro.nep.domain.workflow;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class WorkflowExecutionResponseTest {

    @Test
    public void ingestJobToXml() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        WorkflowExecution workflowExecution = WorkflowExecution.builder()
                .workflowId("b337aa4e-bb95-401a-93e7-b9aea6ac327c")
                .status(StatusType.FAILED)
                .statusMessage("Packaging failed")
                .workflowType("npo_webonly_drm")
                .customerMetadata(new CustomerMetadata("VPWON_1267474"))
                .startTime(formatter.parse("2018-02-08T12:43:57Z"))
                .updateTime(formatter.parse("2018-02-08T12:45:27Z"))
                .endTime(formatter.parse("2018-02-08T12:45:27Z")).build();

        List<WorkflowExecution> workflowExecutions = new ArrayList<WorkflowExecution>();
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
                        "      \"mid\" : \"VPWON_1267474\"\n" +
                        "    },\n" +
                        "    \"startTime\" : 1518090237000,\n" +
                        "    \"updateTime\" : 1518090327000,\n" +
                        "    \"endTime\" : 1518090327000\n" +
                        "  } ]\n" +
                        "}");

    }
}