package nl.vpro.nep.service;

import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;

import nl.vpro.nep.domain.workflow.StatusType;
import nl.vpro.nep.domain.workflow.WorkflowExecution;
import nl.vpro.nep.domain.workflow.WorkflowExecutionRequest;

/**
 * This is an interface for the NEP 'transcode' API. This used to be WorkflowExecutionService.
 *
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface NEPTranscodeService {

    WorkflowExecution transcode(WorkflowExecutionRequest request) throws IOException;

    Iterator<WorkflowExecution> getTranscodeStatuses(String mid, StatusType status, Instant from, Long limit);
}
