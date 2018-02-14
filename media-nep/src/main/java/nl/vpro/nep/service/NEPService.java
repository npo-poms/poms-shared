package nl.vpro.nep.service;

import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;

import nl.vpro.nep.domain.workflow.StatusType;
import nl.vpro.nep.domain.workflow.WorkflowExecution;
import nl.vpro.nep.domain.workflow.WorkflowExecutionRequest;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface NEPService {

    WorkflowExecution execute(WorkflowExecutionRequest request) throws IOException;

    Iterator<WorkflowExecution> getStatuses(String mid, StatusType status, Instant from, Long limit);
}
