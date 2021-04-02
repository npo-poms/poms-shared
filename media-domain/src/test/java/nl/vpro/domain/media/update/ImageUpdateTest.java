package nl.vpro.domain.media.update;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.support.License;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
@Slf4j
public class ImageUpdateTest {

    @Test
    public void xml() {
        ImageUpdate update = new ImageUpdate(ImageType.PICTURE, "title", null, new ImageLocation("http://placehold.it/150/7735a"));
        update.setLicense(License.CC_BY);
        update.setSourceName("placeholdit");
        update.setCredits(getClass().getName());
        JAXBTestUtil.roundTripAndSimilar(update, "\n" +
            "<image type=\"PICTURE\" highlighted=\"false\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <title>title</title>\n" +
            "    <sourceName>placeholdit</sourceName>\n" +
            "    <license>CC_BY</license>\n" +
            "    <credits>nl.vpro.domain.media.update.ImageUpdateTest</credits>\n" +
            "    <imageLocation>\n" +
            "        <url>http://placehold.it/150/7735a</url>\n" +
            "    </imageLocation>\n" +
            "</image>");

    }

    @Test
    public void json() {
         ImageUpdate update = new ImageUpdate(ImageType.PICTURE, "title", null, new ImageLocation("http://placehold.it/150/7735a"));
        update.setLicense(License.CC_BY);
        update.setSourceName("placeholdit");
        update.setCredits(getClass().getName());
        Jackson2TestUtil.roundTripAndSimilar(update, "{\n" +
            "  \"title\" : \"title\",\n" +
            "  \"sourceName\" : \"placeholdit\",\n" +
            "  \"license\" : \"CC_BY\",\n" +
            "  \"credits\" : \"nl.vpro.domain.media.update.ImageUpdateTest\",\n" +
            "  \"image\" : {\n" +
            "    \"imageLocation\" : {\n" +
            "      \"url\" : \"http://placehold.it/150/7735a\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"type\" : \"PICTURE\",\n" +
            "  \"highlighted\" : false\n" +
            "}");
    }


    @Test
    public void xmlBackwards() {
        ImageUpdate update = new ImageUpdate(ImageType.PICTURE, "title", null, new ImageLocation("http://placehold.it/150/7735a"));
        update.setLicense(License.CC_BY);
        update.setCredits(getClass().getName());
        JAXBTestUtil.roundTripAndSimilar(update, "\n" +
            "<image type=\"PICTURE\" highlighted=\"false\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <title>title</title>\n" +
            "    <license>CC_BY</license>\n" +
            "    <credits>nl.vpro.domain.media.update.ImageUpdateTest</credits>\n" +
            "    <imageLocation>\n" +
            "        <url>http://placehold.it/150/7735a</url>\n" +
            "    </imageLocation>\n" +
            "</image>");

    }


    @Test
    public void xmlLicense() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ImageUpdate update = new ImageUpdate(ImageType.PICTURE, "title", null, new ImageLocation("http://placehold.it/150/7735a"));

        // If you insist an invalid License can be created. It won't be valid though!
        Constructor<License> constructor = License.class.getDeclaredConstructor(String.class);
        constructor.setAccessible(true);
        update.setLicense(constructor.newInstance("blabla"));
        update.setSourceName("placeholdit");
        update.setCredits(getClass().getName());
        JAXBTestUtil.roundTripAndSimilar(update, "\n" +
            "<image type=\"PICTURE\" highlighted=\"false\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <title>title</title>\n" +
            "    <sourceName>placeholdit</sourceName>\n" +
            "   <license>blabla</license>\n" +
            "    <credits>nl.vpro.domain.media.update.ImageUpdateTest</credits>\n" +
            "    <imageLocation>\n" +
            "        <url>http://placehold.it/150/7735a</url>\n" +
            "    </imageLocation>\n" +
            "</image>");
        assertThat(update.violations()).isNotEmpty();
        log.info("v:{}", update.violations());
    }


}
