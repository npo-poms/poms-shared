package nl.vpro.sourcingservice;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;


/**
 * Video part of sourcing service. This is currently not used, as poms interfaces with NEP directly for that.
 * @implNote See {@link AbstractSourcingServiceImpl}
 */
@Log4j2
public class VideoSourcingServiceImpl extends  AbstractSourcingServiceImpl implements VideoSourcingService {



    public VideoSourcingServiceImpl(
        @Value("${sourcingservice.video.baseUrl}") String audioBaseUrl,
        @Value("${sourcingservice.callbackBaseUrl:#{null}}") String callbackBaseUrl,
        @Value("${sourcingservice.video.token}") String audioToken,
        @Value("${sourcingservice.chunkSize:10000000}") int chunkSize,
        @Value("${sourcingservice.defaultEmail:#{null}}") String defaultEmail,
        MeterRegistry meterRegistry
       ) {
        super(audioBaseUrl, callbackBaseUrl, audioToken, chunkSize, defaultEmail, meterRegistry);
    }

    @Override
    protected String getFileName(String mid) {
        return mid + ".mp4";
    }

    @Override
    protected String implName() {
        return "video";
    }


}
