package nl.vpro.nep.domain.workflow;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * @since 5.6
 */
@Getter
@lombok.Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkflowExecutionResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -1979597113983375616L;

    private List<WorkflowExecution> workflowExecutions;


    public WorkflowExecutionResponse() {

    }
}
