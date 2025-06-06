package nl.vpro.nep.service;

import java.util.List;
import java.util.UUID;

public interface NEPSourceServiceIngestService {

    void ingest(Payload payload) throws Exception;

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
}
