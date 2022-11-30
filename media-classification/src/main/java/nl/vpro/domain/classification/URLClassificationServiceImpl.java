package nl.vpro.domain.classification;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.cache.annotation.CacheResult;
import javax.xml.parsers.ParserConfigurationException;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.xml.sax.InputSource;

import nl.vpro.util.URLResource;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
@Slf4j
@ToString(callSuper = true)
public class URLClassificationServiceImpl extends AbstractClassificationServiceImpl  implements Consumer<SortedMap<TermId, Term>> {

    final URLResource<SortedMap<TermId, Term>> resource;

    public URLClassificationServiceImpl(URI url) {
        this.resource = create(url);
    }

    URLResource<SortedMap<TermId, Term>> create(final URI url) {
        Function<InputStream, SortedMap<TermId, Term>> f = inputStream -> {
            try {
                InputSource source = new InputSource(inputStream);
                source.setSystemId(url.toURL().toExternalForm());
                return create(source);
            } catch (MalformedURLException e) {
                log.error(e.getMessage(), e);
                return null;
            }
        };
        return new URLResource<>(url, f, this);
    }

    public URLClassificationServiceImpl(String url) {
        this(URI.create(url));
    }

    @Override
    @Nullable
    protected List<InputSource> getSources(boolean init) {
        return null;
    }

    @Override
    @CacheResult(cacheName = "URLClassificationServiceImpl")
    protected synchronized SortedMap<TermId, Term> getTermsMap() {
        resource.get();
        return super.getTermsMap();
    }

    @Nullable
    private SortedMap<TermId, Term> create(InputSource inputSource){
        try{
            return readTerms(Collections.singletonList(inputSource));
        } catch(ParserConfigurationException e){
            log.error(e.getMessage(),e);
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        URLClassificationServiceImpl that = (URLClassificationServiceImpl) o;

        return resource != null ? resource.equals(that.resource) : that.resource == null;
    }

    @Override
    public int hashCode() {
        return resource != null ? resource.hashCode() : 0;
    }

    public Instant getLastLoad() {
        return resource.getLastLoad();
    }

    public Integer getCode() {
        return resource.getCode();
    }

    public URLResource<SortedMap<TermId, Term>> getResource() {
        return resource;
    }

    @Override
    public void accept(SortedMap<TermId, Term> termIdTermSortedMap) {
        this.terms = termIdTermSortedMap;
    }
}
