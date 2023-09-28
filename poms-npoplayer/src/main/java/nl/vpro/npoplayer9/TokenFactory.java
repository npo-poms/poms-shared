package nl.vpro.npoplayer9;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.time.Clock;
import java.time.temporal.ChronoUnit;

import javax.crypto.SecretKey;
import javax.inject.Inject;
import javax.inject.Named;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * See <a href="https://docs.npoplayer.nl/">npoplayer doc</a>. Just contains the code and credentials to perform the server side jwt generation.
 * @author Michiel Meeuwissen
 * @since 7.8
 */
@Slf4j
@Getter
@ToString(onlyExplicitlyIncluded = true)
public class TokenFactory {

    @ToString.Include
    private final String issuer;
    private final String signingKey;
    private final Clock clock;

    @Inject
    public TokenFactory(
        @Named("npoplayerapi.issuer") @NonNull String issuer,
        @Named("npoplayerapi.secretKey") @NonNull String signingKey) {
        this(Clock.systemUTC(), issuer, signingKey);
    }

     private TokenFactory(Clock clock, String issuer, String signingKey) {
        this.issuer = issuer;
        this.signingKey = signingKey;
        this.clock =  clock;
    }

    /**
     * For testing purposes, you could fix the clock.
     */
    public TokenFactory withClock(@NonNull Clock clock) {
        return new TokenFactory(clock, issuer, signingKey);
    }

    public String token(@NonNull String mid) {
        final SecretKey secretKey = Keys.hmacShaKeyFor(signingKey.getBytes());
        return Jwts.builder()
            .setSubject(mid)
            .setIssuedAt(Date.from(clock.instant().truncatedTo(ChronoUnit.SECONDS)))
            .setIssuer(issuer)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }
}
