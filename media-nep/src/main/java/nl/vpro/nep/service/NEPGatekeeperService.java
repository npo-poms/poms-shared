package nl.vpro.nep.service;

import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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

    @NonNull
    WorkflowExecution transcode(
        @NonNull WorkflowExecutionRequest request
    ) throws IOException;

    @NonNull
    Iterator<WorkflowExecution> getTranscodeStatuses(
        @Nullable String mid,
        @Nullable StatusType status,
        @Nullable Instant from,
        @Nullable Long limit
    );
<<<<<<< HEAD
=======

    @NonNull
    Optional<WorkflowExecution> getTranscodeStatus(
        @NonNull String workflowId
    );
>>>>>>> 9825812ed... MSE-4670
}
