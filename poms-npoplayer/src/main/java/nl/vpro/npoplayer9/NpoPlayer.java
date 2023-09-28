package nl.vpro.npoplayer9;

import lombok.Getter;

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

    public boolean isAvailable() {
        return endpoint != null;
    }

    @Override
    public String toString() {
        return tokenFactory.getIssuer() + "@" + endpoint;
    }
}
