package nl.vpro.domain.image.backend;

import javax.validation.*;

import javax.validation.groups.Default;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.support.License;
import nl.vpro.validation.WarningValidatorGroup;

import static org.assertj.core.api.Assertions.assertThat;

class ImageTest {

    private final Validator validator;

    {
        try (ValidatorFactory factory = Validation
            .byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void validation() {
        Image image = Image.builder()
            .license(License.COPYRIGHTED)
            .title("bla")
            .heightInMm(1f)
            .build();

        assertThat(validator.validate(image)).isEmpty();
    }
    @Test
    public void invalidation() {
        Image image = Image.builder()
            .license(null)
            .title("bla")
            .heightInMm(-1f)
            .build();

        assertThat(validator.validate(image, WarningValidatorGroup.class, Default.class)).hasSize(2);
    }

}
