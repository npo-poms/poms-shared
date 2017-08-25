package nl.vpro.domain.support;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LicenseTest {


    @Test
    public void getId() throws Exception {
        License testLicense = new License("testid");
        assertThat(testLicense.getId()).isEqualTo("testid");
    }


    @Test
    public void getIdShouldReturnLicenseId() throws Exception {
        License testLicence = License.CC_BY;
        assertThat(testLicence.getId()).isEqualTo("CC_BY");
    }

    @Test
    public void allsShouldReturnLicenseId() throws Exception {

        License testLicense = new License();
        assertThat(testLicense.values()[0].getId()).isEqualTo("COPYRIGHTED");

    }

    @Test
    public void getLicenseById() throws Exception {

        License testLicense = new License();
        assertThat(testLicense.getLicenseById("COPYRIGHTED")).isEqualTo(License.COPYRIGHTED);
    }


    @Test
    public void getLicenseInvalidId() throws Exception {
        ValidatorFactory config = Validation.buildDefaultValidatorFactory();
        Validator validator = config.getValidator();
        License testLicense = new License("bla");
        Set<ConstraintViolation<License>> validate = validator.validate(testLicense);
        assertThat(validate).isNotEmpty();

        assertThat(validator.validate(License.PUBLIC_DOMAIN)).isEmpty();

    }

}
