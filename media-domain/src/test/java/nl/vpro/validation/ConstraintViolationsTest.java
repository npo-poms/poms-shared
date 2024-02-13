package nl.vpro.validation;

import java.util.Collection;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Title;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstraintViolationsTest {

    @Test
    public void testHumanReadable() {
        Title title = new Title("<h1>bla</h1", OwnerType.BROADCASTER, TextualType.MAIN);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Collection<ConstraintViolation<Title>> violations = factory.getValidator().validate(title);
            assertThat(ConstraintViolations.humanReadable(violations)).isEqualTo("\"value\" contains HTML");
        }



    }
}
