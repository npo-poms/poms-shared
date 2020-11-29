package nl.vpro.domain.media.support;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.support.License;
import nl.vpro.domain.media.Schedule;
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

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<Image>> validate = validator.validate(image);
        assertThat(validate).isEmpty();
    }

    @Test
    public void json() {
        Image image = new Image();
        image.setSource(null);
        image.setImageUri("urn:vpro:image:123");
        image.setCreationInstant(LocalDate.of(2016, 11, 9).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant());
        image.setOffset(java.time.Duration.ofMillis(100));
        image.setLicense(License.CC_BY);


        Image result = Jackson2TestUtil.roundTripAndSimilarAndEquals(image, "{\n" +
            "  \"imageUri\" : \"urn:vpro:image:123\",\n" +
            "  \"offset\" : 100,\n" +
            "  \"license\" : \"CC_BY\",\n" +
            "  \"owner\" : \"BROADCASTER\",\n" +
            "  \"type\" : \"PICTURE\",\n" +
            "  \"highlighted\" : false,\n" +
            "  \"creationDate\" : 1478646000000,\n" +
            "  \"workflow\" : \"FOR_PUBLICATION\"\n" +
            "}");
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


        JAXBTestUtil.roundTripAndSimilar(image, "<local:image owner=\"BROADCASTER\" type=\"PICTURE\" highlighted=\"false\" creationDate=\"2016-11-09T00:00:00+01:00\" workflow=\"FOR PUBLICATION\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:local=\"uri:local\">\n" +
            "    <shared:imageUri>urn:vpro:image:123</shared:imageUri>\n" +
            "    <shared:offset>P0DT0H0M0.100S</shared:offset>\n" +
            "    <shared:license>CC_BY</shared:license>\n" +
            "</local:image>");

    }
}
