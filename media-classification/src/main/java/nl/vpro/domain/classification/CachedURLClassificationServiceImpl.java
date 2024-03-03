package nl.vpro.domain.classification;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.SortedMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * If not using spring for ehcache configuration, this can be used to cache 5 minutes (or some other interval).
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class CachedURLClassificationServiceImpl extends URLClassificationServiceImpl {


    Instant lastCheck = Instant.EPOCH;

    private Duration checkInterval = Duration.ofSeconds(300);

    private long hits = 0;
    private long misses = 0;

    @Inject
    public CachedURLClassificationServiceImpl(
        @Named("npo-pages_publisher.baseUrl") String url) {
        this(URI.create(url + (url.endsWith("/") ? "": "/") + "schema/classification"));
    }

    public CachedURLClassificationServiceImpl(URI url) {
        super(url);
    }

    @Override
    protected synchronized SortedMap<TermId, Term> getTermsMap() {
        if (terms == null || Instant.now().isAfter(lastCheck.plus(checkInterval))) {
            lastCheck = Instant.now();
            misses++;
            return super.getTermsMap();
        }
        hits++;
        return terms;
    }

    public long getCheckIntervalInSeconds() {
        return checkInterval.getSeconds();
    }

    public void setCheckIntervalInSeconds(long checkIntervalInSeconds) {
        this.checkInterval = Duration.ofSeconds(checkIntervalInSeconds);
    }

    public void setCheckInterval(Duration duration) {
        this.checkInterval = duration;
    }
    public Duration getCheckInterval() {
        return checkInterval;
    }

    public long getHits() {
        return hits;
    }

    public long getMisses() {
        return misses;
    }
}
