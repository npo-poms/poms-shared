package nl.vpro.npoplayer9;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Player 9 just needs a server side generated jws token.
 * <p>
 * This wraps it together with the original mid, and possibly also with the 'bitmovin key'.
 *
 * @author Michiel Meeuwissen
 * @since 7.8
 */
@XmlRootElement
@Data
@XmlType(propOrder = {
    "mid",
    "token",
    "key"
})
@JsonTypeName("tokenResponse")
public class TokenResponse {
    private final String mid;
    private final String token;
    private final String key;
    private final String analyticsKey;

    private final String endpoint;

    @lombok.Builder
    @JsonCreator
    private TokenResponse(String mid, String token, String key, String analyticsKey, String endpoint) {
        this.mid = mid;
        this.token = token;
        this.key = key;
        this.analyticsKey = analyticsKey;
        this.endpoint = endpoint;
    }

}
