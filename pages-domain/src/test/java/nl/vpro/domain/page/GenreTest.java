package nl.vpro.domain.page;

import java.io.IOException;
import java.io.StringReader;

import jakarta.validation.*;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.classification.ClassificationService;
import nl.vpro.domain.media.MediaClassificationService;
import nl.vpro.jackson2.Jackson2Mapper;

import static org.assertj.core.api.Assertions.assertThat;


public class GenreTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testJson() throws IOException {
        ClassificationService classificationService = new MediaClassificationService();
        Genre genre = new Genre(classificationService.getTerm("3.0.1.1.2"));
        String marshalled = Jackson2Mapper.getInstance().writeValueAsString(genre);
        assertThat(marshalled).isEqualTo("{\"id\":\"3.0.1.1.2\",\"terms\":[\"Jeugd\",\"Film\"],\"displayName\":\"Jeugd - Film\"}");

        Genre unmarshalled = Jackson2Mapper.getInstance().readValue(new StringReader(marshalled), Genre.class);
        assertThat(unmarshalled.getTermId()).isEqualTo("3.0.1.1.2");
    }

    @Test
    public void validation() {
        assertThat(VALIDATOR.validate(Genre.of("3.0.1.2"))).isEmpty();
        assertThat(VALIDATOR.validate(Genre.of("3.0.1"))).hasSize(1);
    }

}
