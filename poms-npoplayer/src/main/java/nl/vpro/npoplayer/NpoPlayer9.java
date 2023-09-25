package nl.vpro.npoplayer;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

import nl.vpro.domain.npoplayer.TokenRequest;
import nl.vpro.domain.npoplayer.TokenResponse;

/**
 * See https://docs.npoplayer.nl/
 */
@Slf4j
public class NpoPlayer9 {


    private final String issuer;
    private final String signingKey;


    @Inject
    public NpoPlayer9(String issuer, String signingKey) {
        this.issuer = issuer;
        this.signingKey = signingKey;
    }

    public TokenResponse token(TokenRequest tokenRequest) {
        return TokenResponse.builder()
            .mid(tokenRequest.getMid())
            .token(jwt(tokenRequest.getMid()))
            .build();
    }

    /**
     * Create a jwt token from the payload
     *

     * @return signed payload in jwt form
     */
    private String jwt(@NonNull String mid) {

        Instant now = Instant.now();
        Instant expires = now.plus(Duration.ofHours(12));
        SecretKey secretKey = Keys.hmacShaKeyFor(signingKey.getBytes());
        String compactJws = Jwts.builder()
            .setSubject(mid)
            .setIssuedAt(Date.from(Instant.now()))
            .setIssuer(issuer)
            .signWith(secretKey)
            .compact();

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("foobarfoobarfoobarfoobarfoobarfoobarfoobarfoobar");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        String token = Jwts.builder()
     //                .signWith(new SsigningKey, SignatureAlgorithm.HS256)

            .compact();
        return token;

    }

}
