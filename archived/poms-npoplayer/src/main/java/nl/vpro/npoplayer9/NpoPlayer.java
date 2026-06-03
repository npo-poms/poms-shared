package nl.vpro.npoplayer9;

import lombok.Getter;

/**
 * Wraps a {@link TokenFactory} with some additional configuration.
 * @since 7.8
 * @author Michiel Meeuwissen
 */
@Getter
public class NpoPlayer {

    final TokenFactory tokenFactory;
    final String bitmovinKey;
    final String analyticsKey;;
    final String endpoint;

    @lombok.Builder
    public NpoPlayer(TokenFactory tokenFactory, String bitmovinKey, String analyticsKey, String endpoint) {
        this.tokenFactory = tokenFactory;
        this.bitmovinKey = bitmovinKey;
        this.analyticsKey = analyticsKey;
        this.endpoint = endpoint;
    }

    public TokenResponse tokenResponse(String mid){
        return TokenResponse.builder()
            .mid(mid)
            .token(tokenFactory.token(mid) )
            .key(bitmovinKey)
            .analyticsKey(analyticsKey)
            .endpoint(endpoint)
            .build();
    }

    /**
     * It's possible that this player is not available, because the configuration is not complete.
     */

    public boolean isAvailable() {
        return endpoint != null;
    }

    @Override
    public String toString() {
        return tokenFactory.getIssuer() + "@" + endpoint;
    }
}
