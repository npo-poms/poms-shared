package nl.vpro.nep;

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
public class ItemizeRequest  {

    private String identifier;
    private String starttime;
    private String endtime;

}
