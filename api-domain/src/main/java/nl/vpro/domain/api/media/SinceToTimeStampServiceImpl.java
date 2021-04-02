package nl.vpro.domain.api.media;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Michiel Meeuwissen
  */
public class SinceToTimeStampServiceImpl implements SinceToTimeStampService {

    private final SortedSet<Entry> backend = new TreeSet<>();

    public SinceToTimeStampServiceImpl()  {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/sincetotimestamp.properties"));
            for (Map.Entry e : properties.entrySet()) {
                backend.add(new Entry(
                    Long.parseLong(String.valueOf(e.getKey())),
                    Instant.ofEpochMilli(Long.parseLong(String.valueOf(e.getValue())))));
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }


    @Override
    public Instant getInstance(Long since) {
        return since > DIVIDING_SINCE.toEpochMilli() ? Instant.ofEpochMilli(since) : lookupSince(since);

    }

    @Override
    public Long getSince(Instant since) {
        return since.toEpochMilli();

    }

    private Instant lookupSince(Long since) {
        Instant result = DIVIDING_SINCE;
        for (Entry e : backend) {
            if (since <= e.since) {
                result = e.instant;
            }
        }
        return result;
    }

    private static class Entry implements  Comparable<Entry> {
        final Long since;
        final Instant instant;

        private Entry(Long since, Instant instant) {
            this.since = since;
            this.instant = instant;
        }

        @Override
        public int compareTo(Entry o) {
            return -1 * since.compareTo(o.since);
        }
        @Override
        public String toString() {
            return since + ":" + instant;
        }
    }
}
