package nl.vpro.domain.media.support;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.domain.image.ImageMetadata;
import nl.vpro.domain.image.ImageMetadataProvider;
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
        ImageMetadata imageMetadata = ImageMetadataProvider.of(support).toImageMetadataWithSourceSet();
        Jackson2TestUtil.roundTripAndSimilarAndEquals(imageMetadata, "{\n" +
            "  \"type\" : \"STILL\",\n" +
            "  \"title\" : \"2Doc:\",\n" +
            "  \"description\" : \"Filmmaakster Michal Weits duikt in het verleden van haar overgrootvader Joseph Weitz die, met geld van de Blue Box-campagne van het Joods Nationaal Fonds, Palestijns land aankocht en onteigende.\",\n" +
            "  \"license\" : \"COPYRIGHTED\",\n" +
            "  \"sourceName\" : \"VPRO\",\n" +
            "  \"credits\" : \"Still 2Doc: / Blue Box\",\n" +
            "  \"height\" : 1080,\n" +
            "  \"width\" : 1920, \n" +
            "  \"sourceSet\" : {\n" +
            "    \"THUMBNAIL\" : {\n" +
            "      \"url\" : \"https://images.poms.omroep.nl/image/s100/1646895\",\n" +
            "      \"type\" : \"THUMBNAIL\",\n" +
            "      \"dimension\" : {\n" +
            "        \"width\" : 100\n" +
            "      }\n" +
            "    },\n" +
            "    \"MOBILE\" : {\n" +
            "      \"url\" : \"https://images.poms.omroep.nl/image/s200/1646895\",\n" +
            "      \"type\" : \"MOBILE\",\n" +
            "      \"dimension\" : {\n" +
            "        \"width\" : 200\n" +
            "      }\n" +
            "    },\n" +
            "    \"TABLET\" : {\n" +
            "      \"url\" : \"https://images.poms.omroep.nl/image/s300/1646895\",\n" +
            "      \"type\" : \"TABLET\",\n" +
            "      \"dimension\" : {\n" +
            "        \"width\" : 300\n" +
            "      }\n" +
            "    },\n" +
            "    \"LARGE\" : {\n" +
            "      \"url\" : \"https://images.poms.omroep.nl/image/s400/1646895\",\n" +
            "      \"type\" : \"LARGE\",\n" +
            "      \"dimension\" : {\n" +
            "        \"width\" : 400\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"lastModified\" : 1639275163526,\n" +
            "  \"creationDate\" : 1639275163526\n" +
            "}");
    }

}
