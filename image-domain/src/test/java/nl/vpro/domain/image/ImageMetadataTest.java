package nl.vpro.domain.image;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.google.common.annotations.Beta;

import nl.vpro.domain.support.License;
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
        .imageSource(
            thumbNail("https://www.vpro.nl/plaatje.jpeg")
                .format(ImageFormat.JPG)
                .dimension(Dimension.of(640, 320))
                .build()
        )
        .imageSource(
            thumbNail("https://www.vpro.nl/plaatje.webp")
                .dimension(Dimension.of(640, 320))
                .format(ImageFormat.WEBP)
                .build()
        )
        .areaOfInterest(new Area(10, 20, 100, 120))
        .dimensions(200, 150)
        .crid("urn:cinema:1234")
        .license(License.CC_BY)
        .sourceName("VPRO")
        .source("https://www.vpro.nl")
        .credits("Pietje Puk")
        .build();



    @Test
    @Disabled("no xml binding for now , probably nobody is interested, and without we can make the object better read-only. (could be fixed with jakarta/jaxb?")
    public void xml() {
        JAXBTestUtil.roundTripAndSimilar(image, "<a />");
    }

    @Test
    public void json() {
        Jackson2TestUtil.roundTripAndSimilar(image, "{\n" +
            "  \"type\" : \"LOGO\",\n" +
            "  \"title\" : \"foobar\",\n" +
            "  \"height\" : 150,\n" +
            "  \"width\" : 200,\n" +
            "  \"sourceSet\" : {\n" +
            "    \"THUMBNAIL.JPG\" : {\n" +
            "      \"url\" : \"https://www.vpro.nl/plaatje.jpeg\",\n" +
            "      \"type\" : \"THUMBNAIL\",\n" +
            "      \"format\" : \"JPG\",\n" +
            "      \"dimension\" : {\n" +
            "        \"width\" : 640,\n" +
            "        \"height\" : 320\n" +
            "      }\n" +
            "    },\n" +
            "    \"THUMBNAIL.WEBP\" : {\n" +
            "      \"url\" : \"https://www.vpro.nl/plaatje.webp\",\n" +
            "      \"type\" : \"THUMBNAIL\",\n" +
            "      \"format\" : \"WEBP\",\n" +
            "      \"dimension\" : {\n" +
            "        \"width\" : 640,\n" +
            "        \"height\" : 320\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"crids\" : [ \"urn:cinema:1234\" ],\n" +
            "  \"areaOfInterest\" : {\n" +
            "    \"lowerLeft\" : {\n" +
            "      \"x\" : 10,\n" +
            "      \"y\" : 20\n" +
            "    },\n" +
            "    \"upperRight\" : {\n" +
            "      \"x\" : 100,\n" +
            "      \"y\" : 120\n" +
            "    }\n" +
            "  },\n" +
            "  \"lastModified\" : 1650010800000,\n" +
            "  \"creationDate\" : 1650010200000,\n" +
            "  \"license\" : \"CC_BY\",\n" +
            "  \"source\" : \"https://www.vpro.nl\",\n" +
            "  \"sourceName\" : \"VPRO\",\n" +
            "  \"credits\" : \"Pietje Puk\"\n" +
            "}");

        assertThat(image.getSourceSet().toString()).isEqualTo("https://www.vpro.nl/plaatje.jpeg 640w, https://www.vpro.nl/plaatje.webp 640w");
    }

    @Test
    @Beta
    public void modelJson() {
        Jackson2TestUtil.assertThatJson(Jackson2Mapper.getModelInstance(), image).withoutRemarshalling().isSimilarTo("{\n" +
            "    \"type\" : \"LOGO\",\n" +
            "    \"title\" : \"foobar\",\n" +
            "    \"height\" : 150,\n" +
            "    \"width\" : 200,\n" +
            "    \"sourceSetString\" : \"https://www.vpro.nl/plaatje.jpeg 640w, https://www.vpro.nl/plaatje.webp 640w\",\n" +
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
            "    \"creationDate\" : 1650010200000,\n" +
            "    \"license\" : \"CC_BY\",\n" +
            "    \"source\" : \"https://www.vpro.nl\",\n" +
            "    \"sourceName\" : \"VPRO\",\n" +
            "    \"credits\" : \"Pietje Puk\",\n" +
            "    \"picture\" : {\n" +
            "      \"sources\" : {\n" +
            "        \"image/jpeg\" : \"https://www.vpro.nl/plaatje.jpeg 640w\",\n" +
            "        \"image/webp\" : \"https://www.vpro.nl/plaatje.webp 640w\"\n" +
            "      },\n" +
            "      \"imageSrc\" : \"https://www.vpro.nl/plaatje.jpeg\",\n" +
            "      \"alternative\" : \"foobar\",\n" +
            "      \"width\" : 200,\n" +
            "      \"height\" : 150,\n" +
            "      \"pointOfInterest\" : \"28% 47%\"\n" +
            "    },\n" +
            "    \"alternativeOrTitle\" : \"foobar\"\n" +
            "  }");

    }

    @Test
    @Beta
    public void modelAndNormalJson() {
        Jackson2TestUtil.roundTripAndSimilar(Jackson2Mapper.getModelAndNormalInstance(), image, "{\n" +
            "  \"type\" : \"LOGO\",\n" +
            "  \"title\" : \"foobar\",\n" +
            "  \"height\" : 150,\n" +
            "  \"width\" : 200,\n" +
            "  \"sourceSet\" : {\n" +
            "    \"THUMBNAIL.JPG\" : {\n" +
            "      \"url\" : \"https://www.vpro.nl/plaatje.jpeg\",\n" +
            "      \"type\" : \"THUMBNAIL\",\n" +
            "      \"format\" : \"JPG\",\n" +
            "      \"dimension\" : {\n" +
            "        \"width\" : 640,\n" +
            "        \"height\" : 320\n" +
            "      }\n" +
            "    },\n" +
            "    \"THUMBNAIL.WEBP\" : {\n" +
            "      \"url\" : \"https://www.vpro.nl/plaatje.webp\",\n" +
            "      \"type\" : \"THUMBNAIL\",\n" +
            "      \"format\" : \"WEBP\",\n" +
            "      \"dimension\" : {\n" +
            "        \"width\" : 640,\n" +
            "        \"height\" : 320\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"sourceSetString\" : \"https://www.vpro.nl/plaatje.jpeg 640w, https://www.vpro.nl/plaatje.webp 640w\",\n" +
            "  \"crids\" : [ \"urn:cinema:1234\" ],\n" +
            "  \"areaOfInterest\" : {\n" +
            "    \"lowerLeft\" : {\n" +
            "      \"x\" : 10,\n" +
            "      \"y\" : 20\n" +
            "    },\n" +
            "    \"upperRight\" : {\n" +
            "      \"x\" : 100,\n" +
            "      \"y\" : 120\n" +
            "    }\n" +
            "  },\n" +
            "  \"lastModified\" : 1650010800000,\n" +
            "  \"creationDate\" : 1650010200000,\n" +
            "  \"license\" : \"CC_BY\",\n" +
            "  \"source\" : \"https://www.vpro.nl\",\n" +
            "  \"sourceName\" : \"VPRO\",\n" +
            "  \"credits\" : \"Pietje Puk\",\n" +
            "  \"picture\" : {\n" +
            "    \"sources\" : {\n" +
            "      \"image/jpeg\" : \"https://www.vpro.nl/plaatje.jpeg 640w\",\n" +
            "      \"image/webp\" : \"https://www.vpro.nl/plaatje.webp 640w\"\n" +
            "    },\n" +
            "    \"imageSrc\" : \"https://www.vpro.nl/plaatje.jpeg\",\n" +
            "    \"alternative\" : \"foobar\",\n" +
            "    \"width\" : 200,\n" +
            "    \"height\" : 150,\n" +
            "    \"pointOfInterest\" : \"28% 47%\"\n" +
            "  },\n" +
            "  \"alternativeOrTitle\" : \"foobar\"\n" +
            "}");

    }



    @Test
    public void schema() throws JsonProcessingException {
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(Jackson2Mapper.getInstance());
        JsonSchema schema = schemaGen.generateSchema(ImageMetadataImpl.class);

        Jackson2TestUtil.assertThatJson(schema).isSimilarTo(
            // text blocks only in java 15....
            "{\n" +
                "  \"type\" : \"object\",\n" +
                "  \"id\" : \"urn:jsonschema:nl:vpro:domain:image:ImageMetadataImpl\",\n" +
                "  \"properties\" : {\n" +
                "    \"type\" : {\n" +
                "      \"type\" : \"string\",\n" +
                "      \"enum\" : [ \"PICTURE\", \"PORTRAIT\", \"STILL\", \"LOGO\", \"ICON\", \"PROMO_LANDSCAPE\", \"PROMO_PORTRAIT\", \"BACKGROUND\" ]\n" +
                "    },\n" +
                "    \"title\" : {\n" +
                "      \"type\" : \"string\"\n" +
                "    },\n" +
                "    \"height\" : {\n" +
                "      \"type\" : \"integer\"\n" +
                "    },\n" +
                "    \"width\" : {\n" +
                "      \"type\" : \"integer\"\n" +
                "    },\n" +
                "    \"sourceSet\" : {\n" +
                "      \"type\" : \"object\",\n" +
                "      \"additionalProperties\" : {\n" +
                "        \"type\" : \"object\",\n" +
                "        \"id\" : \"urn:jsonschema:nl:vpro:domain:image:ImageSource\",\n" +
                "        \"properties\" : {\n" +
                "          \"url\" : {\n" +
                "            \"type\" : \"string\"\n" +
                "          },\n" +
                "          \"type\" : {\n" +
                "            \"type\" : \"string\",\n" +
                "            \"enum\" : [ \"THUMBNAIL\", \"MOBILE_HALF\", \"MOBILE\", \"MOBILE_2\", \"MOBILE_3\", \"TABLET\", \"TABLET_2\", \"TABLET_3\", \"LARGE\" ]\n" +
                "          },\n" +
                "          \"format\" : {\n" +
                "            \"type\" : \"string\",\n" +
                "            \"enum\" : [ \"BMP\", \"GIF\", \"IEF\", \"IFF\", \"JPG\", \"JFIF\", \"PNG\", \"PBM\", \"PGM\", \"PNM\", \"PPM\", \"SVG\", \"RAS\", \"RGB\", \"TIF\", \"XBM\", \"XPM\", \"WEBP\" ]\n" +
                "          },\n" +
                "          \"dimension\" : {\n" +
                "            \"type\" : \"object\",\n" +
                "            \"id\" : \"urn:jsonschema:nl:vpro:domain:image:Dimension\",\n" +
                "            \"properties\" : {\n" +
                "              \"width\" : {\n" +
                "                \"type\" : \"integer\"\n" +
                "              },\n" +
                "              \"height\" : {\n" +
                "                \"type\" : \"integer\"\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          \"areaOfInterest\" : {\n" +
                "            \"type\" : \"object\",\n" +
                "            \"id\" : \"urn:jsonschema:nl:vpro:domain:image:Area\",\n" +
                "            \"properties\" : {\n" +
                "              \"lowerLeft\" : {\n" +
                "                \"type\" : \"object\",\n" +
                "                \"id\" : \"urn:jsonschema:nl:vpro:domain:image:Point\",\n" +
                "                \"properties\" : {\n" +
                "                  \"x\" : {\n" +
                "                    \"type\" : \"integer\"\n" +
                "                  },\n" +
                "                  \"y\" : {\n" +
                "                    \"type\" : \"integer\"\n" +
                "                  }\n" +
                "                }\n" +
                "              },\n" +
                "              \"upperRight\" : {\n" +
                "                \"type\" : \"object\",\n" +
                "                \"$ref\" : \"urn:jsonschema:nl:vpro:domain:image:Point\"\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"crids\" : {\n" +
                "      \"type\" : \"array\",\n" +
                "      \"items\" : {\n" +
                "        \"type\" : \"string\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"areaOfInterest\" : {\n" +
                "      \"type\" : \"object\",\n" +
                "      \"$ref\" : \"urn:jsonschema:nl:vpro:domain:image:Area\"\n" +
                "    },\n" +
                "    \"lastModified\" : {\n" +
                "      \"type\" : \"any\"\n" +
                "    },\n" +
                "    \"description\" : {\n" +
                "      \"type\" : \"string\"\n" +
                "    },\n" +
                "    \"alternative\" : {\n" +
                "      \"type\" : \"string\"\n" +
                "    },\n" +
                "    \"license\" : {\n" +
                "      \"type\" : \"any\"\n" +
                "    },\n" +
                "    \"source\" : {\n" +
                "      \"type\" : \"string\"\n" +
                "    },\n" +
                "    \"sourceName\" : {\n" +
                "      \"type\" : \"string\"\n" +
                "    },\n" +
                "    \"credits\" : {\n" +
                "      \"type\" : \"string\"\n" +
                "    },\n" +
                "    \"creationDate\" : {\n" +
                "      \"type\" : \"any\"\n" +
                "    }\n" +
                "  }\n" +
                "}");
    }

    @Test
    public void picture() throws JsonProcessingException {
        Picture picture =  image.getSourceSet().getPictureMetadata();
        Jackson2TestUtil.assertJsonEquals( "{\n" +
            "  \"type\" : \"LOGO\",\n" +
            "  \"title\" : \"foobar\",\n" +
            "  \"height\" : 150,\n" +
            "  \"width\" : 200,\n" +
            "  \"lastModifiedInstant\" : 1650010800000,\n" +
            "  \"creationInstant\" : 1650010200000,\n" +
            "  \"sources\" : {\n" +
            "    \"image/jpeg\" : \"https://www.vpro.nl/plaatje.jpeg 640w\",\n" +
            "    \"image/webp\" : \"https://www.vpro.nl/plaatje.webp 640w\"\n" +
            "  },\n" +
            "  \"imageSrc\" : \"https://www.vpro.nl/plaatje.jpeg\",\n" +
            "  \"alternative\" : \"foobar\",\n" +
            "  \"source\" : \"https://www.vpro.nl\",\n" +
            "  \"license\" : \"CC_BY\",\n" +
            "  \"credits\" : \"Pietje Puk\",\n" +
            "  \"sourceName\" : \"VPRO\",\n" +
            "  \"pointOfInterest\" : \"28% 47%\"\n" +
            "}", Jackson2Mapper.getInstance().writeValueAsString(picture));
    }
}
