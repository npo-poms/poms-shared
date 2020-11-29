package nl.vpro.domain.classification;

import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class TermTest {
    private ClassificationService classificationService;


    @Before
    public void setup() throws URISyntaxException {
        URL url = getClass().getResource("/nl/vpro/domain/media/classification/ebu_ContentGenreCS.xml");
        classificationService = new URLClassificationServiceImpl(url.toURI());
    }

    @Test
    public void testIsTopTerm() {


        assertThat(classificationService.getTerm("3.0.1").isTopTerm()).isTrue();
        assertThat(classificationService.getTerm("3.0.1.1").isTopTerm()).isFalse();
        assertThat(classificationService.getTerm("3.0.1.1.2").isTopTerm()).isFalse();

/*
        assertThat(ClassificationService.getTerm("3.1").isTopTerm()).isTrue();
        assertThat(ClassificationService.getTerm("3.1.1.1").isTopTerm()).isFalse();
*/
    }
}
