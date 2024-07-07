package nl.vpro.domain.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.StringReader;
import java.util.Set;

import jakarta.validation.*;
import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.junit.jupiter.api.Test;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
public class LicenseTest {

    @Test
    public void getIdShouldReturnLicenseId() {
        License testLicence = License.CC_BY;
        assertThat(testLicence.getId()).isEqualTo("CC_BY");
    }

    @Test
    public void allsShouldReturnLicenseId() {
        assertThat(License.values()[0].getId()).isEqualTo("COPYRIGHTED");

        for (License l : License.values()) {
            if (l.display()) {
                log.info(l.getId() + " " + l.getDisplayName() + ":" + l.getUrl());
                assertThat(l.isDeprecated()).isFalse();
            } else {
                if (l.getId().endsWith("0")) {
                    assertThat(l.isDeprecated()).isTrue();
                } else {
                    log.info("HIDDEN " + l.getId() + " " + l.getDisplayName() + ":" + l.getUrl());
                }
            }
        }

    }

    @Test
    public void getLicenseById() {
        assertThat(License.valueOf("COPYRIGHTED")).isEqualTo(License.COPYRIGHTED);
    }


    @Test
    public void json() {
        Jackson2TestUtil.roundTripAndSimilar(new A(License.CC_BY), """
            {
              "license" : "CC_BY"
            }""");
    }

    @Test
    public void xml() {
        JAXBTestUtil.roundTripAndSimilar(new A(License.CC_BY), """
            <a>
                <license>CC_BY</license>
            </a>
            """);
    }

    ValidatorFactory config = Validation.buildDefaultValidatorFactory();
    Validator validator = config.getValidator();

    @Test
    public void validator() {
        assertThat(validator.validate(License.PUBLIC_DOMAIN)).isEmpty();
    }


    @Test
    public void futureIdsCanBeUnmarshalledXml() {
        A a = JAXB.unmarshal(new StringReader("""
            <a>
                <license>FUTURE_LICENSE</license>
            </a>"""), A.class);
        License testLicense = a.getLicense();
        assertThat(testLicense.getId()).isEqualTo("FUTURE_LICENSE");

        Set<ConstraintViolation<License>> validate = validator.validate(testLicense);
        assertThat(validate).isNotEmpty();
    }


    @Test
    public void futureIdsCanBeUnmarshalledJson() throws Exception {
        A a = Jackson2Mapper.getInstance().readValue(new StringReader("""
            {
              "license" : "FUTURE_LICENSE"
            }"""), A.class);
        License testLicense = a.getLicense();
        assertThat(testLicense.getId()).isEqualTo("FUTURE_LICENSE");
    }


    @Test
    public void futureIdsAreInvalidThough() throws Exception {
        A a = Jackson2Mapper.getInstance().readValue(new StringReader("""
            {
              "license" : "FUTURE_LICENSE"
            }"""), A.class);
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
