package nl.vpro.nep.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
@ToString
public class NEPItemizeRequest {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS");


    private String identifier;

    /**
     * Can be duration like (existing items) or absolute time (live knipper)
     */
    private String starttime;
    private String endtime;

    private Integer max_bitrate;


    public static Optional<String> fromDuration(Duration duration) {
        if (duration == null) {
            return Optional.empty();
        }
        return Optional.of(DurationFormatUtils.formatDurationHMS(duration.toMillis()));
    }

    public static String fromDuration(Duration duration, Duration defaultDuration) {
         if (duration == null) {
            duration = defaultDuration;
        }
        return DurationFormatUtils.formatDurationHMS(duration.toMillis());
    }


    public static Optional<String> fromInstant(Instant instant) {
        if (instant== null) {
            return Optional.empty();
        }
        LocalDateTime localDateTime = instant.atZone(ZoneId.of("UTC")).toLocalDateTime();
        return Optional.of(FORMATTER.format(localDateTime));
    }


    public String guessOutput_filename() {
        return identifier + "_" + starttime.replaceAll("[.:]", "") + "-" + endtime.replaceAll("[.:]", "") + ".mp4";
    }

}
