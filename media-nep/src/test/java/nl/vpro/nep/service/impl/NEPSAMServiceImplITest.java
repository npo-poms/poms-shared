package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.time.Duration;
import java.util.Properties;

import org.junit.Test;

import static nl.vpro.nep.service.NEPSAMService.createStreamAccessItem;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class NEPSAMServiceImplITest {


    Properties properties = new Properties();
    {
        File propFile = new File(new File(new File(System.getProperty("user.home")), "conf"), "sam.properties");
        try (FileInputStream input = new FileInputStream(propFile)) {
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    String url = properties.getProperty("url", "https://api.samgcloud.nepworldwide.nl/");
    NEPSAMAuthenticator authenticator = new NEPSAMAuthenticator(
            "npo_poms",
        properties.getProperty("password"),
        url

    );
    NEPSAMServiceImpl impl = new NEPSAMServiceImpl(
        url,
        null,
        null,
        null,
        authenticator
    );

   /* @Test
    public void widevine() {
        WideVineResponse wideVineResponse = impl.widevineToken(new WideVineRequest("145.58.169.92", null));
        log.info("{}", wideVineResponse);

    }


    @Test
    public void playready() {
        PlayreadyResponse wideVineResponse = impl.playreadyToken(new PlayreadyRequest("145.58.169.92"));
        log.info("{}", wideVineResponse);

    }*/

    @Test
    public void streamUrlForMid() {
        String streamUrl = impl.streamAccess("POW_04146689", createStreamAccessItem("145.58.169.92", null));
        log.info("{}", streamUrl);

    }


    @Test
    public void streamUrlForLive() {
        String streamUrl = impl.streamAccess("npo1", createStreamAccessItem("145.58.169.92", Duration.ofHours(24)));
        log.info("{}", streamUrl);

    }


}
