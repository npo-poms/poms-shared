package nl.vpro.nep.domain.workflow;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Test;

import nl.vpro.nep.service.impl.NEPGatekeeperServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public class WorkflowExecutionTest {


    @Test
    public void unmarshal() throws IOException {
        WorkflowExecution workflowExecution =
        NEPGatekeeperServiceImpl.MAPPER.readValue("{\n" +
            "      \"workflowId\" : \"01680e99-134d-43ee-af29-c514e6fc1f66\",\n" +
            "      \"status\" : \"FAILED\",\n" +
            "      \"statusMessage\" : \"Packaging failed.\",\n" +
            "      \"workflowType\" : \"npo_webonly_drm\",\n" +
            "      \"customerMetadata\" : {\n" +
            "        \"mid\" : \"POW_03427594\"\n" +
            "      },\n" +
            "      \"startTime\" : \"2018-02-08T12:09:14Z\",\n" +
            "      \"updateTime\" : \"2018-02-08T12:09:46Z\",\n" +
            "      \"endTime\" : \"2018-02-08T12:09:46Z\",\n" +
            "      \"_links\" : {\n" +
            "        \"self\" : {\n" +
            "          \"href\" : \"http://npo-gatekeeper-acc.cdn1.usvc.nepworldwide.nl/api/workflows/01680e99-134d-43ee-af29-c514e6fc1f66\"\n" +
            "        }\n" +
            "      }\n" +
            "    }", WorkflowExecution.class);
        assertThat(workflowExecution.getWorkflowId()).isEqualTo("01680e99-134d-43ee-af29-c514e6fc1f66");
        assertThat(workflowExecution.getStartTime()).isEqualTo(LocalDateTime.of(2018, 2, 8, 12, 9, 14).atZone(ZoneId.of("UTC")).toInstant());

    }
}
