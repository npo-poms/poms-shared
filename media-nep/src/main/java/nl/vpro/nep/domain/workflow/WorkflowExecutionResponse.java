package nl.vpro.nep.domain.workflow;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
public class WorkflowExecutionResponse implements Serializable {
    List<WorkflowExecution> workflowExecutions;
}
