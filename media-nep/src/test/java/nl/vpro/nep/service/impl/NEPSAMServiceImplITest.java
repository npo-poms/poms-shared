package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import org.junit.Test;

import nl.vpro.nep.domain.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class NEPSAMServiceImplITest {

    String url = "https://api.samgcloud.nepworldwide.nl/v2/";
    NEPSAMAuthenticator authenticator = new NEPSAMAuthenticator(
            "npo_poms",
        System.getProperty("password"),
        url

    );
    NEPSAMServiceImpl impl = new NEPSAMServiceImpl(
        url,
        authenticator
    );

    @Test
    public void widevine() {
        WideVineResponse wideVineResponse = impl.widevineToken(new WideVineRequest("145.58.169.92", null));
        log.info("{}", wideVineResponse);

    }


    @Test
    public void playready() {
        PlayreadyResponse wideVineResponse = impl.playreadyToken(new PlayreadyRequest("145.58.169.92"));
        log.info("{}", wideVineResponse);

    }

    @Test
    public void streamUrlForMid() {
        String streamUrl = impl.streamUrl("POW_04146689", new StreamUrlRequest("145.58.169.92", null));
        log.info("{}", streamUrl);

    }


    @Test
    public void streamUrlForLive() {
        String streamUrl = impl.streamUrl("npo1-dvr", new StreamUrlRequest("145.58.169.92", Duration.ofHours(24)));
        log.info("{}", streamUrl);

    }


}
