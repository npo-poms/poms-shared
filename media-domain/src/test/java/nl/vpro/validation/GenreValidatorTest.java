package nl.vpro.validation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.Genre;
import nl.vpro.domain.media.MediaClassificationService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class GenreValidatorTest {

    @BeforeAll
    public static void init() {
        ClassificationServiceLocator.setInstance(MediaClassificationService.getInstance());
    }
    @Test
    public void testIsValid() {
        GenreValidator validator = new GenreValidator();
        assertFalse(validator.isValid(new Genre("4.0.1"), null));
        assertTrue(validator.isValid(new Genre("3.0.1.1.11"), null));

    }


}
