package nl.vpro.berlijn.domain.epg;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import nl.vpro.berlijn.domain.AssertValidatable;

/**
 * @param duration Duration of the schedule event in seconds. Seems to be the difference between {@link #guideStartTime} and {@link #endTime}.
 * @param startTime
 * @param endTime
 * @param guideStartTime What's the different with {@link #startTime}
 */
@JsonIgnoreProperties({
    "firstTransmissionDate" // contains mess. We don't use it any way for now. See https://publiekeomroep.atlassian.net/browse/VPPM-1167
})
public record EPGEntry(
    long duration,
    Instant guideStartTime,
    //Instant firstTransmissionDate, // wtf "firstTransmissionDate":"25-05-2021"
    boolean isLive,
    boolean isRerun,
    String crid,
    String guci,
    String prid,
    Instant startTime,
    Instant endTime,
    ProductOverride productOverride) implements AssertValidatable {

    @Override
    public void assertValid() {
        assert startTime != null : "no start time in " + this;

        if (guideStartTime != null && endTime != null) {
            //assert Objects.equals(Duration.between(guideStartTime, endTime), Duration.ofSeconds(duration)) : Duration.between(guideStartTime, endTime) + "!= " + Duration.ofSeconds(duration) + " in " + this;
        }
    }

}
