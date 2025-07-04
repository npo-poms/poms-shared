package nl.vpro.nep.service.impl;

import lombok.extern.log4j.Log4j2;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import nl.vpro.nep.service.NEPSourcingService;

@Log4j2
public class NEPSourcingServiceITest {

    NEPSourcingServiceImpl nepSourceServiceIngestService = new NEPSourcingServiceImpl(
        "https://sourcingservice-acc.cdn1.usvc.nepworldwide.nl/v1/",
         "",
    );


    @Test
    public void ingest() throws ExecutionException, InterruptedException {
        CompletableFuture<HttpResponse<NEPSourcingService.RequestResult>> ingest = nepSourceServiceIngestService.ingest(new NEPSourcingService.Payload(
            "WO_NPO_L20000010_2025-07-04T101905195_portrait.mp4",
            "npo_webonly_vertical@smartftp1.cdn1.usvc.twobridges.io",
            List.of(
                new NEPSourcingService.Profile(
                    UUID.fromString("89968391-875e-47d1-8912-7f2f5135a837"),
                    List.of(
                        new NEPSourcingService.Parameter("drm", "true")
                    )
                )
            ),
            List.of())
        );
        HttpResponse<NEPSourcingService.RequestResult> response = ingest.get();
        log.info("{}: {} ", response.statusCode(), response.body());


    }
}
