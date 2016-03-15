package nl.vpro.domain.classification;

import java.io.IOException;
import java.net.*;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

import javax.cache.annotation.CacheResult;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class URLClassificationServiceImpl extends AbstractClassificationServiceImpl {

    private static final String RFC822 = "EEE, dd MMM yyyy HH:mm:ss z";


    final URI url;

    public URLClassificationServiceImpl(URI url) {
        this.url = url;
    }


    @Deprecated
    public URLClassificationServiceImpl(URL url) {
        try {
            this.url = url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public URLClassificationServiceImpl(String url) {
        this.url = URI.create(url);
    }

    @Override
    protected List<InputSource> getSources(boolean init) {
        try {
            URLConnection connection = url.toURL().openConnection();
            lastModified = Instant.ofEpochMilli(connection.getHeaderFieldDate("Last-Modified", System.currentTimeMillis()));
            InputSource source = new InputSource(connection.getInputStream());
            source.setSystemId(url.toURL().toExternalForm());
            return Collections.singletonList(source);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return null;
        }

    }

    @Override
    @CacheResult(cacheName = "URLClassificationServiceImpl")
    protected SortedMap<TermId, Term> getTermsMap() {
        try {
            URLConnection connection = url.toURL().openConnection();
            boolean ifModifiedCheck = connection instanceof HttpURLConnection;
            int code;
            if (ifModifiedCheck && lastModified != null) {
                connection.setRequestProperty("If-Modified-Since", DateTimeFormatter.RFC_1123_DATE_TIME.format(lastModified.atOffset(ZoneOffset.UTC)));
                code = ((HttpURLConnection) connection).getResponseCode();
            } else {
                code = HttpServletResponse.SC_OK;
            }
            switch (code) {
                case HttpServletResponse.SC_NOT_MODIFIED:
                    LOG.debug("Not modified " + url);
                    break;
                case HttpServletResponse.SC_OK:
                    InputSource input = new InputSource(connection.getInputStream());
                    input.setSystemId(url.toURL().toExternalForm());
                    Instant prevMod = lastModified;
                    try {
                        terms = readTerms(Collections.singletonList(input));
                    } catch (ParserConfigurationException e) {
                        LOG.error(e.getMessage(), e);
                    }
                    lastModified = Instant.ofEpochMilli(connection.getHeaderFieldDate("Last-Modified", System.currentTimeMillis()));
                    if (ifModifiedCheck) {
                        LOG.info("Reloaded " + url + " as it is modified since " + prevMod + " -> " + lastModified);
                    }
                    break;
                default:
                    LOG.error(code + ":" + connection);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return super.getTermsMap();
    }


    @Override
    public String toString() {
        return String.valueOf(url);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        URLClassificationServiceImpl that = (URLClassificationServiceImpl) o;

        return url != null ? url.equals(that.url) : that.url == null;

    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
