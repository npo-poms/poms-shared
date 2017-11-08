package nl.vpro.nep;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  *  @TODO this is NEP specifific You'd expect in in a package nl.vpro.nep or so.

 * @since 5.5
 */
@Data
@NoArgsConstructor
class WideVineResponse {
    private boolean success;
    private String token;
}
