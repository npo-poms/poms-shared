package nl.vpro.nep.service;

import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.nep.domain.workflow.*;

/**
 * This is an interface for the NEP 'transcode' API. This used to be WorkflowExecutionService.
 *
 * http://npo-gatekeeper-prd.cdn2.usvc.nepworldwide.nl/v2/api-docs
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

    @NonNull
    Optional<WorkflowExecution> getTranscodeStatus(
        @NonNull String workflowId
    );

    String getGatekeeperString();
}
