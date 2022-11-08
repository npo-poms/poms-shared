package nl.vpro.nep.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import nl.vpro.util.DateUtils;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
@WireMockTest
public class NEPSAMAuthenticatorTest {


    @Test
    public void authenticate(
        WireMockRuntimeInfo wireMockRuntimeInfo) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("foobarfoobarfoobarfoobarfoobarfoobarfoobarfoobar");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        String token = Jwts.builder()
            .signWith(signingKey, signatureAlgorithm)
            .setExpiration(DateUtils.toDate(Instant.now().plus(Duration.ofDays(14))))
            .compact();

        WireMock.stubFor(post(urlEqualTo("/v2/token"))
            .willReturn(
                aResponse()
                    .withBody("{'token': '" + token + "'}")
            ));


        NEPSAMAuthenticator authenticator = new NEPSAMAuthenticator("username", "password", wireMockRuntimeInfo.getHttpBaseUrl());

        authenticator.get();
        log.info("{}", authenticator.getExpiration());
        assertThat(authenticator.needsRefresh()).isFalse();

    }
}
