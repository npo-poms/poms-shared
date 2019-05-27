package nl.vpro.nep.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;

/**

 * @since 5.5
 */
@Data
@NoArgsConstructor
public class WideVineRequest {
    private String client_ip;
    private String authorization_key;

    public WideVineRequest(@Nonnull String client_ip, @Nonnull String authorization_key) {
        this.client_ip = client_ip;
        this.authorization_key = authorization_key;
    }
}
