package nl.vpro.nep.service;

import nl.vpro.nep.sourcingservice.invoker.ApiException;

/**
 * TODO: MSE-5411
 */
public interface NEPSourcingService {


    void ingest() throws ApiException;
}
