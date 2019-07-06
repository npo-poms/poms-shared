package nl.vpro.nep.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**

 * @since 5.5
 */
@Data
@NoArgsConstructor
public class PlayreadyRequest {
    private String client_ip;
    private String authorization_key;


    public PlayreadyRequest(String client_ip, String authorization_key) {
        this.client_ip = client_ip;
        this.authorization_key = authorization_key;
    }
}
