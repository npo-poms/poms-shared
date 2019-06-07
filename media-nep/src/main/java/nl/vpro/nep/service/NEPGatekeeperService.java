package nl.vpro.nep.service;

import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nl.vpro.nep.domain.workflow.StatusType;
import nl.vpro.nep.domain.workflow.WorkflowExecution;
import nl.vpro.nep.domain.workflow.WorkflowExecutionRequest;

/**
 * This is an interface for the NEP 'transcode' API. This used to be WorkflowExecutionService.
 *
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface NEPGatekeeperService {

    @Nonnull
    WorkflowExecution transcode(
        @Nonnull WorkflowExecutionRequest request
    ) throws IOException;

    @Nonnull
    Iterator<WorkflowExecution> getTranscodeStatuses(
        @Nullable String mid,
        @Nullable StatusType status,
        @Nullable Instant from,
        @Nullable Long limit
    );
}
