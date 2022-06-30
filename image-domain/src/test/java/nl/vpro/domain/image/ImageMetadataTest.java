package nl.vpro.domain.image;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static nl.vpro.domain.image.ImageSource.thumbNail;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ImageMetadataTest {
    public static final ZoneId ZONE_ID = ZoneId.of("Europe/Amsterdam");



    final ImageMetadata image = ImageMetadata.builder()
        .title("foobar")
        .creationInstant(LocalDateTime.of(2022, 4, 15, 10, 10, 0).atZone(ZONE_ID).toInstant())
        .lastModifiedInstant(LocalDateTime.of(2022, 4, 15, 10, 20, 0).atZone(ZONE_ID).toInstant())
        .type(ImageType.LOGO)
        .imageSource(thumbNail("https://www.vpro.nl/plaatje.jpeg").dimension(Dimension.of(640, 320)).build())
        .areaOfInterest(new Area(10, 20, 100, 120))
        .dimensions(200, 200)
        .crid("urn:cinema:1234")
        .build();



    @Test
    @Disabled("no xml binding for now , probably nobody is interested, and without we can make the object better read-only. (could be fixed with jakarta/jaxb?")
    public void xml() {
        JAXBTestUtil.roundTripAndSimilar(image, "<a />");
    }

    @Test
    public void json() {
        Jackson2TestUtil.roundTripAndSimilar(image, "{\n" +
            "    \"type\" : \"LOGO\",\n" +
            "    \"title\" : \"foobar\",\n" +
            "    \"height\" : 200,\n" +
            "    \"width\" : 200,\n" +
            "    \"sourceSet\" : {\n" +
            "      \"THUMBNAIL\" : {\n" +
            "        \"url\" : \"https://www.vpro.nl/plaatje.jpeg\",\n" +
            "        \"type\" : \"THUMBNAIL\",\n" +
            "        \"dimension\" : {\n" +
            "          \"width\" : 640,\n" +
            "          \"height\" : 320\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"crids\" : [ \"urn:cinema:1234\" ],\n" +
            "    \"areaOfInterest\" : {\n" +
            "      \"lowerLeft\" : {\n" +
            "        \"x\" : 10,\n" +
            "        \"y\" : 20\n" +
            "      },\n" +
            "      \"upperRight\" : {\n" +
            "        \"x\" : 100,\n" +
            "        \"y\" : 120\n" +
            "      }\n" +
            "    },\n" +
            "    \"lastModified\" : 1650010800000,\n" +
            "    \"creationDate\" : 1650010200000\n" +
            "  }");

        assertThat(image.getSourceSet().toString()).isEqualTo("https://www.vpro.nl/plaatje.jpeg w640");
    }

    @Test
    public void schema() throws JsonProcessingException {
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(Jackson2Mapper.getInstance());
        JsonSchema schema = schemaGen.generateSchema(ImageMetadataImpl.class);

        Jackson2TestUtil.assertThatJson(schema).isSimilarTo(
            // text blocks only in java 15....
            "{\n" +
                "    \"type\" : \"object\",\n" +
                "    \"id\" : \"urn:jsonschema:nl:vpro:domain:images:ImageMetadataImpl\",\n" +
                "    \"properties\" : {\n" +
                "      \"type\" : {\n" +
                "        \"type\" : \"string\",\n" +
                "        \"enum\" : [ \"PICTURE\", \"PORTRAIT\", \"STILL\", \"LOGO\", \"ICON\", \"PROMO_LANDSCAPE\", \"PROMO_PORTRAIT\", \"BACKGROUND\" ]\n" +
                "      },\n" +
                "      \"title\" : {\n" +
                "        \"type\" : \"string\"\n" +
                "      },\n" +
                "      \"description\" : {\n" +
                "        \"type\" : \"string\"\n" +
                "      },\n" +
                "      \"alternative\" : {\n" +
                "        \"type\" : \"string\"\n" +
                "      },\n" +
                "      \"license\" : {\n" +
                "        \"type\" : \"any\"\n" +
                "      },\n" +
                "      \"source\" : {\n" +
                "        \"type\" : \"string\"\n" +
                "      },\n" +
                "      \"sourceName\" : {\n" +
                "        \"type\" : \"string\"\n" +
                "      },\n" +
                "      \"credits\" : {\n" +
                "        \"type\" : \"string\"\n" +
                "      },\n" +
                "      \"height\" : {\n" +
                "        \"type\" : \"integer\"\n" +
                "      },\n" +
                "      \"width\" : {\n" +
                "        \"type\" : \"integer\"\n" +
                "      },\n" +
                "      \"sourceSet\" : {\n" +
                "        \"type\" : \"object\",\n" +
                "        \"additionalProperties\" : {\n" +
                "          \"type\" : \"object\",\n" +
                "          \"id\" : \"urn:jsonschema:nl:vpro:domain:images:ImageSource\",\n" +
                "          \"properties\" : {\n" +
                "            \"url\" : {\n" +
                "              \"type\" : \"string\"\n" +
                "            },\n" +
                "            \"type\" : {\n" +
                "              \"type\" : \"string\",\n" +
                "              \"enum\" : [ \"THUMBNAIL\", \"MOBILE\", \"TABLET\", \"LARGE\" ]\n" +
                "            },\n" +
                "            \"dimension\" : {\n" +
                "              \"type\" : \"object\",\n" +
                "              \"id\" : \"urn:jsonschema:nl:vpro:domain:images:Dimension\",\n" +
                "              \"properties\" : {\n" +
                "                \"width\" : {\n" +
                "                  \"type\" : \"integer\"\n" +
                "                },\n" +
                "                \"height\" : {\n" +
                "                  \"type\" : \"integer\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"areaOfInterest\" : {\n" +
                "              \"type\" : \"object\",\n" +
                "              \"id\" : \"urn:jsonschema:nl:vpro:domain:images:Area\",\n" +
                "              \"properties\" : {\n" +
                "                \"lowerLeft\" : {\n" +
                "                  \"type\" : \"object\",\n" +
                "                  \"id\" : \"urn:jsonschema:nl:vpro:domain:images:Point\",\n" +
                "                  \"properties\" : {\n" +
                "                    \"x\" : {\n" +
                "                      \"type\" : \"integer\"\n" +
                "                    },\n" +
                "                    \"y\" : {\n" +
                "                      \"type\" : \"integer\"\n" +
                "                    }\n" +
                "                  }\n" +
                "                },\n" +
                "                \"upperRight\" : {\n" +
                "                  \"type\" : \"object\",\n" +
                "                  \"$ref\" : \"urn:jsonschema:nl:vpro:domain:images:Point\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"crids\" : {\n" +
                "        \"type\" : \"array\",\n" +
                "        \"items\" : {\n" +
                "          \"type\" : \"string\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"areaOfInterest\" : {\n" +
                "        \"type\" : \"object\",\n" +
                "        \"$ref\" : \"urn:jsonschema:nl:vpro:domain:images:Area\"\n" +
                "      },\n" +
                "      \"lastModified\" : {\n" +
                "        \"type\" : \"any\"\n" +
                "      },\n" +
                "      \"creationDate\" : {\n" +
                "        \"type\" : \"any\"\n" +
                "      }\n" +
                "    }\n" +
                "  }");

    }

}
