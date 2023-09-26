package nl.vpro.npoplayer9;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.time.Clock;

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
public class NpoPlayer {

    private final String issuer;
    private final String signingKey;
    private final Clock clock;

    @Inject
    public NpoPlayer(
        @Named("npoplayerapi.issuer") @NonNull String issuer,
        @Named("npoplayerapi.secretKey") @NonNull String signingKey) {
        this(Clock.systemUTC(), issuer, signingKey);
    }

     private NpoPlayer(Clock clock, String issuer, String signingKey) {
        this.issuer = issuer;
        this.signingKey = signingKey;
        this.clock =  clock;
    }

    /**
     * For testing purposes, you could fix the clock.
     */
    public NpoPlayer withClock(@NonNull Clock clock) {
        return new NpoPlayer(clock, issuer, signingKey);
    }

    public String token(@NonNull String mid) {
        // TODO: as yet untested with a real player.
        SecretKey secretKey = Keys.hmacShaKeyFor(signingKey.getBytes());
        return Jwts.builder()
            .setSubject(mid)
            .setIssuedAt(Date.from(clock.instant()))
            .setIssuer(issuer)
            .signWith(secretKey)
            .compact();
    }

}
