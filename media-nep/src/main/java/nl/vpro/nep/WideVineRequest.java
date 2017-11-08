package nl.vpro.nep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**

 * @since 5.5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WideVineRequest {
    private String client_ip;
    private String authorization_key;
}
