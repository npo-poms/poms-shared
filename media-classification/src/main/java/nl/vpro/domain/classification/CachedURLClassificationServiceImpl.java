package nl.vpro.domain.classification;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.SortedMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * If not using spring for ehcache configuration, this can be used to cache 5 minutes (or some other interval).
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class CachedURLClassificationServiceImpl extends URLClassificationServiceImpl {


    private long lastCheck = 0;

    private long checkIntervalInMilliseconds = 300 * 1000L;

    @Inject
    public CachedURLClassificationServiceImpl(
        @Named("pageupdate-api.baseUrl") String url) throws MalformedURLException {
        this(URI.create(url + "schema/classification/"));
    }

    public CachedURLClassificationServiceImpl(URI url) {
        super(url);
    }

    @Override
    protected SortedMap<TermId, Term> getTermsMap() {
        if (terms == null || System.currentTimeMillis() > lastCheck + checkIntervalInMilliseconds) {
            lastCheck = System.currentTimeMillis();
            return super.getTermsMap();
        }
        return terms;
    }

    public long getCheckIntervalInSeconds() {
        return checkIntervalInMilliseconds / 1000;
    }

    public void setCheckIntervalInSeconds(long checkIntervalInSeconds) {
        this.checkIntervalInMilliseconds = checkIntervalInSeconds * 1000;
    }
}
