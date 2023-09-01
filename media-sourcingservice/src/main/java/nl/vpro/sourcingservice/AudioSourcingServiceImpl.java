package nl.vpro.sourcingservice;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedResource;


/**
 * @implNote See {@link AbstractSourcingServiceImpl}
 */
@Log4j2
@ManagedResource
public class AudioSourcingServiceImpl extends  AbstractSourcingServiceImpl implements AudioSourcingService {


    public AudioSourcingServiceImpl(
        @NonNull @Value("${sourcingservice.audio.baseUrl}") String audioBaseUrl,
        @Value("${sourcingservice.callbackBaseUrl:#{null}}") String callbackBaseUrl,
        @NonNull @Value("${sourcingservice.audio.token}") String audioToken,
        @Value("${sourcingservice.chunkSize:10000000}") int chunkSize,
        @Value("${sourcingservice.defaultEmail:#{null}}") String defaultEmail,
        MeterRegistry meterRegistry
       ) {
        super(audioBaseUrl, callbackBaseUrl, audioToken, chunkSize, defaultEmail, meterRegistry);
    }

    @Override
    protected String getFileName(String mid) {
        return mid + ".mp3";
    }

    @Override
    protected String implName() {
        return "audio";
    }


}
