package nl.vpro.domain.image.backend;

import javax.validation.Validation;
import javax.validation.Validator;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.support.License;

import static org.assertj.core.api.Assertions.assertThat;

class ImageTest {


    @Test
    public void validation() {
        Image image = Image.builder()
            .license(License.COPYRIGHTED)
            .title("bla")
            .heightInMm(1f)
            .build();
        Validator validator = Validation
            .byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();
        assertThat(validator.validate(image)).isEmpty();;
    }

}
