package nl.vpro.nep.service;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface NEPSourcingService {

    CompletableFuture<HttpResponse<RequestResult>> ingest(Payload payload);

    record Parameter(
        String key,
        String value
    ) {
    }

    record Profile(
        UUID uuid,
        List<Parameter> parameters
        ) {
    }


    record Payload(
        String fileName,
        String origin,
        List<Profile> profiles,
        List<String> ancillaryFiles
    ) {
    }

    record RequestResult(
        UUID uuid,
        String fileName,
        String origin,
        Instant requestedAt,
        Instant expiresAt,
        List<Task> tasks,
        boolean isUploaded,
        UUID fileUid,
        Instant uploadedAt,
        String status,
        int progress,
        Instant completedAt
    ) {

    }

    record Task(
        UUID profile,
        String name,
        List<Parameter> inputParameters,
        List<Parameter> outputParameters,
        String status) {

    }
}
