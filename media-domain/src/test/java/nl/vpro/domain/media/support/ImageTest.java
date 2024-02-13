package nl.vpro.domain.media.support;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.*;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.support.License;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

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
        image.setLicense(License.CC_BY);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Image>> validate = validator.validate(image);
            assertThat(validate).isEmpty();
        }
    }

    @Test
    public void json() {
        Image image = new Image();
        image.setSource(null);
        image.setImageUri("urn:vpro:image:123");
        image.setCreationInstant(LocalDate.of(2016, 11, 9).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant());
        image.setOffset(java.time.Duration.ofMillis(100));
        image.setLicense(License.CC_BY);


        Image result = Jackson2TestUtil.roundTripAndSimilarAndEquals(image, """
            {
              "imageUri" : "urn:vpro:image:123",
              "offset" : 100,
              "license" : "CC_BY",
              "owner" : "BROADCASTER",
              "type" : "PICTURE",
              "highlighted" : false,
              "creationDate" : 1478646000000,
              "workflow" : "FOR_PUBLICATION"
            }""");
        assertThat(result.getLicense()).isEqualTo(License.CC_BY);
    }

    @Test
    public void xml() {
        Image image = new Image();
        image.setSource(null);
        image.setImageUri("urn:vpro:image:123");
        image.setCreationInstant(LocalDate.of(2016, 11, 9).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant());
        image.setOffset(java.time.Duration.ofMillis(100));
        image.setLicense(License.CC_BY);


        JAXBTestUtil.roundTripAndSimilar(image, """
            <local:image owner="BROADCASTER" type="PICTURE" highlighted="false" creationDate="2016-11-09T00:00:00+01:00" workflow="FOR PUBLICATION" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:local="uri:local">
                <shared:imageUri>urn:vpro:image:123</shared:imageUri>
                <shared:offset>P0DT0H0M0.100S</shared:offset>
                <shared:license>CC_BY</shared:license>
            </local:image>""");

    }


    @Test
    public void setTitle() {
        Image image = new Image();
        image.setTitle("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        assertThat(image.getTitle()).hasSize(255);

        assertThat(image.getTitle()).startsWith("Lorem ipsum");
    }
}
