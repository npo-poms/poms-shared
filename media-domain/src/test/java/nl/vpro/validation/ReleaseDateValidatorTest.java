package nl.vpro.validation;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class ReleaseDateValidatorTest {

    ReleaseDateValidator validator = new ReleaseDateValidator();
    @Test
    public void isValid() throws Exception {

        assertThat(validator.isValid("123", null)).isFalse();
        assertThat(validator.isValid("2016-11", null)).isFalse();

    }

}
