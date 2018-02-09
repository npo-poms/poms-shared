package nl.vpro.nep.domain.workflow;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Builder
@Getter
public class WorkflowExecution {
    String workflowId;
    StatusType status;
    String statusMessage;
    String workflowType;
    CustomerMetadata customerMetadata;
    Date startTime;
    Date updateTime;
    Date endTime;
    List<Link> _links;
}
