package nl.vpro.sourcingservice;

import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;

import nl.vpro.domain.media.update.UploadResponse;
import nl.vpro.logging.simple.Log4j2SimpleLogger;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.util.FileCachingInputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static nl.vpro.sourcingservice.SourcingService.loggingConsumer;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@WireMockTest
class WiremockAudioSourcingServiceImplTest {




    AudioSourcingServiceImpl impl;

    @BeforeEach
    public void setUp(WireMockRuntimeInfo wireMock) {
        impl = new AudioSourcingServiceImpl(
            wireMock.getHttpBaseUrl(),
            null,
            "token",
            1000,
            null,
            2,
            new LoggingMeterRegistry()
        );
    }

    @Test
    public void uploadAudio() throws IOException, InterruptedException {
        stubFor(post(UrlPattern.ANY).willReturn(ok()));
        final Instant start = Instant.now();

        byte[] bytes = "foobar".getBytes();

        final SimpleLogger logger = Log4j2SimpleLogger.simple(log);
        final FileCachingInputStream cachingInputStream = FileCachingInputStream.builder()
            .input(new ByteArrayInputStream(bytes))
            .noProgressLogging()
            .batchSize(1024 * 1024)
            .startImmediately(false)
            .batchConsumer(loggingConsumer(logger))
            .build();
        final UploadResponse upload = impl.upload(logger, "mid", null,
            bytes.length,
            "audio/mp3",
            cachingInputStream,
            "m.meeuwissen.vpro@gmail.com"
        );
        log.info("Took {} {}", Duration.between(start, Instant.now()), upload);

        List<ServeEvent> allServeEvents = getAllServeEvents();
        ServeEvent serveEvent = allServeEvents.get(0);
        assertThat(serveEvent.getRequest().isMultipart()).isTrue();
        log.info(serveEvent.getRequest().getHeaders());
        String boundary = serveEvent.getRequest().getHeader("Content-Type").split("boundary=")[1];
        assertThat(serveEvent.getRequest().getBodyAsString()).isEqualToNormalizingNewlines(
                """
                    --%s
                    Content-Disposition: form-data; name="file"; filename="mid.mp3"
                    Content-Type: audio/mp3

                    foobar
                    --%s--
                    """.formatted(boundary, boundary)

        );

    }



}
