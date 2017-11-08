package nl.vpro.nep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  *  @TODO this is NEP specifific You'd expect in in a package nl.vpro.nep or so.

 * @since 5.5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class PlayreadyRequest {
    private String client_ip;
    private String authorization_key;
}
