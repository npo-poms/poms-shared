package nl.vpro.domain.classification;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class URLClassificationServiceImplTest {


    @Test
    public void testResource() throws MalformedURLException, InterruptedException, URISyntaxException {
        URL url = getClass().getResource("/nl/vpro/domain/media/classification/ebu_ContentGenreCS.xml");

        CachedURLClassificationServiceImpl service = new CachedURLClassificationServiceImpl(url.toURI());
        service.setCheckIntervalInSeconds(1);

        assertThat(service.values().size()).isGreaterThan(10);
        assertThat(service.getCode()).isEqualTo(200);


        // call another time to show that it would not be loaded again.
        assertThat(service.values().size()).isGreaterThan(11);
        assertThat(service.getCode()).isEqualTo(200);


    }


    @Test
    public void testCachingURL() throws MalformedURLException, InterruptedException, URISyntaxException {
        URL url = new URL("http://publish.pages.omroep.nl/schema/classification");
        CachedURLClassificationServiceImpl service = new CachedURLClassificationServiceImpl(url.toURI());
        service.setCheckIntervalInSeconds(1);

        assertThat(service.values().size()).isGreaterThan(10);
        assertThat(service.getCode()).isEqualTo(200);


        // call another time to show that it would not be loaded again.
        assertThat(service.values().size()).isGreaterThan(11);
        assertThat(service.getCode()).isEqualTo(200);

    }


    @Test
    public void testURL() throws MalformedURLException, InterruptedException, URISyntaxException {
        URL url = new URL("http://publish.pages.omroep.nl/schema/classification");
        //URL url = new URL("http://localhost:8060/schema/classification");
        URLClassificationServiceImpl service = new URLClassificationServiceImpl(url.toURI());

        assertThat(service.values().size()).isGreaterThan(10);
        assertThat(service.getCode()).isEqualTo(200);
        Instant load = service.getLastLoad();
        assertThat(load.isAfter(service.getLastModified()));

        // call another time to show that it would not be loaded again.
        assertThat(service.values().size()).isGreaterThan(11);

        assertThat(service.getLastLoad()).isEqualTo(load);
        assertThat(service.getCode()).isEqualTo(304);




    }

}
