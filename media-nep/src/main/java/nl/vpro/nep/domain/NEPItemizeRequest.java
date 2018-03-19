package nl.vpro.nep.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

import org.apache.commons.lang3.time.DurationFormatUtils;

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


    public static String fromDuration(Duration duration) {
        if (duration == null) {
            return null;
        }
        return DurationFormatUtils.formatDurationHMS(duration.toMillis());
    }

}
