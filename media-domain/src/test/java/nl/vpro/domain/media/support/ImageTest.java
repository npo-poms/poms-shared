package nl.vpro.domain.media.support;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public class ImageTest {


    @Test
    public void valid() {
        Image image = new Image();
        image.setSource(null);
        image.setImageUri("urn:vpro:image:123");

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<Image>> validate = validator.validate(image);
        assertThat(validate).isEmpty();


    }
}
