package nl.vpro.domain.support;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.StringReader;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Test;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class LicenseTest {



    @Test
    public void getIdShouldReturnLicenseId() {
        License testLicence = License.CC_BY;
        assertThat(testLicence.getId()).isEqualTo("CC_BY");
    }

    @Test
    public void allsShouldReturnLicenseId() {
        assertThat(License.values()[0].getId()).isEqualTo("COPYRIGHTED");

    }

    @Test
    public void getLicenseById() {
        assertThat(License.valueOf("COPYRIGHTED")).isEqualTo(License.COPYRIGHTED);
    }


    @Test
    public void json() {
        Jackson2TestUtil.roundTripAndSimilar(new A(License.CC_BY), "{\n" +
            "  \"license\" : \"CC_BY\"\n" +
            "}");
    }

    @Test
    public void xml() {
        JAXBTestUtil.roundTripAndSimilar(new A(License.CC_BY), "<a>\n" +
            "    <license>CC_BY</license>\n" +
            "</a>\n");
    }

    ValidatorFactory config = Validation.buildDefaultValidatorFactory();
    Validator validator = config.getValidator();

    @Test
    public void validator() {
        assertThat(validator.validate(License.PUBLIC_DOMAIN)).isEmpty();
    }


    @Test
    public void futureIdsCanBeUnmarshalledXml() {
        A a = JAXB.unmarshal(new StringReader("<a>\n" +
            "    <license>FUTURE_LICENSE</license>\n" +
            "</a>"), A.class);
        License testLicense = a.getLicense();
        assertThat(testLicense.getId()).isEqualTo("FUTURE_LICENSE");

        Set<ConstraintViolation<License>> validate = validator.validate(testLicense);
        assertThat(validate).isNotEmpty();
    }


    @Test
    public void futureIdsCanBeUnmarshalledJson() throws Exception {
        A a = Jackson2Mapper.getInstance().readValue(new StringReader("{\n" +
            "  \"license\" : \"FUTURE_LICENSE\"\n" +
            "}"), A.class);
        License testLicense = a.getLicense();
        assertThat(testLicense.getId()).isEqualTo("FUTURE_LICENSE");
    }


    @Test
    public void futureIdsAreInvalidThough() throws Exception {
        A a = Jackson2Mapper.getInstance().readValue(new StringReader("{\n" +
            "  \"license\" : \"FUTURE_LICENSE\"\n" +
            "}"), A.class);
        License testLicense = a.getLicense();


        assertThat(validator.validate(testLicense)).hasSize(1);
    }

    @Data
    @AllArgsConstructor
    @XmlRootElement
    public static class A {
        License license;
        protected A() {

        }
    }
}
