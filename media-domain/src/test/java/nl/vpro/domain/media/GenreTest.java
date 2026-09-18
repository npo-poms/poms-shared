package nl.vpro.domain.media;

import java.time.LocalDate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
class GenreTest {


    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void setup() {
        ClassificationServiceLocator.setInstance(MediaClassificationService.getInstance());
    }


    @Test
    void matchLegacy() {
        assertThat(EpgGenreType.valueOfLegacy(asList(MisGenreType.YOUTH, MisGenreType.ENTERTAINMENT))).containsOnly(EpgGenreType._0106);
        assertThat(EpgGenreType.valueOfLegacy(asList(MisGenreType.ENTERTAINMENT, MisGenreType.YOUTH))).containsOnly(EpgGenreType._0106);
        assertThat(EpgGenreType.valueOfLegacy(singletonList(MisGenreType.RELIGIOUS))).containsOnly(EpgGenreType._0726);
    }

    @Test
    void legacy2() {
        assertThat(EpgGenreType.valueOf(MisGenreType.ENTERTAINMENT, MisGenreType.YOUTH)).containsOnly(EpgGenreType._0106);
        assertThat(EpgGenreType.valueOf(MisGenreType.ENTERTAINMENT, MisGenreType.INFORMATIVE, MisGenreType.YOUTH)).containsOnly(EpgGenreType._0106, EpgGenreType._07);
    }

    @Test
    void getEpgTerms() {
        Genre genre = new Genre("3.0.1.2.10");

        JAXB.marshal(genre, System.out);
        JAXBTestUtil.roundTripAndSimilar(genre, """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <local:genre id="3.0.1.2.10" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:local="uri:local">
                <term>Film</term>
                <term>Spanning</term>
            </local:genre>""");
    }

/*
    @Test
    void getEbuTerms() throws Exception {
        Genre genre = new Genre("3.1.1.1");

        JAXBTestUtil.roundTripAndSimilar(genre, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<genre id=\"3.1.1.1\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <term>News / Pure information</term>\n" +
            "    <term>Daily news</term>\n" +
            "</genre>\n");

    }
*/


    @Test
    void compareToWhenSmaller() {
        assertThat(new Genre("3.0.1.7.27").compareTo(new Genre("3.0.100.7.27"))).isNegative();
    }

    @Test
    void compareToWhenGreater() {
        assertThat(new Genre("3.0.100.7.27").compareTo(new Genre("3.0.1.7.27"))).isPositive();
    }

    @Test
    void equality() {
        assertThat(new Genre("3.0.1.7.27").equals(new Genre("3.0.1.7.27"))).isTrue();
    }

    @Test
    void getFirstVersionDate() {
        assertThat(new Genre("3.0.1.1.25").getFirstVersionDate()).isEqualTo(LocalDate.of(2014,4,8));
        assertThat(new Genre("3.0.1.1.41").getFirstVersionDate()).isEqualTo(LocalDate.of(2021,4,1));
    }

    @Test
    void validation() {
        assertThat(VALIDATOR.validate(new Genre("3.0.1.2"))).isEmpty();
        assertThat(VALIDATOR.validate(new Genre("3.0.1"))).hasSize(1);
    }
}
