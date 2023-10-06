package nl.vpro.domain.image;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.domain.image.backend.BackendImage;
import nl.vpro.jackson2.Jackson2Mapper;

import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;


@Slf4j
class ImageSourceServiceTest {

    static Map<ImageSource.Type, Dimension> DIMENSIONS = new HashMap<>();
    static {
        DIMENSIONS.put(ImageSource.Type.THUMBNAIL, Dimension.of(100, 100));
        DIMENSIONS.put(ImageSource.Type.MOBILE, Dimension.of(200, 200));
        DIMENSIONS.put(ImageSource.Type.LARGE, Dimension.of(300, 300));

        ImageUrlServiceHolder.setInstance(new AbsoluteImageUrlServiceImpl("https://images.poms.omroep.nl/"));
    }


    public static class SomeImageThatCantImplementMetadata implements MetadataSupplier {

        long getId() {
            return 1;
        }

        @Override
        public @NonNull Metadata getMetadata() {
            return ImageMetadataImpl.builder()
                .areaOfInterest(new Area(0, 0, 10, 10))
                .dimension(Dimension.of(100, 200))
                .title("Test")
                .build();
        }


    }

    public static class SomeCreator implements  ImageSourceCreator<SomeImageThatCantImplementMetadata> {

        @Override
        public Optional<ImageSource> createFor(SomeImageThatCantImplementMetadata imageThatCantImplementMetadata, Metadata imageMetadata, ImageSource.Key type) {
            return
                Optional.ofNullable(DIMENSIONS.get(type.getType())).map(dim ->

                    ImageSource.of(type)
                        .url("https://bla/" + type.getShortName() + "/" + imageThatCantImplementMetadata.getId() + "." + type.getFormat().getFileExtension())
                        .dimension(dim)
                        .build());
        }
    }


    /**
     * How it could work with some object that is an image, but only implements {@link MetadataSupplier}
     */
    @Test
    void forSomeSupplier() {
        MetadataSupplier supplier = new SomeImageThatCantImplementMetadata();

        ImageSourceSet sourceSet = ImageSourceService.INSTANCE.getSourceSet(supplier);
        assertThatJson(sourceSet)
            .isSimilarTo("""
                {
                  "THUMBNAIL.WEBP" : {
                    "url" : "https://bla/TN.W/1.webp",
                    "type" : "THUMBNAIL",
                    "format" : "WEBP",
                    "dimension" : {
                      "width" : 100,
                      "height" : 100
                    }
                  },
                  "MOBILE.WEBP" : {
                    "url" : "https://bla/M1.W/1.webp",
                    "type" : "MOBILE",
                    "format" : "WEBP",
                    "dimension" : {
                      "width" : 200,
                      "height" : 200
                    }
                  },
                  "LARGE.WEBP" : {
                    "url" : "https://bla/L1.W/1.webp",
                    "type" : "LARGE",
                    "format" : "WEBP",
                    "dimension" : {
                      "width" : 300,
                      "height" : 300
                    }
                  },
                  "MOBILE.JPG" : {
                    "url" : "https://bla/M1.J/1.jpg",
                    "type" : "MOBILE",
                    "format" : "JPG",
                    "dimension" : {
                      "width" : 200,
                      "height" : 200
                    }
                  }
                }""");

        ImageMetadata metadata = ImageMetadata.of(supplier);
        assertThatJson(Jackson2Mapper.getModelInstance(), metadata)
            .withoutRemarshalling()
            .isSimilarTo("""
                {
                  "title" : "Test",
                  "height" : 200,
                  "width" : 100,
                  "sourceSet" : {
                    "THUMBNAIL.WEBP" : {
                      "url" : "https://bla/TN.W/1.webp",
                      "type" : "THUMBNAIL",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 100,
                        "height" : 100
                      }
                    },
                    "MOBILE.WEBP" : {
                      "url" : "https://bla/M1.W/1.webp",
                      "type" : "MOBILE",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 200,
                        "height" : 200
                      }
                    },
                    "LARGE.WEBP" : {
                      "url" : "https://bla/L1.W/1.webp",
                      "type" : "LARGE",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 300,
                        "height" : 300
                      }
                    },
                    "MOBILE.JPG" : {
                      "url" : "https://bla/M1.J/1.jpg",
                      "type" : "MOBILE",
                      "format" : "JPG",
                      "dimension" : {
                        "width" : 200,
                        "height" : 200
                      }
                    }
                  },
                  "areaOfInterest" : {
                    "lowerLeft" : {
                      "x" : 0,
                      "y" : 0
                    },
                    "upperRight" : {
                      "x" : 10,
                      "y" : 10
                    }
                  },
                  "pointOfInterest" : {
                    "x" : 0.05,
                    "y" : 0.025
                  },
                  "alternativeOrTitle" : "Test"
                }""");
    }


    /**
     *  A {@link BackendImage} properly implements {@link Metadata} itself.
     */

    @Test
    void forBackendImage() throws JsonProcessingException {
        BackendImage  image = BackendImage.builder()
            .imageFormat(ImageFormat.JPG)
            .title("bla")
            .height(100)
            .width(200)
            .build();
        image.setCreationInstant(Instant.ofEpochMilli(1675851618171L));
        image.setId(123L);

        log.info("{}", Jackson2Mapper.getPrettyInstance().writeValueAsString(image));
        ImageMetadata imageMetadata = ImageMetadata.of(image);
        imageMetadata.getSourceSet().forEach((k, v) -> log.info("{} {}", k, v));
        assertThatJson(Jackson2Mapper.getModelInstance(), imageMetadata)
            .withoutRemarshalling()
            .isSimilarTo("""
                {
                  "title" : "bla",
                  "height" : 100,
                  "width" : 200,
                  "sourceSet" : {
                    "THUMBNAIL.WEBP" : {
                      "url" : "https://images.poms.omroep.nl/s100/123.webp",
                      "type" : "THUMBNAIL",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 100,
                        "height" : 50
                      }
                    },
                    "MOBILE_HALF.WEBP" : {
                      "url" : "https://images.poms.omroep.nl/s160/123.webp",
                      "type" : "MOBILE_HALF",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 160,
                        "height" : 80
                      }
                    },
                    "MOBILE.WEBP" : {
                      "url" : "https://images.poms.omroep.nl/s320/123.webp",
                      "type" : "MOBILE",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 320,
                        "height" : 160
                      }
                    },
                    "MOBILE_2.WEBP" : {
                      "url" : "https://images.poms.omroep.nl/s640%3E/123.webp",
                      "type" : "MOBILE_2",
                      "format" : "WEBP",
                      "dimension" : {
                        "width" : 200,
                        "height" : 100
                      }
                    },
                    "MOBILE.JPG" : {
                      "url" : "https://images.poms.omroep.nl/s640%3E/123.jpg",
                      "type" : "MOBILE",
                      "format" : "JPG",
                      "dimension" : {
                        "width" : 200,
                        "height" : 100
                      }
                    }
                  },
                  "creationDate" : 1675851618171,
                  "pointOfInterest" : {
                    "x" : 0.5,
                    "y" : 0.5
                  },
                  "alternativeOrTitle" : "bla"
                }""");
    }

}
