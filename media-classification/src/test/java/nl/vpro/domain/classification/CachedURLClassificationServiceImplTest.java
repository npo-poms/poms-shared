package nl.vpro.domain.classification;

import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public class CachedURLClassificationServiceImplTest {


    @Test
    public void test() throws URISyntaxException {
        URL url = getClass().getResource("/nl/vpro/domain/media/classification/ebu_ContentGenreCS.xml");
        CachedURLClassificationServiceImpl cs = new CachedURLClassificationServiceImpl(url.toURI());
        cs.getTermsMap();
        assertThat(cs.getHits()).isEqualTo(0);
        assertThat(cs.getMisses()).isEqualTo(1);

        cs.getTermsMap();
        assertThat(cs.getMisses()).isEqualTo(1);
        assertThat(cs.getHits()).isEqualTo(1);

        cs.lastCheck = Instant.now().minus(10, ChronoUnit.MINUTES);
        cs.getTermsMap();
        assertThat(cs.getMisses()).isEqualTo(2);
        assertThat(cs.getHits()).isEqualTo(1);
    }

}
