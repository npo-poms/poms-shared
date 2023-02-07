package nl.vpro.domain.classification;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class URLClassificationServiceImplTest {

    public static URI publicURL = URI.create("https://publish.pages.omroep.nl/schema/classification");
    //URL url = new URL("http://localhost:8060/schema/classification");

    @Test
    public void testResource() throws URISyntaxException {
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
    public void testCachingURL() {
        CachedURLClassificationServiceImpl service = new CachedURLClassificationServiceImpl(publicURL);
        service.setCheckIntervalInSeconds(1);

        assertThat(service.values().size()).isGreaterThan(10);
        assertThat(service.getCode()).isEqualTo(200);


        // call another time to show that it would not be loaded again.
        assertThat(service.values().size()).isGreaterThan(11);
        assertThat(service.getCode()).isEqualTo(200);
    }


    @Test
    public void testURL() {
        URLClassificationServiceImpl service = new URLClassificationServiceImpl(publicURL);
        service.getResource().setMinAge(Duration.ZERO);

        assertThat(service.values().size()).isGreaterThan(10);
        assertThat(service.getCode()).isEqualTo(200);
        Instant load = service.getLastLoad();
        assertThat(load).isAfter(service.getLastModified());

        // call another time to show that it would not be loaded again.
        assertThat(service.values().size()).isGreaterThan(11);
        assertThat(service.getLastLoad()).isEqualTo(load);
        assertThat(service.getCode()).isEqualTo(304);
    }

}
