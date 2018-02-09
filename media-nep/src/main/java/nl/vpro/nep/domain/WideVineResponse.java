package nl.vpro.nep.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @since 5.5
 */
@Data
@NoArgsConstructor
public class WideVineResponse {
    private boolean success;
    private String token;
}
