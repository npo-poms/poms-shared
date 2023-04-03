package nl.vpro.sourcingservice;

import java.io.InputStream;

import nl.vpro.sourcingservice.invoker.ApiException;

public interface SourcingService {

    void upload(String mid, InputStream inputStream) throws ApiException;

}
