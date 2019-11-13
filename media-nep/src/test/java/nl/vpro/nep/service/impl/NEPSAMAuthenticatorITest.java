package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class NEPSAMAuthenticatorITest {



    @Test
    public void authenticate() {

        NEPSAMAuthenticator authenticator = new NEPSAMAuthenticator(
            "npo_poms",
            System.getProperty("password"),
            "https://api.samgcloud.nepworldwide.nl/"
        );

        authenticator.get();
        log.info("{}", authenticator.loginResponse.getToken());
        log.info("Expires {}", authenticator.getExpiration());
        assertThat(authenticator.needsRefresh()).isFalse();

    }
}
