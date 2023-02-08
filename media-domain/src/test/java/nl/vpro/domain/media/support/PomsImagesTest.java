package nl.vpro.domain.media.support;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.domain.image.ImageMetadata;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import static nl.vpro.domain.media.support.ImageUrlServiceHolder.IMAGE_SERVER_BASE_URL_PROPERTY;

class PomsImagesTest {

    @BeforeAll
    public static void beforeAll() {
        System.clearProperty(IMAGE_SERVER_BASE_URL_PROPERTY);
        ImageUrlServiceHolder.setInstance();
    }

    @Test
    public void json() throws JsonProcessingException {
        Image support = Jackson2Mapper.getLenientInstance().readValue("{\n" +
            "      \"title\": \"2Doc:\",\n" +
            "      \"description\": \"Filmmaakster Michal Weits duikt in het verleden van haar overgrootvader Joseph Weitz die, met geld van de Blue Box-campagne van het Joods Nationaal Fonds, Palestijns land aankocht en onteigende.\",\n" +
            "      \"imageUri\": \"urn:vpro:image:1646895\",\n" +
            "      \"offset\": 180000,\n" +
            "      \"height\": 1080,\n" +
            "      \"width\": 1920,\n" +
            "      \"credits\": \"Still 2Doc: / Blue Box\",\n" +
            "      \"sourceName\": \"VPRO\",\n" +
            "      \"license\": \"COPYRIGHTED\",\n" +
            "      \"date\": \"2021\",\n" +
            "      \"owner\": \"AUTHORITY\",\n" +
            "      \"type\": \"STILL\",\n" +
            "      \"highlighted\": false,\n" +
            "      \"creationDate\": 1639275163526,\n" +
            "      \"workflow\": \"PUBLISHED\",\n" +
            "      \"lastModified\": 1639275163526,\n" +
            "      \"urn\": \"urn:vpro:media:image:122414908\"\n" +
            "    }", Image.class);

        ImageMetadata imageMetadata = ImageMetadata.of(support);
        Jackson2TestUtil.assertThatJson(Jackson2Mapper.getModelInstance(), imageMetadata)
            .withoutUnmarshalling()
            .isSimilarTo("{\n" +
                "  \"type\" : \"STILL\",\n" +
                "  \"title\" : \"2Doc:\",\n" +
                "  \"height\" : 1080,\n" +
                "  \"width\" : 1920,\n" +
                "  \"sourceSet\" : {\n" +
                "    \"THUMBNAIL.WEBP\" : {\n" +
                "      \"url\" : \"https://images.poms.omroep.nl/image/s100/1646895.webp\",\n" +
                "      \"type\" : \"THUMBNAIL\",\n" +
                "      \"format\" : \"WEBP\",\n" +
                "      \"dimension\" : {\n" +
                "        \"width\" : 100,\n" +
                "        \"height\" : 56\n" +
                "      }\n" +
                "    },\n" +
                "    \"MOBILE_HALF.WEBP\" : {\n" +
                "      \"url\" : \"https://images.poms.omroep.nl/image/s160/1646895.webp\",\n" +
                "      \"type\" : \"MOBILE_HALF\",\n" +
                "      \"format\" : \"WEBP\",\n" +
                "      \"dimension\" : {\n" +
                "        \"width\" : 160,\n" +
                "        \"height\" : 90\n" +
                "      }\n" +
                "    },\n" +
                "    \"MOBILE.WEBP\" : {\n" +
                "      \"url\" : \"https://images.poms.omroep.nl/image/s320/1646895.webp\",\n" +
                "      \"type\" : \"MOBILE\",\n" +
                "      \"format\" : \"WEBP\",\n" +
                "      \"dimension\" : {\n" +
                "        \"width\" : 320,\n" +
                "        \"height\" : 180\n" +
                "      }\n" +
                "    },\n" +
                "    \"MOBILE_2.WEBP\" : {\n" +
                "      \"url\" : \"https://images.poms.omroep.nl/image/s640%3E/1646895.webp\",\n" +
                "      \"type\" : \"MOBILE_2\",\n" +
                "      \"format\" : \"WEBP\",\n" +
                "      \"dimension\" : {\n" +
                "        \"width\" : 640,\n" +
                "        \"height\" : 360\n" +
                "      }\n" +
                "    },\n" +
                "    \"MOBILE_3.WEBP\" : {\n" +
                "      \"url\" : \"https://images.poms.omroep.nl/image/s960%3E/1646895.webp\",\n" +
                "      \"type\" : \"MOBILE_3\",\n" +
                "      \"format\" : \"WEBP\",\n" +
                "      \"dimension\" : {\n" +
                "        \"width\" : 960,\n" +
                "        \"height\" : 540\n" +
                "      }\n" +
                "    },\n" +
                "    \"TABLET.WEBP\" : {\n" +
                "      \"url\" : \"https://images.poms.omroep.nl/image/s1280%3E/1646895.webp\",\n" +
                "      \"type\" : \"TABLET\",\n" +
                "      \"format\" : \"WEBP\",\n" +
                "      \"dimension\" : {\n" +
                "        \"width\" : 1280,\n" +
                "        \"height\" : 720\n" +
                "      }\n" +
                "    },\n" +
                "    \"TABLET_2.WEBP\" : {\n" +
                "      \"url\" : \"https://images.poms.omroep.nl/image/s1440%3E/1646895.webp\",\n" +
                "      \"type\" : \"TABLET_2\",\n" +
                "      \"format\" : \"WEBP\",\n" +
                "      \"dimension\" : {\n" +
                "        \"width\" : 1440,\n" +
                "        \"height\" : 810\n" +
                "      }\n" +
                "    },\n" +
                "    \"TABLET_3.WEBP\" : {\n" +
                "      \"url\" : \"https://images.poms.omroep.nl/image/s1920%3E/1646895.webp\",\n" +
                "      \"type\" : \"TABLET_3\",\n" +
                "      \"format\" : \"WEBP\",\n" +
                "      \"dimension\" : {\n" +
                "        \"width\" : 1920,\n" +
                "        \"height\" : 1080\n" +
                "      }\n" +
                "    },\n" +
                "    \"MOBILE.JPG\" : {\n" +
                "      \"url\" : \"https://images.poms.omroep.nl/image/s640%3E/1646895.jpg\",\n" +
                "      \"type\" : \"MOBILE\",\n" +
                "      \"format\" : \"JPG\",\n" +
                "      \"dimension\" : {\n" +
                "        \"width\" : 640,\n" +
                "        \"height\" : 360\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"lastModified\" : 1639275163526,\n" +
                "  \"creationDate\" : 1639275163526,\n" +
                "  \"picture\" : {\n" +
                "    \"sources\" : {\n" +
                "      \"image/webp\" : \"https://images.poms.omroep.nl/image/s100/1646895.webp 100w, https://images.poms.omroep.nl/image/s160/1646895.webp 160w, https://images.poms.omroep.nl/image/s320/1646895.webp 320w, https://images.poms.omroep.nl/image/s640%3E/1646895.webp 640w, https://images.poms.omroep.nl/image/s960%3E/1646895.webp 960w, https://images.poms.omroep.nl/image/s1280%3E/1646895.webp 1280w, https://images.poms.omroep.nl/image/s1440%3E/1646895.webp 1440w, https://images.poms.omroep.nl/image/s1920%3E/1646895.webp 1920w\",\n" +
                "      \"image/jpeg\" : \"https://images.poms.omroep.nl/image/s640%3E/1646895.jpg 640w\"\n" +
                "    },\n" +
                "    \"imageSrc\" : \"https://images.poms.omroep.nl/image/s640%3E/1646895.jpg\",\n" +
                "    \"alternative\" : \"2Doc:\",\n" +
                "    \"imageTitle\" : \"2Doc:\",\n" +
                "    \"width\" : 1920,\n" +
                "    \"height\" : 1080,\n" +
                "    \"pointOfInterest\" : \"50% 50%\"\n" +
                "  },\n" +
                "  \"pointOfInterest\" : {\n" +
                "    \"x\" : 50.0,\n" +
                "    \"y\" : 50.0\n" +
                "  },\n" +
                "  \"license\" : \"COPYRIGHTED\",\n" +
                "  \"credits\" : \"Still 2Doc: / Blue Box\",\n" +
                "  \"description\" : \"Filmmaakster Michal Weits duikt in het verleden van haar overgrootvader Joseph Weitz die, met geld van de Blue Box-campagne van het Joods Nationaal Fonds, Palestijns land aankocht en onteigende.\",\n" +
                "  \"alternativeOrTitle\" : \"2Doc:\",\n" +
                "  \"sourceName\" : \"VPRO\"\n" +
                "}");
    }

}
