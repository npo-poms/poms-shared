package nl.vpro.domain.gtaa;

import java.lang.reflect.InvocationTargetException;
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
        Scheme.classes();
        return Arrays.stream(Scheme.values()).filter(s -> s != Scheme.person).map(s -> new Object[]{s}).collect(Collectors.toList());
    }


    Scheme scheme;

    public ThesaurusObjectsTest(Scheme scheme) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        this.scheme = scheme;
    }

    @Test
    public void toThesaurusObject() {
        ThesaurusObject thesaurusObject = ThesaurusObjects.toThesaurusObject(Description
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
        ThesaurusObject thesaurusObject = ThesaurusObjects.toThesaurusObject(Description
            .builder()
            .inScheme(scheme.getUrl())
            .build());
        assertThat(ThesaurusObjects.toScheme(thesaurusObject)).isEqualTo(scheme);
    }
}
