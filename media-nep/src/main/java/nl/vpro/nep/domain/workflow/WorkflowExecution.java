package nl.vpro.nep.domain.workflow;

import io.openapitools.jackson.dataformat.hal.HALLink;
import io.openapitools.jackson.dataformat.hal.annotation.Link;
import io.openapitools.jackson.dataformat.hal.annotation.Resource;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@Resource
public class WorkflowExecution {

    @Link
    HALLink self;
    @Getter
    String workflowId;
    @Getter
    StatusType status;
    @Getter
    String statusMessage;
    @Getter
    String workflowType;
    CustomerMetadata customerMetadata;
    @Getter
    Instant startTime;
    @Getter
    Instant updateTime;
    @Getter
    Instant endTime;

}
