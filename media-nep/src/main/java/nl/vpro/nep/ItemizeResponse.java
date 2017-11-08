package nl.vpro.nep;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @since 5.5
 */
@Data
@NoArgsConstructor
public class ItemizeResponse {
    private Boolean success;
    private Integer id;
    private String output_filename;



}
