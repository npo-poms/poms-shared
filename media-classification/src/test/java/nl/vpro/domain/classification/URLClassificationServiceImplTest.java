package nl.vpro.domain.classification;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class URLClassificationServiceImplTest {

    @Test
    public void test() throws MalformedURLException, InterruptedException, URISyntaxException {
        URL url = getClass().getResource("/nl/vpro/domain/media/classification/ebu_ContentGenreCS.xml");
        //URL url = new URL("http://publish.pages.omroep.nl/schema/classification");
        //URL url = new URL("http://localhost:8060/schema/classification");
        CachedURLClassificationServiceImpl service = new CachedURLClassificationServiceImpl(url.toURI());
        service.setCheckIntervalInSeconds(1);

        assertThat(service.values().size()).isGreaterThan(10);


        // call another time to show that it would not be loaded again.
        assertThat(service.values().size()).isGreaterThan(11);
/*

        for (int i = 0; i < 100; i++) {
            System.out.println(service.getLastModified());
            service.values();
            Thread.sleep(1000);
        }
*/


    }

}
