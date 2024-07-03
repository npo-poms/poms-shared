package nl.vpro.berlijn.domain.epg;

import lombok.extern.log4j.Log4j2;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Range;

import nl.vpro.berlijn.domain.AssertValidatable;
import nl.vpro.util.Ranges;

@JsonIgnoreProperties({
    "s3FilePath",
    "ttl" // 'ignore that'
})
@Log4j2
public record EPGContents(
    LocalDate date,
    Instant lastUpdated,
    List<EPGEntry> entries,
    Instant created,
    Instant periodStart,
    String  channelId,
    Instant periodEnd
) implements AssertValidatable {

    public Range<Instant> asRange() {
        return Ranges.closedOpen(periodStart, periodEnd);
    }

    @Override
    public void assertValid() {
        record Key(Instant start) {
        }
        var  keys = new HashMap<Key, String>();
        entries().forEach(e -> {
            String existing = keys.put(new Key(e.guideStartTime()), e.prid());
            if (existing != null) {
                log.warn("{} and {} both on {}", e.prid(), existing, e.guideStartTime());
            }
            // don't do this, it is known that sometimes there are invalid entries.
            //e.assertValid();
        });

    }

}
