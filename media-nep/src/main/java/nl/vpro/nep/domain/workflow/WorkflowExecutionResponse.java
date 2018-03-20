package nl.vpro.nep.domain.workflow;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;


/**
 * @since 5.6
 */
@Getter
@lombok.Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkflowExecutionResponse implements Serializable {

    private List<WorkflowExecution> workflowExecutions;


    public WorkflowExecutionResponse() {

    }
}
