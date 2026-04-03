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

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static nl.vpro.domain.image.ImageSource.thumbNail;
import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ImageMetadataTest {

    public static final ZoneId ZONE_ID = ZoneId.of("Europe/Amsterdam");

    final ImageMetadataImpl image = ImageMetadata.builder()
        .title("foobar")
        .creationInstant(LocalDateTime.of(2022, 4, 15, 10, 10, 0).atZone(ZONE_ID).toInstant())
        .lastModifiedInstant(LocalDateTime.of(2022, 4, 15, 10, 20, 0).atZone(ZONE_ID).toInstant())
        .type(ImageType.LOGO)
        .imageSource(
            thumbNail("https://www.vpro.nl/plaatje.jpeg")
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
        assertThatJson(image)
            .withoutRemarshalling()
            .isSimilarTo("""
                {
                  "type" : "LOGO",
                  "title" : "foobar",
                  "height" : 200,
                  "width" : 200,
                  "crids" : [ "urn:cinema:1234" ],
                  "areaOfInterest" : {
                    "lowerLeft" : {
                      "x" : 10,
                      "y" : 20
                    },
                    "upperRight" : {
                      "x" : 100,
                      "y" : 120
                    }
                  },
                  "lastModified" : 1650010800000,
                  "creationDate" : 1650010200000
                }""");

        assertThat(image.getSourceSet().toString())
            .isEqualTo("https://www.vpro.nl/plaatje.jpeg 640w, https://www.vpro.nl/plaatje.webp 640w");
    }

    @Test
    @Beta
    public void modelJson() {
        assertThatJson(Jackson2Mapper
            .getModelInstance(), image)
            .withoutRemarshalling()
            .isSimilarTo("""
                {
                  "type" : "LOGO",
                  "title" : "foobar",
                  "height" : 200,
                  "width" : 200,
                  "sourceSet" : {
                    "THUMBNAIL" : {
                      "url" : "https://www.vpro.nl/plaatje.jpeg",
                      "type" : "THUMBNAIL",
                      "dimension" : {
                        "width" : 640,
                        "height" : 320
                      }
                    },
                    "THUMBNAIL.WEBP" : {
                      "url" : "https://www.vpro.nl/plaatje.webp",
                      "type" : "THUMBNAIL",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 640,
                        "height" : 320
                      }
                    }
                  },
                  "crids" : [ "urn:cinema:1234" ],
                  "areaOfInterest" : {
                    "lowerLeft" : {
                      "x" : 10,
                      "y" : 20
                    },
                    "upperRight" : {
                      "x" : 100,
                      "y" : 120
                    }
                  },
                  "lastModified" : 1650010800000,
                  "creationDate" : 1650010200000,
                  "pointOfInterest" : {
                    "x" : 0.275,
                    "y" : 0.35
                  },
                  "alternativeOrTitle" : "foobar"
                }""");

    }

    @Test
    @Beta
    public void modelAndNormalJson() {
        assertThatJson(Jackson2Mapper.getModelAndNormalInstance(), image)
            .withoutRemarshalling()
            .isSimilarTo("""
                {
                  "type" : "LOGO",
                  "title" : "foobar",
                  "height" : 200,
                  "width" : 200,
                  "sourceSet" : {
                    "THUMBNAIL" : {
                      "url" : "https://www.vpro.nl/plaatje.jpeg",
                      "type" : "THUMBNAIL",
                      "dimension" : {
                        "width" : 640,
                        "height" : 320
                      }
                    },
                    "THUMBNAIL.WEBP" : {
                      "url" : "https://www.vpro.nl/plaatje.webp",
                      "type" : "THUMBNAIL",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 640,
                        "height" : 320
                      }
                    }
                  },
                  "crids" : [ "urn:cinema:1234" ],
                  "areaOfInterest" : {
                    "lowerLeft" : {
                      "x" : 10,
                      "y" : 20
                    },
                    "upperRight" : {
                      "x" : 100,
                      "y" : 120
                    }
                  },
                  "lastModified" : 1650010800000,
                  "creationDate" : 1650010200000,
                  "pointOfInterest" : {
                    "x" : 0.275,
                    "y" : 0.35
                  },
                  "alternativeOrTitle" : "foobar"
                }""");

    }

    @Test
    @Beta
    public void modelAndNormalJsonPicture() {
        assertThatJson(Jackson2Mapper.getModelAndNormalInstance(), image.getPicture())
            .withoutRemarshalling()
            .withoutUnmarshalling()
            .isSimilarTo("""
                {
                  "imageSrc" : "https://www.vpro.nl/plaatje.jpeg",
                  "alternative" : "foobar",
                  "imageTitle" : "foobar",
                  "width" : 200,
                  "height" : 200,
                  "sources" : [ {
                    "srcSet" : "https://www.vpro.nl/plaatje.jpeg 640w"
                  }, {
                    "type" : "image/webp",
                    "srcSet" : "https://www.vpro.nl/plaatje.webp 640w"
                  } ],
                  "pointOfInterest" : "28% 35%"
                }""");

    }



    @Test
    public void schema() throws JsonProcessingException {
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(Jackson2Mapper.getModelAndNormalInstance());
        JsonSchema schema = schemaGen.generateSchema(ImageMetadataImpl.class);

        assertThatJson(schema).isSimilarTo(
            """
                {
                  "type" : "object",
                  "id" : "urn:jsonschema:nl:vpro:domain:image:ImageMetadataImpl",
                  "properties" : {
                    "type" : {
                      "type" : "string",
                      "enum" : [ "PICTURE", "PORTRAIT", "STILL", "LOGO", "ICON", "PROMO_LANDSCAPE", "PROMO_PORTRAIT", "BACKGROUND" ]
                    },
                    "title" : {
                      "type" : "string"
                    },
                    "height" : {
                      "type" : "integer"
                    },
                    "width" : {
                      "type" : "integer"
                    },
                    "sourceSet" : {
                      "type" : "object",
                      "additionalProperties" : {
                        "type" : "object",
                        "id" : "urn:jsonschema:nl:vpro:domain:image:ImageSource",
                        "properties" : {
                          "url" : {
                            "type" : "string"
                          },
                          "type" : {
                            "type" : "string",
                            "enum" : [ "THUMBNAIL", "MOBILE_HALF", "MOBILE", "MOBILE_2", "MOBILE_3", "TABLET", "TABLET_2", "TABLET_3", "LARGE" ]
                          },
                          "format" : {
                            "type" : "string",
                            "enum" : [ "BMP", "GIF", "IEF", "IFF", "JPG", "JFIF", "PNG", "PBM", "PGM", "PNM", "PPM", "SVG", "RAS", "RGB", "TIF", "XBM", "XPM", "WEBP", "UNKNOWN" ]
                                                                                                                },
                          "dimension" : {
                            "type" : "object",
                            "id" : "urn:jsonschema:nl:vpro:domain:image:Dimension",
                            "properties" : {
                              "width" : {
                                "type" : "integer"
                              },
                              "height" : {
                                "type" : "integer"
                              }
                            }
                          },
                          "areaOfInterest" : {
                            "type" : "object",
                            "id" : "urn:jsonschema:nl:vpro:domain:image:Area",
                            "properties" : {
                              "lowerLeft" : {
                                "type" : "object",
                                "id" : "urn:jsonschema:nl:vpro:domain:image:Point",
                                "properties" : {
                                  "x" : {
                                    "type" : "integer"
                                  },
                                  "y" : {
                                    "type" : "integer"
                                  }
                                }
                              },
                              "upperRight" : {
                                "type" : "object",
                                "$ref" : "urn:jsonschema:nl:vpro:domain:image:Point"
                              }
                            }
                          }
                        }
                      }
                    },
                    "crids" : {
                      "type" : "array",
                      "items" : {
                        "type" : "string"
                      }
                    },
                    "areaOfInterest" : {
                      "type" : "object",
                      "$ref" : "urn:jsonschema:nl:vpro:domain:image:Area"
                    },
                    "lastModified" : {
                      "type" : "any"
                    },
                    "creationDate" : {
                      "type" : "any"
                    },
                    "description" : {
                      "type" : "string"
                    },
                    "alternative" : {
                      "type" : "string"
                    },
                    "license" : {
                      "type" : "any"
                    },
                    "source" : {
                      "type" : "string"
                    },
                    "sourceName" : {
                      "type" : "string"
                    },
                    "credits" : {
                      "type" : "string"
                    },
                    "pointOfInterest" : {
                      "type" : "object",
                      "id" : "urn:jsonschema:nl:vpro:domain:image:RelativePoint",
                      "properties" : {
                        "x" : {
                          "type" : "number"
                        },
                        "y" : {
                          "type" : "number"
                        }
                      }
                    },
                    "alternativeOrTitle" : {
                      "type" : "string"
                    }
                  }
                }""");

    }

}
