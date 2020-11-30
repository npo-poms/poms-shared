package nl.vpro.domain.gtaa;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runners.Parameterized;

import nl.vpro.w3.rdf.Description;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class ThesaurusObjectsTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.stream(Scheme.values()).filter(s -> s != Scheme.person).map(s -> new Object[]{s}).collect(Collectors.toList());
    }


    @ParameterizedTest
    @MethodSource("data")
    public void toThesaurusObject(Scheme scheme) {
        GTAAConcept thesaurusObject = GTAAConcepts.toConcept(Description
            .builder()
            .inScheme(scheme.getUrl())
            .build()).get();
        assertThat(thesaurusObject).isInstanceOf(scheme.getImplementation());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void toScheme(Scheme scheme) {
        GTAAConcept thesaurusObject = GTAAConcepts.toConcept(Description
            .builder()
            .inScheme(scheme.getUrl())
            .build()).get();
        assertThat(GTAAConcepts.toScheme(thesaurusObject)).isEqualTo(scheme);
    }
}
