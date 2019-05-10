package nl.vpro.nep.service.impl;

import org.junit.Test;

import nl.vpro.nep.domain.WideVineRequest;

import static org.junit.Assert.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
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
    public void test() {

        impl.widevineToken(new WideVineRequest("145.58.169.92"));

    }


}
