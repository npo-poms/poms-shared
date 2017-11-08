package nl.vpro.nep;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @since 5.5
 */
@Data
@NoArgsConstructor
public class PlayreadyResponse {
    private boolean success;
    private String token;
}
