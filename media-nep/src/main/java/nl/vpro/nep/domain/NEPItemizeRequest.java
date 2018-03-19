package nl.vpro.nep.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
public class NEPItemizeRequest {

    private String identifier;

    /**
     * Can be duration like (existing items) or absolute time (live knipper)
     */
    private String starttime;
    private String endtime;

}
