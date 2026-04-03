package nl.vpro.nep.domain.workflow;

import io.openapitools.jackson.dataformat.hal.HALMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import nl.vpro.nep.service.impl.NEPGatekeeperServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public class WorkflowExecutionTest {


    HALMapper MAPPER = NEPGatekeeperServiceImpl.createMapper();
    @Test
    public void unmarshal() throws IOException {
        WorkflowExecution workflowExecution =
        MAPPER.readValue("""
            {
                  "workflowId" : "01680e99-134d-43ee-af29-c514e6fc1f66",
                  "status" : "FAILED",
                  "statusMessage" : "Packaging failed.",
                  "workflowType" : "npo_webonly_drm",
                  "customerMetadata" : {
                    "mid" : "POW_03427594"
                  },
                  "startTime" : "2018-02-08T12:09:14Z",
                  "updateTime" : "2018-02-08T12:09:46Z",
                  "endTime" : "2018-02-08T12:09:46Z",
                  "_links" : {
                    "self" : {
                      "href" : "http://npo-gatekeeper-acc.cdn1.usvc.nepworldwide.nl/api/workflows/01680e99-134d-43ee-af29-c514e6fc1f66"
                    }
                  }
                }""", WorkflowExecution.class);
        assertThat(workflowExecution.getWorkflowId()).isEqualTo("01680e99-134d-43ee-af29-c514e6fc1f66");
        assertThat(workflowExecution.getStartTime()).isEqualTo(LocalDateTime.of(2018, 2, 8, 12, 9, 14).atZone(ZoneId.of("UTC")).toInstant());

    }
}
