package nl.vpro.sourcingservice;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;

import nl.vpro.domain.user.UserService;


@Log4j2
public class VideoSourcingServiceImpl extends  AbstractSourcingServiceImpl implements VideoSourcingService {



    public VideoSourcingServiceImpl(
        @Value("${sourcingservice.video.baseUrl}") String audioBaseUrl,
        @Value("${sourcingservice.callbackBaseUrl}") String callbackBaseUrl,
        @Value("${sourcingservice.video.token}") String audioToken,
        UserService<?> userService,
        @Value("${sourcingservice.chunkSize:10000000}") int chunkSize,
        @Value("${sourcingservice.defaultEmail:#{null}}") String defaultEmail
       ) {
        super(audioBaseUrl, callbackBaseUrl, audioToken,  userService, chunkSize, defaultEmail);
    }

    @Override
    protected String getFileName(String mid) {
        return mid + ".mp4";
    }


}
