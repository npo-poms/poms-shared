package nl.vpro.domain.gtaa;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import nl.vpro.w3.rdf.Description;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@RunWith(Parameterized.class)
public class ThesaurusObjectsTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.stream(Scheme.values()).filter(s -> s != Scheme.person).map(s -> new Object[]{s}).collect(Collectors.toList());
    }


    Scheme scheme;

    public ThesaurusObjectsTest(Scheme scheme) {
        this.scheme = scheme;
    }

    @Test
    public void toThesaurusObject() {
        GTAAConcept thesaurusObject = GTAAConcepts.toConcept(Description
            .builder()
            .inScheme(scheme.getUrl())
            .build());
        assertThat(thesaurusObject).isInstanceOf(scheme.getImplementation());
    }

    @Test
    public void toThesaurusObjectFromNew() {

    }

    @Test
    public void toScheme() {
        GTAAConcept thesaurusObject = GTAAConcepts.toConcept(Description
            .builder()
            .inScheme(scheme.getUrl())
            .build());
        assertThat(GTAAConcepts.toScheme(thesaurusObject)).isEqualTo(scheme);
    }
}
