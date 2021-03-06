package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import nl.vpro.nep.service.exception.NEPException;

import org.junit.jupiter.api.Test;

import nl.vpro.nep.domain.*;
import nl.vpro.nep.service.NEPPlayerTokenService;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class NEPPlayerTokenServiceImplITest extends AbstractNEPTest {



    NEPPlayerTokenService impl = new NEPPlayerTokenServiceImpl(
        getProperty("baseUrl"),
        getProperty("widevinekey"),
        getProperty("playreadykey")
    );

    {
        log.info("Testing with {}", impl);
    }

    public NEPPlayerTokenServiceImplITest() {
        super("nep.tokengenerator-api");
    }

    @Test
    public void widevine() throws NEPException {
        WideVineResponse wideVineResponse = impl.widevineToken("145.58.169.92");
        log.info("{}", wideVineResponse);
    }


    @Test
    public void playready() throws NEPException {
        PlayreadyResponse playreadyToken = impl.playreadyToken("145.58.169.92");
        log.info("{}", playreadyToken);
    }


    @Test
    public void fairplay() throws NEPException {
        FairplayResponse fairplayToken = impl.fairplayToken("145.58.169.92");
        log.info("{}", fairplayToken);
    }
}
