package nl.vpro.sourcingservice;

import lombok.extern.log4j.Log4j2;

import java.io.*;

import org.apache.commons.io.IOUtils;

import nl.vpro.sourcingservice.api.IngestApi;
import nl.vpro.sourcingservice.invoker.ApiClient;
import nl.vpro.sourcingservice.invoker.ApiException;


@Log4j2
public class SourcingServiceImpl implements SourcingService {

    private final static long CHUNK_SIZE = 10_000_000L;
    private final IngestApi ingestApi;




    public SourcingServiceImpl(ApiClient apiClient) {
        this.ingestApi = new IngestApi(apiClient);
    }


    @Override
    public void upload(String mid, InputStream inputStream) throws ApiException {

        try {
            File tempFile = File.createTempFile(mid, ".transfer");
            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                long test = IOUtils.copyLarge(inputStream, outputStream, 0, CHUNK_SIZE);
                Object o = ingestApi.fe2878922c15ec40d930bec65724f0c7(mid, "start", "" + test, tempFile);
            }



        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }
}
