package nl.vpro.domain.user;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import jakarta.validation.*;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
public class EditorTest {

    private Validator validator;
    {
        try {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        } catch (ValidationException ve) {
            log.info(ve.getClass().getName() + " " + ve.getMessage());
            validator = null;

        }
    }


    @Test
    public void testValidation() {
        User user = Editor.builder()
            .email("Vera.van.Slooten@ntr.nl")
            .build();

        Set<ConstraintViolation<User>> validate = validator.validate(user);
        assertThat(validate).isEmpty();
    }

}
