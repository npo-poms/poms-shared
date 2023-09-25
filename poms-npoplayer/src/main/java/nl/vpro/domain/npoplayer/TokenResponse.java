package nl.vpro.domain.npoplayer;

import lombok.*;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
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
