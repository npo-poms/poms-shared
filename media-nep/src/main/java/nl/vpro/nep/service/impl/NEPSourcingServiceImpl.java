package nl.vpro.nep.service.impl;

import java.util.Arrays;
import java.util.UUID;

import nl.vpro.nep.service.NEPSourcingService;
import nl.vpro.nep.sourcingservice.api.IngestApi;
import nl.vpro.nep.sourcingservice.invoker.ApiException;
import nl.vpro.nep.sourcingservice.model.IngestRequest;
import nl.vpro.nep.sourcingservice.model.IngestRequestProfiles;

/**
 * TODO. MSE-5411
 *
 */
public class NEPSourcingServiceImpl implements NEPSourcingService  {

    private final IngestApi ingestApi;

    public NEPSourcingServiceImpl(IngestApi ingestApi) {
        this.ingestApi = ingestApi;
    }

    @Override
    public void ingest() throws ApiException {
        IngestRequest request = new IngestRequest();
        request.setFileName("bla.mp3");
        request.setProfiles(Arrays.asList(
            new IngestRequestProfiles().uuid(UUID.randomUUID())
        ));
        ingestApi.v1IngestRequestPost(
            new IngestRequest()
        );

    }
}
