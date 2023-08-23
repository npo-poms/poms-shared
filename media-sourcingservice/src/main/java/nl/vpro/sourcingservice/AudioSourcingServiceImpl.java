package nl.vpro.sourcingservice;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedResource;

import nl.vpro.domain.user.UserService;


/**
 * @implNote See {@link AbstractSourcingServiceImpl}
 */
@Log4j2
@ManagedResource
public class AudioSourcingServiceImpl extends  AbstractSourcingServiceImpl implements AudioSourcingService {


    public AudioSourcingServiceImpl(
        @Value("${sourcingservice.audio.baseUrl}") String audioBaseUrl,
        @Value("${sourcingservice.callbackBaseUrl:#{null}") String callbackBaseUrl,
        @Value("${sourcingservice.audio.token}") String audioToken,
        UserService<?> userService,
        @Value("${sourcingservice.chunkSize:10000000}") int chunkSize,
        @Value("${sourcingservice.defaultEmail:#{null}}") String defaultEmail,
        MeterRegistry meterRegistry
       ) {
        super(audioBaseUrl, callbackBaseUrl, audioToken,  userService, chunkSize, defaultEmail, meterRegistry);
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
