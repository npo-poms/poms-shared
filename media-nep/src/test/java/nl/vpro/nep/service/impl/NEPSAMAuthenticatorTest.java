package nl.vpro.nep.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import ru.lanwen.wiremock.ext.WiremockResolver;
import ru.lanwen.wiremock.ext.WiremockUriResolver;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.tomakehurst.wiremock.WireMockServer;

import nl.vpro.util.DateUtils;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
@ExtendWith({
    WiremockResolver.class,
    WiremockUriResolver.class
})
public class NEPSAMAuthenticatorTest {


    @Test
    public void authenticate(
        @WiremockResolver.Wiremock WireMockServer server,
        @WiremockUriResolver.WiremockUri String uri) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("foobarfoobarfoobarfoobarfoobarfoobarfoobarfoobar");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        String token = Jwts.builder()
            .signWith(signingKey, signatureAlgorithm)
            .setExpiration(DateUtils.toDate(Instant.now().plus(Duration.ofDays(14))))
            .compact();

        server.stubFor(post(urlEqualTo("/v2/token"))
            .willReturn(
                aResponse()
                    .withBody("{'token': '" + token + "'}")
            ));


        NEPSAMAuthenticator authenticator = new NEPSAMAuthenticator("username", "password", uri);

        authenticator.get();
        log.info("{}", authenticator.getExpiration());
        assertThat(authenticator.needsRefresh()).isFalse();

    }
}
