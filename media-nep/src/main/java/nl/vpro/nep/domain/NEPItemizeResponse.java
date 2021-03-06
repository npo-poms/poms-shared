package nl.vpro.nep.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @since 5.5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
public class NEPItemizeResponse {
    private Boolean success;
    private String id;
    private String  output_filename;

    public boolean isSuccess() {
        return success != null && success;
    }
}
