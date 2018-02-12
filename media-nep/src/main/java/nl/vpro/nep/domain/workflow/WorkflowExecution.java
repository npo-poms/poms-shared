package nl.vpro.nep.domain.workflow;

import io.openapitools.jackson.dataformat.hal.HALLink;
import io.openapitools.jackson.dataformat.hal.annotation.Link;
import io.openapitools.jackson.dataformat.hal.annotation.Resource;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
@Resource
public class WorkflowExecution {
    String workflowId;
    StatusType status;
    String statusMessage;
    String workflowType;
    CustomerMetadata customerMetadata;
    Date startTime;
    Date updateTime;
    Date endTime;

    @Link
    HALLink self;
}
