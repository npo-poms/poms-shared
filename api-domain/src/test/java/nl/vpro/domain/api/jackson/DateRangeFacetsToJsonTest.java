package nl.vpro.domain.api.jackson;

import java.time.Duration;
import java.time.Instant;
import nl.vpro.domain.api.*;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import org.junit.jupiter.api.Test;

class DateRangeFacetsToJsonTest {
    
    @Test
    public void json() {
        DateRangeFacets<?> dateRangeFacets = new DateRangeFacets<>();
        dateRangeFacets.addRanges(
            DateRangePreset.BEFORE_LAST_YEAR, 
            new DateRangeInterval(2, IntervalUnit.HOUR),
            new DateRangeFacetItem("bloe", Instant.EPOCH, Instant.EPOCH.plus(Duration.ofDays(1000)))
            );


        Jackson2TestUtil.roundTripAndSimilar(dateRangeFacets, """
            [ "BEFORE_LAST_YEAR", "2 HOURS", {
              "name" : "bloe",
              "begin" : 0,
              "end" : 86400000000
            } ]
            """);
    }

}