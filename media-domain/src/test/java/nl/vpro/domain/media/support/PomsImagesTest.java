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
        Image support = Jackson2Mapper.getLenientInstance().readValue("""
            {
                  "title": "2Doc:",
                  "description": "Filmmaakster Michal Weits duikt in het verleden van haar overgrootvader Joseph Weitz die, met geld van de Blue Box-campagne van het Joods Nationaal Fonds, Palestijns land aankocht en onteigende.",
                  "imageUri": "urn:vpro:image:1646895",
                  "offset": 180000,
                  "height": 1080,
                  "width": 1920,
                  "credits": "Still 2Doc: / Blue Box",
                  "sourceName": "VPRO",
                  "license": "COPYRIGHTED",
                  "date": "2021",
                  "owner": "AUTHORITY",
                  "type": "STILL",
                  "highlighted": false,
                  "creationDate": 1639275163526,
                  "workflow": "PUBLISHED",
                  "lastModified": 1639275163526,
                  "urn": "urn:vpro:media:image:122414908"
                }""", Image.class);

        ImageMetadata imageMetadata = ImageMetadata.of(support);
        Jackson2TestUtil.assertThatJson(Jackson2Mapper.getModelInstance(), imageMetadata)
            .withoutUnmarshalling()
            .isSimilarTo("""
                {
                  "type" : "STILL",
                  "title" : "2Doc:",
                  "height" : 1080,
                  "width" : 1920,
                  "sourceSet" : {
                    "THUMBNAIL.WEBP" : {
                      "url" : "https://images.poms.omroep.nl/image/s100/1646895.webp",
                      "type" : "THUMBNAIL",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 100,
                        "height" : 56
                      }
                    },
                    "MOBILE_HALF.WEBP" : {
                      "url" : "https://images.poms.omroep.nl/image/s160/1646895.webp",
                      "type" : "MOBILE_HALF",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 160,
                        "height" : 90
                      }
                    },
                    "MOBILE.WEBP" : {
                      "url" : "https://images.poms.omroep.nl/image/s320/1646895.webp",
                      "type" : "MOBILE",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 320,
                        "height" : 180
                      }
                    },
                    "MOBILE_2.WEBP" : {
                      "url" : "https://images.poms.omroep.nl/image/s640%3E/1646895.webp",
                      "type" : "MOBILE_2",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 640,
                        "height" : 360
                      }
                    },
                    "MOBILE_3.WEBP" : {
                      "url" : "https://images.poms.omroep.nl/image/s960%3E/1646895.webp",
                      "type" : "MOBILE_3",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 960,
                        "height" : 540
                      }
                    },
                    "TABLET.WEBP" : {
                      "url" : "https://images.poms.omroep.nl/image/s1280%3E/1646895.webp",
                      "type" : "TABLET",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 1280,
                        "height" : 720
                      }
                    },
                    "TABLET_2.WEBP" : {
                      "url" : "https://images.poms.omroep.nl/image/s1440%3E/1646895.webp",
                      "type" : "TABLET_2",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 1440,
                        "height" : 810
                      }
                    },
                    "TABLET_3.WEBP" : {
                      "url" : "https://images.poms.omroep.nl/image/s1920%3E/1646895.webp",
                      "type" : "TABLET_3",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 1920,
                        "height" : 1080
                      }
                    },
                    "MOBILE.JPG" : {
                      "url" : "https://images.poms.omroep.nl/image/s640%3E/1646895.jpg",
                      "type" : "MOBILE",
                      "format" : "JPG",
                      "dimension" : {
                        "width" : 640,
                        "height" : 360
                      }
                    }
                  },
                  "lastModified" : 1639275163526,
                  "creationDate" : 1639275163526,
                  "pointOfInterest" : {
                    "x" : 0.5,
                    "y" : 0.5
                  },
                  "license" : "COPYRIGHTED",
                  "credits" : "Still 2Doc: / Blue Box",
                  "description" : "Filmmaakster Michal Weits duikt in het verleden van haar overgrootvader Joseph Weitz die, met geld van de Blue Box-campagne van het Joods Nationaal Fonds, Palestijns land aankocht en onteigende.",
                  "alternativeOrTitle" : "2Doc:",
                  "sourceName" : "VPRO"
                }""");
    }

}
