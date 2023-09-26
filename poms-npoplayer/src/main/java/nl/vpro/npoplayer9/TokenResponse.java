package nl.vpro.npoplayer9;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Player 9 just needs a server side generated jws token.
 * <p>
 * This wraps it together with the original mid
 *
 * @author Michiel Meeuwissen
 * @since 7.8
 */
@XmlRootElement
@Data
@XmlType(propOrder = {
    "mid",
    "token"
})
@JsonTypeName("tokenResponse")
public class TokenResponse {
    private final String mid;
    private final String token;

    @lombok.Builder
    @JsonCreator
    public TokenResponse(String mid, String token) {
        this.mid = mid;
        this.token = token;
    }

}
