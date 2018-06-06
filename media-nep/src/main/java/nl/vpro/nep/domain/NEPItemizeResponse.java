package nl.vpro.nep.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @since 5.5
 */
@Data
@NoArgsConstructor
public class NEPItemizeResponse {
    private Boolean success;
    private Integer id;
    private String output_filename;

    public boolean isSuccess() {
        return success != null && success;
    }
}
