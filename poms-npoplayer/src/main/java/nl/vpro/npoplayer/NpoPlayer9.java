package nl.vpro.npoplayer;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.time.Clock;

import javax.crypto.SecretKey;
import javax.inject.Inject;

/**
 * See <a href="https://docs.npoplayer.nl/">npoplayer doc</a>
 */
@Slf4j
public class NpoPlayer9 {

    private final String issuer;
    private final String signingKey;
    private final Clock clock;


    @Inject
    public NpoPlayer9(String issuer, String signingKey) {
        this(Clock.systemUTC(), issuer, signingKey);
    }

     private NpoPlayer9(Clock clock, String issuer, String signingKey) {
        this.issuer = issuer;
        this.signingKey = signingKey;
        this.clock =  clock;
    }

    /**
     * For testing purposes, you could fix the clock.
     */
    public NpoPlayer9 withClock(Clock clock) {
        return new NpoPlayer9(clock, issuer, signingKey);
    }

    public String token(String mid) {
        SecretKey secretKey = Keys.hmacShaKeyFor(signingKey.getBytes());
        return Jwts.builder()
            .setSubject(mid)
            .setIssuedAt(Date.from(clock.instant()))
            .setIssuer(issuer)
            .signWith(secretKey)
            .compact();

    }

}
